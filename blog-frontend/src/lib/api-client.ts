import { getAccessToken } from "./auth";

// --- Types ---

interface FetchOptions extends Omit<RequestInit, "body"> {
  headers?: HeadersInit;
  data?: unknown; // Request body payload
}

interface ApiResponse<T> {
  data: T;
  status: number;
  headers: Headers;
}

// --- Helpers ---

const getBody = async <T>(c: Response): Promise<T> => {
  // Handle 204 No Content
  if (c.status === 204) return null as T;

  const contentType = c.headers.get("content-type");

  if (contentType?.includes("application/json")) {
    return c.json();
  }

  if (contentType?.includes("application/pdf")) {
    return c.blob() as unknown as T;
  }

  return c.text() as unknown as T;
};

const getAuthHeaders = async (
  existingHeaders?: HeadersInit
): Promise<Headers> => {
  const headers = new Headers(existingHeaders);
  const accessToken = await getAccessToken();

  if (accessToken) {
    headers.set("Authorization", `Bearer ${accessToken}`);
  }

  return headers;
};

// --- Core Logic ---

/**
 * The internal fetcher that handles auth, URL construction, and raw execution.
 */
const coreFetch = async <T>(
  endpoint: string,
  options: FetchOptions = {}
): Promise<ApiResponse<T>> => {
  const { data, headers: customHeaders, ...customConfig } = options;

  const headers = await getAuthHeaders(customHeaders);

  // Automatically handle JSON body and Content-Type
  let body: BodyInit | undefined;
  if (data) {
    body = JSON.stringify(data);
    if (!headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
  }

  const baseUrl = process.env.NEXT_PUBLIC_API_URL || "";
  // Ensure we don't end up with double slashes (e.g., api.com//users)
  const cleanUrl = `${baseUrl.replace(/\/$/, "")}/${endpoint.replace(
    /^\//,
    ""
  )}`;

  const config: RequestInit = {
    ...customConfig,
    headers,
    body,
    // Default cache strategy (can be overridden in options)
    cache: customConfig.cache || "no-store",
  };

  const response = await fetch(cleanUrl, config);

  if (!response.ok) {
    // You might want to parse the error body here to get a specific error message from your API
    throw new Error(`API Error: ${response.status} ${response.statusText}`);
  }

  const responseData = await getBody<T>(response);

  return {
    data: responseData,
    status: response.status,
    headers: response.headers,
  };
};

// --- Exports ---

/**
 * Used for Client Components.
 * Returns the full response object { data, status, headers }.
 */
export const clientFetch = async <T>(
  url: string,
  options?: FetchOptions
): Promise<ApiResponse<T>> => {
  // --- 🕒 ARTIFICIAL DELAY (2 Seconds) ---
  // This pauses execution here for 2000ms before fetching data
  await new Promise((resolve) => setTimeout(resolve, 2000));
  return coreFetch<T>(url, options);
};

/**
 * Used for Server Components.
 * Returns ONLY the data T directly.
 * Matches your original signature: `serverFetch({ url: "...", data: ... })`
 */
export const serverFetch = async <T>(
  options: FetchOptions & { url: string }
): Promise<T> => {
  const { url, ...fetchOptions } = options;
  const response = await coreFetch<T>(url, fetchOptions);
  return response.data;
};

import { getAccessToken } from "./auth";

// --- Types ---

interface FetchOptions extends Omit<RequestInit, "body"> {
  headers?: HeadersInit;
  data?: unknown;
  // 1. ADD THIS LINE: Allow params in the interface
  params?: Record<string, string | number | boolean | undefined | null>;
}

interface ApiResponse<T> {
  data: T;
  status: number;
  headers: Headers;
}

// --- Helpers ---

const getBody = async <T>(c: Response): Promise<T> => {
  if (c.status === 204) return null as T;
  const contentType = c.headers.get("content-type");
  if (contentType?.includes("application/json")) return c.json();
  if (contentType?.includes("application/pdf")) return c.blob() as unknown as T;
  return c.text() as unknown as T;
};

const getAuthHeaders = async (
  existingHeaders?: HeadersInit
): Promise<Headers> => {
  const headers = new Headers(existingHeaders);
  const accessToken = await getAccessToken();
  if (accessToken) headers.set("Authorization", `Bearer ${accessToken}`);
  return headers;
};

// --- Core Logic ---

const coreFetch = async <T>(
  endpoint: string,
  options: FetchOptions = {}
): Promise<ApiResponse<T>> => {
  // 2. EXTRACT PARAMS HERE
  const { data, headers: customHeaders, params, ...customConfig } = options;

  const headers = await getAuthHeaders(customHeaders);

  let body: BodyInit | undefined;
  if (data) {
    body = JSON.stringify(data);
    if (!headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
  }

  const baseUrl = process.env.NEXT_PUBLIC_API_URL || "";
  let cleanUrl = `${baseUrl.replace(/\/$/, "")}/${endpoint.replace(/^\//, "")}`;

  // 3. APPEND QUERY PARAMETERS HERE
  if (params) {
    const searchParams = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
      // Filter out null/undefined so we don't send "?page=undefined"
      if (value !== undefined && value !== null) {
        searchParams.append(key, String(value));
      }
    });

    const queryString = searchParams.toString();
    if (queryString) {
      // Check if URL already has '?', append accordingly
      cleanUrl += (cleanUrl.includes("?") ? "&" : "?") + queryString;
    }
  }

  const config: RequestInit = {
    ...customConfig,
    headers,
    body,
    cache: customConfig.cache || "no-store",
  };

  const response = await fetch(cleanUrl, config);

  if (!response.ok) {
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

export const clientFetch = async <T>(
  url: string,
  options?: FetchOptions
): Promise<ApiResponse<T>> => {
  await new Promise((resolve) => setTimeout(resolve, 2000));
  return coreFetch<T>(url, options);
};

export const serverFetch = async <T>(
  options: FetchOptions & { url: string }
): Promise<T> => {
  const { url, ...fetchOptions } = options;
  // Now valid: fetchOptions contains 'params' and coreFetch will handle it
  const response = await coreFetch<T>(url, fetchOptions);
  return response.data;
};

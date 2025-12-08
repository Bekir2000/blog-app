import { getAccessToken } from "./auth";

// --- Custom Error Class ---
// This allows us to pass the backend JSON to the UI
export class ApiError extends Error {
  response: {
    data: any;
    status: number;
    statusText: string;
  };

  constructor(message: string, data: any, status: number, statusText: string) {
    super(message);
    this.name = "ApiError";
    this.response = {
      data,
      status,
      statusText,
    };
  }
}

// --- Types ---

interface FetchOptions extends Omit<RequestInit, "body"> {
  headers?: HeadersInit;
  data?: unknown;
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
  options: any = {}
): Promise<ApiResponse<T>> => {
  const { data, headers: customHeaders, params, ...customConfig } = options;

  const headers = await getAuthHeaders(customHeaders);

  let body: BodyInit | undefined;
  if (data) {
    body = JSON.stringify(data);
    if (!headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
  } else if (options.body) {
    body = options.body;
    if (!headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
  }

  const baseUrl = process.env.NEXT_PUBLIC_API_URL || "";
  let cleanUrl = `${baseUrl.replace(/\/$/, "")}/${endpoint.replace(/^\//, "")}`;

  if (params) {
    const searchParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        searchParams.append(key, String(value));
      }
    });
    const queryString = searchParams.toString();
    if (queryString) {
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

  // --- CHANGED LOGIC START ---
  if (!response.ok) {
    // 1. Try to parse the error body (JSON)
    const errorBody = await getBody<any>(response).catch(() => null);

    // 2. Throw our custom error with the data attached
    throw new ApiError(
      errorBody?.message ||
        `API Error: ${response.status} ${response.statusText}`,
      errorBody,
      response.status,
      response.statusText
    );
  }
  // --- CHANGED LOGIC END ---

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
  options?: any
): Promise<ApiResponse<T>> => {
  // Simulating delay is fine for dev, consider removing for prod
  // await new Promise((resolve) => setTimeout(resolve, 2000));

  // NOTE: Orval expects the return type to just be the data usually,
  // but your coreFetch returns { data, status, headers }.
  // If Orval behaves weirdly, you might need to return `response.data` here.
  // For now, keeping your structure:
  return coreFetch<T>(url, options);
};

export const serverFetch = async <T>(
  options: FetchOptions & { url: string }
): Promise<T> => {
  const { url, ...fetchOptions } = options;
  const response = await coreFetch<T>(url, fetchOptions);
  return response.data;
};

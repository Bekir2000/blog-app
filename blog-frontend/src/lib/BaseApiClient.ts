// src/lib/BaseApiClient.ts
import { ApiError } from "./ApiError";

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

interface RequestOptions {
  body?: any;
  headers?: HeadersInit;
}

export class BaseApiClient {
  constructor(private baseUrl: string) {
    this.baseUrl = baseUrl || process.env.NEXT_PUBLIC_API_URL || "";
  }

  protected async request<R = unknown>(
    path: string,
    method: HttpMethod,
    options: RequestOptions = {}
  ): Promise<R> {
    const res = await fetch(`${this.baseUrl}${path}`, {
      method,
      headers: {
        "Content-Type": "application/json",
        ...(options.headers ?? {}),
      },
      body: options.body ? JSON.stringify(options.body) : undefined,
      credentials: "include",
    });

    if (!res.ok) {
      const text = await res.text();
      throw new ApiError(res.status, text, `HTTP ${res.status}: ${text}`);
    }

    return res.json() as Promise<R>;
  }

  get<R>(path: string, options?: RequestOptions) {
    return this.request<R>(path, "GET", options);
  }
  post<R>(path: string, body?: any, options?: RequestOptions) {
    return this.request<R>(path, "POST", { ...options, body });
  }
  put<R>(path: string, body?: any, options?: RequestOptions) {
    return this.request<R>(path, "PUT", { ...options, body });
  }
  delete<R>(path: string, options?: RequestOptions) {
    return this.request<R>(path, "DELETE", options);
  }
}

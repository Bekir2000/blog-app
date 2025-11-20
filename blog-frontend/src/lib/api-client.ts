"use server";

import { getAccessToken } from "./auth";

export const serverFetch = async <T>({
  url,
  method,
  params,
  data,
}: {
  url: string;
  method: string;
  params?: any;
  data?: any;
}): Promise<T> => {
  const BASE_URL = process.env.API_URL || "http://localhost:8080";
  const access_token = await getAccessToken();

  const headers = new Headers();
  headers.set("Content-Type", "application/json");
  if (access_token) {
    headers.set("Authorization", `Bearer ${access_token}`);
  }

  const queryString = params
    ? "?" + new URLSearchParams(params).toString()
    : "";

  const response = await fetch(`${BASE_URL}${url}${queryString}`, {
    method,
    headers,
    body: data ? JSON.stringify(data) : undefined,
    // You can default to 'no-store' to ensure real-time data in Server Components
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error(`API Error: ${response.status}`);
  }

  if (response.status === 204) {
    return null as T;
  }

  return response.json() as Promise<T>;
};

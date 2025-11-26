"use server";

import { getAccessToken } from "@/lib/auth";

export async function performApiRequest(
  url: string,
  method: string,
  params?: any,
  data?: any
): Promise<{ data: any; status: number; headers: Record<string, string> }> {
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

  console.log(`${method} Request to: ${BASE_URL}${url}${queryString}`);

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
    return { data: null, status: 204, headers: {} };
  }

  const json = await response.json();

  const responseHeaders: Record<string, string> = {};
  response.headers.forEach((value, key) => {
    responseHeaders[key] = value;
  });

  return { data: json, status: response.status, headers: responseHeaders };
}

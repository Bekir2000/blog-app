import { AuthApi } from "@/features/auth/api";
import { cookies } from "next/headers";
import { BaseApiClient } from "./BaseApiClient";

export class AuthApiClient extends BaseApiClient {
  constructor(baseUrl: string) {
    super(baseUrl);
  }

  private async getValidToken(): Promise<string> {
    const cookieStore = await cookies();
    const accessToken = cookieStore.get("access_token")?.value;
    if (!accessToken) {
      throw new Error("No access token found");
    }
    return accessToken;
  }

  private async withAuth<R>(fn: (token: string) => Promise<R>): Promise<R> {
    let token = await this.getValidToken();

    try {
      return await fn(token);
    } catch (err: any) {
      if (err.message?.startsWith("HTTP 401")) {
        const authResponse = await AuthApi.refreshAccessToken();
        const newToken = authResponse?.token;
        if (!newToken) throw err;

        const cookieStore = await cookies();
        cookieStore.set("access_token", newToken, {
          httpOnly: true,
          secure: true,
          sameSite: "strict",
          path: "/",
        });

        token = newToken;
        return await fn(token); // retry once
      }
      throw err;
    }
  }

  private withBearer<R>(fn: (headers: HeadersInit) => Promise<R>): Promise<R> {
    return this.withAuth((token) => fn({ Authorization: `Bearer ${token}` }));
  }

  override get<R>(path: string, options: { headers?: HeadersInit } = {}) {
    return this.withBearer((authHeader) =>
      super.get<R>(path, {
        ...options,
        headers: { ...options.headers, ...authHeader },
      })
    );
  }

  override post<R>(
    path: string,
    body?: any,
    options: { headers?: HeadersInit } = {}
  ) {
    return this.withBearer((authHeader) =>
      super.post<R>(path, body, {
        ...options,
        headers: { ...options.headers, ...authHeader },
      })
    );
  }

  override put<R>(
    path: string,
    body?: any,
    options: { headers?: HeadersInit } = {}
  ) {
    return this.withBearer((authHeader) =>
      super.put<R>(path, body, {
        ...options,
        headers: { ...options.headers, ...authHeader },
      })
    );
  }

  override delete<R>(path: string, options: { headers?: HeadersInit } = {}) {
    return this.withBearer((authHeader) =>
      super.delete<R>(path, {
        ...options,
        headers: { ...options.headers, ...authHeader },
      })
    );
  }
}

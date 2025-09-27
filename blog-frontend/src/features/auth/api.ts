// features/auth/api.ts
import { ApiClients } from "@/lib/ApiClients";
import { ApiError } from "@/lib/ApiError"; // if you added custom error
import { cookies } from "next/headers";
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  UserResponse,
} from "./type";

export const AuthApi = {
  login: (data: LoginRequest) =>
    ApiClients.public.post<AuthResponse>("/auth/login", data),

  refreshAccessToken: () =>
    ApiClients.public.post<AuthResponse>("/auth/refresh-access"),

  register: (data: RegisterRequest) =>
    ApiClients.public.post<UserResponse>("/auth/register", data),

  async getCurrentUser(): Promise<UserResponse | null> {
    const cookieStore = await cookies();
    const token = cookieStore.get("access_token")?.value;
    if (!token) return null; // No token → no user
    try {
      return await ApiClients.private.get<UserResponse>("/auth/me");
    } catch (err: any) {
      // If unauthorized → just return null
      if (err instanceof ApiError && err.status === 401) {
        return null;
      }
      // Optional: also swallow 403 (forbidden)
      if (err instanceof ApiError && err.status === 403) {
        return null;
      }
      // Anything else → rethrow
      throw err;
    }
  },

  logout: () => ApiClients.public.post<void>("/auth/logout", {}),
};

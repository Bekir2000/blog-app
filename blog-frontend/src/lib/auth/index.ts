// lib/auth/index.ts
"use server";

import {
  login,
  logout,
  refreshAccess,
  register,
} from "@/api/generated/auth-controller/auth-controller";
import { getProfile } from "@/api/generated/me-controller/me-controller";
import { UserResponse } from "@/api/generated/model";
import { expiresAtMs } from "./jwt";
import {
  clearAuthCookies,
  getAccessToken,
  getRefreshToken,
  setAuthCookies,
} from "./session";

const API_URL = process.env.API_URL;
if (!API_URL) {
  throw new Error("Missing environment variable: API_URL");
}

/**
 * Helper: Standard JSON POST options
 */
function createFetchOptions(body: Record<string, unknown>): RequestInit {
  return {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  };
}

export async function signUp(
  email: string,
  password: string
): Promise<boolean> {
  try {
    await register({
      email,
      password,
    });

    return true;
  } catch (error) {
    console.error("Registration error:", error);
    return false;
  }
}

export async function signIn(
  email: string,
  password: string
): Promise<boolean> {
  try {
    const { accessToken, refreshToken, expiresIn } = await login({
      email,
      password,
    });

    // Validate tokens
    if (
      !accessToken ||
      !refreshToken ||
      !expiresAtMs(accessToken) ||
      !expiresAtMs(refreshToken)
    ) {
      console.error("Invalid tokens received during login");
      return false;
    }

    await setAuthCookies(accessToken, refreshToken);
    return true;
  } catch (error) {
    console.error("Login error:", error);
    return false;
  }
}

export async function signOut(): Promise<void> {
  await logout();
  await clearAuthCookies();
}

export async function getUser(): Promise<UserResponse | null> {
  const accessToken = await getAccessToken();
  if (!accessToken) return null;
  const user = await getProfile();
  return user;
}

export async function refreshAccessToken(): Promise<boolean> {
  const refreshToken = await getRefreshToken();

  if (!refreshToken) return false;

  try {
    const data = await refreshAccess({
      refreshToken,
    });

    const newAccessToken = data.accessToken;
    const newRefreshToken = data.refreshToken; // Optional, API might rotate it

    if (!newAccessToken || !expiresAtMs(newAccessToken)) {
      return false;
    }

    // If the API returned a new refresh token, use it. Otherwise, keep the old one.
    await setAuthCookies(newAccessToken, newRefreshToken || refreshToken);

    return true;
  } catch (error) {
    console.error("Refresh error:", error);
    await signOut();
    return false;
  }
}

// Re-export getAccessToken if client components need it via Server Actions
export { getAccessToken };

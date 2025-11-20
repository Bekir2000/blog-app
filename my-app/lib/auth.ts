"use server";
import { cookies } from "next/headers";

// 1. Safer Environment Variable handling
const API_URL = process.env.API_URL;
if (!API_URL) {
  throw new Error("Missing environment variable: API_URL");
}

/**
 * Decodes the expiration time from a JWT.
 * Added error handling to prevent crashes from malformed tokens.
 * @returns Expiration timestamp in seconds, or null if decoding fails.
 */
function decodeExp(token: string): number | null {
  try {
    const [, payloadBase64] = token.split(".");
    if (!payloadBase64) return null; // Handle invalid token structure
    const payload = JSON.parse(Buffer.from(payloadBase64, "base64").toString());
    return typeof payload.exp === "number" ? payload.exp : null;
  } catch (error) {
    console.error("Failed to decode token:", error);
    return null;
  }
}

/**
 * Helper to create standard fetch options for JSON POST requests.
 */
function createFetchOptions(body: Record<string, unknown>): RequestInit {
  return {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  };
}

/**
 * Sets a secure, HTTP-only cookie.
 */
async function setAuthCookie(
  name: "access_token" | "refresh_token",
  value: string
) {
  const cookieStore = await cookies();
  cookieStore.set({
    name,
    value,
    httpOnly: true,
    path: "/",
    secure: process.env.NODE_ENV === "production", // Add secure flag in prod
  });
}

/**
 * Attempts to log in a user and set auth cookies.
 * @returns true on success, false on failure.
 */
export async function login(email: string, password: string): Promise<boolean> {
  try {
    // 2. Removed `await` from `cookies()`
    const cookieStore = cookies();

    // 3. Using the helper for consistency
    const url = `${API_URL}/auth/login`;
    const response = await fetch(
      url, // Note: This URL is unusual for a login
      createFetchOptions({ email, password })
    );

    if (!response.ok) return false;

    const { accessToken, refreshToken } = await response.json();
    if (!accessToken || !refreshToken) return false; // Validate payload

    // 4. Added validation for decoded expirations
    const accessExp = decodeExp(accessToken);
    const refreshExp = decodeExp(refreshToken);

    // If decoding fails, login fails
    if (!accessExp || !refreshExp) {
      console.error("Failed to decode tokens during login.");
      return false;
    }

    // 5. Removed unused 'now' variable

    // 6. Using helper function to set cookies
    setAuthCookie("access_token", accessToken);
    setAuthCookie("refresh_token", refreshToken);

    return true;
  } catch (error) {
    console.error("Login failed:", error);
    return false;
  }
}

/**
 * Logs the user out by deleting auth cookies.
 */
export async function logout(): Promise<void> {
  // 2. Removed `await` from `cookies()`
  const cookieStore = await cookies();
  cookieStore.delete("access_token");
  cookieStore.delete("refresh_token");
  console.log("User logged out");
}

export async function getUser(): Promise<{
  id: string;
  username: string;
  firstname: string;
  lastname: string;
  profileImageUrl: string;
} | null> {
  // 2. Removed `await` from `cookies()`
  const cookieStore = await cookies();
  const accessToken = cookieStore.get("access_token")?.value;

  if (!accessToken) {
    return null;
  }

  try {
    const response = await fetch(`${API_URL}/me`, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });

    if (!response.ok) {
      return null;
    }

    const user = await response.json();
    return user;
  } catch (error) {
    console.error("Failed to fetch user:", error);
    return null;
  }
}

/**
 * Attempts to refresh the access token using the refresh token.
 * @returns true on success, false on failure.
 */
export async function refreshAccessToken(): Promise<boolean> {
  // 2. Removed `await` from `cookies()`
  const cookieStore = await cookies();

  // 7. Fixed cookie name to match what was set in login
  const refreshToken = cookieStore.get("refresh_token")?.value;

  if (!refreshToken) {
    return false;
  }

  try {
    const response = await fetch(
      `${API_URL}/auth/refresh-access`,
      createFetchOptions({ refreshToken }) // 8. Used defined helper
    );

    if (!response.ok) {
      // If refresh fails (e.g., token expired), log the user out
      if (response.status === 401 || response.status === 403) {
        await logout();
      }
      return false;
    }

    // 9. --- Start: Added logic to actually *use* the new tokens ---
    const { accessToken, refreshToken: newRefreshToken } =
      await response.json();

    if (!accessToken) {
      console.error("Refresh response missing new access token.");
      return false;
    }

    const accessExp = decodeExp(accessToken);
    if (!accessExp) {
      console.error("Failed to decode new access token.");
      return false;
    }

    // Set the new access token
    setAuthCookie("access_token", accessToken);

    // Best practice: A refresh endpoint may return a new refresh token.
    // If it does, update the refresh token cookie as well.
    if (newRefreshToken) {
      const refreshExp = decodeExp(newRefreshToken);
      if (refreshExp) {
        setAuthCookie("refresh_token", newRefreshToken);
      }
    }
    // 9. --- End: Added logic ---

    return true;
  } catch (error) {
    console.error("Token refresh failed:", error);
    // If an error occurs (e.g., network), log the user out
    await logout();
    return false;
  }
}

import { refreshAccessToken } from "@/lib/auth";
import { NextRequest, NextResponse } from "next/server";

const REFRESH_COOKIE_NAME = "auth-refresh-in-progress";
/**
 * Handles GET requests to refresh the user's access token.
 * It expects 'access_token' and 'refresh_token' cookies.
 * If successful, it redirects the user back to their original destination ('returnTo' param).
 * If it fails (missing tokens, refresh error), it logs the user out and redirects to the login page.
 */
export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;

  const returnToParam = searchParams.get("returnTo");
  let returnTo = "/"; // Default redirect

  if (returnToParam) {
    // SECURITY: Open Redirect Vulnerability Check
    // Ensure 'returnTo' is a relative path starting with '/' and not '//'
    // This prevents redirects to external domains.
    if (returnToParam.startsWith("/") && !returnToParam.startsWith("//")) {
      returnTo = returnToParam;
    } else {
      console.warn(
        `Blocked potentially unsafe redirect attempt to: "${returnToParam}"`
      );
      // Invalid or unsafe path, will use the default "/"
    }
  }

  const accessToken = request.cookies.get("access_token")?.value;
  const refreshToken = request.cookies.get("refresh_token")?.value;

  // Helper function to create a logout response
  const createLogoutResponse = () => {
    const loginUrl = new URL("/auth/login", request.url);
    const response = NextResponse.redirect(loginUrl);
    // Ensure all auth-related cookies are cleared on logout
    response.cookies.delete("access_token");
    response.cookies.delete("refresh_token");
    response.cookies.delete(REFRESH_COOKIE_NAME);
    return response;
  };

  if (!accessToken || !refreshToken) {
    console.log("Missing access or refresh token, redirecting to login.");
    return createLogoutResponse();
  }

  try {
    // Attempt to refresh the access token using the logic from @/lib/auth
    // This function is assumed to handle setting the new cookies internally.
    const refreshResult = await refreshAccessToken();

    if (!refreshResult) {
      // This case handles if refreshAccessToken returns a falsy value (e.g., null, undefined)
      // without throwing an error, indicating a controlled failure (e.g., invalid refresh token).
      console.error(
        "Failed to refresh tokens (refreshAccessToken returned falsy)."
      );
      return createLogoutResponse();
    }

    // On successful refresh, redirect back to the originally intended page
    const destinationUrl = new URL(returnTo, request.url);
    const response = NextResponse.redirect(destinationUrl);
    response.cookies.delete(REFRESH_COOKIE_NAME);
    return response;
  } catch (error) {
    // This catches any unexpected errors during the refresh process
    console.error("Unexpected error during token refresh:", error);
    return createLogoutResponse();
  }
}

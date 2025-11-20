// lib/auth/middleware.ts
import { NextRequest, NextResponse } from "next/server";
import { expiresAtMs } from "./jwt";

const REFRESH_COOKIE_NAME = "auth-refresh-in-progress";
const REFRESH_BUFFER_MS = 30 * 1000; // 30 seconds

function buildReturnUrl(nextUrl: URL) {
  const search = new URLSearchParams(nextUrl.search);
  return nextUrl.pathname + (search.toString() ? `?${search.toString()}` : "");
}

function redirectToRefresh(nextUrl: URL, returnUrl: string) {
  const refreshUrl = `/api/auth/refresh?returnTo=${encodeURIComponent(
    returnUrl
  )}`;
  const response = NextResponse.redirect(new URL(refreshUrl, nextUrl));

  // Set a short-lived cookie to prevent infinite refresh loops
  response.cookies.set(REFRESH_COOKIE_NAME, "true", {
    maxAge: 30,
    path: "/",
    httpOnly: true,
    sameSite: "strict",
  });
  return response;
}

/**
 * MAIN MIDDLEWARE LOGIC
 */
export function authMiddleware(request: NextRequest) {
  const nextUrl = request.nextUrl;

  // Use request.cookies directly (standard for middleware)
  const accessToken = request.cookies.get("access_token")?.value;
  const refreshToken = request.cookies.get("refresh_token")?.value;

  // 1. No access token? Let Next.js handle the route (e.g. show login page or 401)
  if (!accessToken) return NextResponse.next();

  const now = Date.now();
  const accessExpiryMs = expiresAtMs(accessToken);
  const refreshExpiryMs = refreshToken ? expiresAtMs(refreshToken) : null;

  // 2. Refresh token missing or expired? -> Force global logout
  if (refreshExpiryMs === null || refreshExpiryMs < now) {
    return NextResponse.redirect(new URL("/api/auth/logout", nextUrl));
  }

  // 3. Access token unreadable? -> Try to refresh (unless loop detected)
  if (accessExpiryMs === null) {
    if (!request.cookies.has(REFRESH_COOKIE_NAME)) {
      const returnUrl = buildReturnUrl(nextUrl);
      return redirectToRefresh(nextUrl, returnUrl);
    }
    return NextResponse.next();
  }

  // 4. Access token expiring soon? -> Try to refresh (unless loop detected)
  if (
    accessExpiryMs < now + REFRESH_BUFFER_MS &&
    !request.cookies.has(REFRESH_COOKIE_NAME)
  ) {
    const returnUrl = buildReturnUrl(nextUrl);
    return redirectToRefresh(nextUrl, returnUrl);
  }

  return NextResponse.next();
}

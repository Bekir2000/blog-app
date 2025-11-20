import { cookies } from "next/headers";
import type { NextRequest } from "next/server";
import { NextResponse } from "next/server";

const REFRESH_COOKIE_NAME = "auth-refresh-in-progress";

/**
 * Decode the `exp` claim (seconds since epoch) from a JWT payload.
 * Returns null on any parsing/structure error.
 */
function decodeJwtExp(token: string): number | null {
  try {
    const parts = token.split(".");
    if (parts.length < 2) return null;
    const payloadBase64 = parts[1];
    const payloadJson = Buffer.from(payloadBase64, "base64").toString();
    const payload = JSON.parse(payloadJson);
    return typeof payload.exp === "number" ? payload.exp : null;
  } catch {
    return null;
  }
}

/** Return expiration time in milliseconds, or null if it cannot be determined. */
function expiresAtMs(token: string): number | null {
  const exp = decodeJwtExp(token);
  return exp === null ? null : exp * 1000;
}

function buildReturnUrl(nextUrl: URL) {
  const search = new URLSearchParams(nextUrl.search);
  return nextUrl.pathname + (search.toString() ? `?${search.toString()}` : "");
}

function redirectToRefresh(nextUrl: URL, returnUrl: string) {
  const refreshUrl = `/api/auth/refresh?returnTo=${encodeURIComponent(
    returnUrl
  )}`;
  const response = NextResponse.redirect(new URL(refreshUrl, nextUrl));
  response.cookies.set(REFRESH_COOKIE_NAME, "true", {
    maxAge: 30,
    path: "/",
    httpOnly: true,
    sameSite: "strict",
  });
  return response;
}

export async function proxy(request: NextRequest) {
  const nextUrl = request.nextUrl;
  const cookieStore = await cookies();
  const accessToken = cookieStore.get("access_token")?.value;
  const refreshToken = cookieStore.get("refresh_token")?.value;

  // If there's no access token, allow the request to continue (routing/SSR will handle auth pages)
  if (!accessToken) return NextResponse.next();

  const now = Date.now();

  // Determine expirations (null = couldn't decode)
  const accessExpiryMs = expiresAtMs(accessToken);
  const refreshExpiryMs = refreshToken ? expiresAtMs(refreshToken) : null;

  // If refresh token is missing/invalid/expired -> force logout
  if (refreshExpiryMs === null || refreshExpiryMs < now) {
    return NextResponse.redirect(new URL("/api/auth/logout", nextUrl));
  }

  // If access token cannot be decoded, treat as expired and attempt refresh
  if (accessExpiryMs === null) {
    if (!request.cookies.has(REFRESH_COOKIE_NAME)) {
      const returnUrl = buildReturnUrl(nextUrl);
      return redirectToRefresh(nextUrl, returnUrl);
    }
    return NextResponse.next();
  }

  // If access token expired -> attempt refresh (unless already in progress)
  if (accessExpiryMs < now && !request.cookies.has(REFRESH_COOKIE_NAME)) {
    const returnUrl = buildReturnUrl(nextUrl);
    return redirectToRefresh(nextUrl, returnUrl);
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico (favicon file)
     */
    "/((?!api|_next/static|_next/image|favicon.ico).*)",
  ],
};

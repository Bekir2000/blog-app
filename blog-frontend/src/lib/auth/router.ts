// lib/auth/router.ts
import { refreshAccessToken, signOut } from "@/lib/auth"; // Your core auth logic
import { NextRequest, NextResponse } from "next/server";

const COOKIE_NAMES = [
  "access_token",
  "refresh_token",
  "auth-refresh-in-progress",
];

/**
 * Helper: Clears cookies and redirects to login
 */
function createLogoutResponse(request: NextRequest) {
  const loginUrl = new URL("/auth/login", request.url);
  const res = NextResponse.redirect(loginUrl);

  COOKIE_NAMES.forEach((name) => {
    res.cookies.delete(name);
  });

  return res;
}

/**
 * Action: LOGOUT
 */
async function handleLogout(request: NextRequest) {
  await signOut();
  return createLogoutResponse(request);
}

/**
 * Action: REFRESH
 */
async function handleRefresh(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const returnToParam = searchParams.get("returnTo");
  let returnTo = "/";

  // Validate redirect target
  if (
    returnToParam &&
    returnToParam.startsWith("/") &&
    !returnToParam.startsWith("//")
  ) {
    returnTo = returnToParam;
  }

  const accessToken = request.cookies.get("access_token")?.value;
  const refreshToken = request.cookies.get("refresh_token")?.value;

  if (!accessToken || !refreshToken) {
    return createLogoutResponse(request);
  }

  try {
    const refreshResult = await refreshAccessToken();

    if (!refreshResult) {
      return createLogoutResponse(request);
    }

    const destinationUrl = new URL(returnTo, request.url);
    const res = NextResponse.redirect(destinationUrl);
    res.cookies.delete("auth-refresh-in-progress");
    return res;
  } catch (err) {
    console.error("Refresh error:", err);
    return createLogoutResponse(request);
  }
}

/**
 * MAIN HANDLER
 * This matches the Next.js Route Handler signature
 */
export async function authHandler(
  request: NextRequest,
  // 1. Update Type: params is now a Promise
  context: { params: Promise<{ auth: string[] }> }
) {
  // 2. Await the params promise
  const { auth } = await context.params;

  // 3. Now you can destructure the array
  const [route] = auth;

  switch (route) {
    case "logout":
      return handleLogout(request); // Make sure handleLogout is defined in this file or imported
    case "refresh":
      return handleRefresh(request); // Make sure handleRefresh is defined in this file or imported
    default:
      return NextResponse.json(
        { error: "Unknown auth route" },
        { status: 404 }
      );
  }
}

import { logout } from "@/lib/auth";
import { NextRequest, NextResponse } from "next/server";

/**
 * Logout endpoint
 * - Clears common auth cookies (token/session/next-auth)
 * - Redirects to /auth/login
 *
 * Supports GET and POST.
 */

const COOKIE_NAMES = ["access_token", "refresh_token"];

function clearAuthCookies(res: NextResponse) {
  for (const name of COOKIE_NAMES) {
    // set an expired cookie so browsers remove it
    res.cookies.delete(name);
  }
  return res;
}

async function handleLogout(request: NextRequest) {
  await logout();
  const loginUrl = new URL("/auth/login", request.url);
  const res = NextResponse.redirect(loginUrl);
  return clearAuthCookies(res);
}

export async function GET(request: NextRequest) {
  return handleLogout(request);
}

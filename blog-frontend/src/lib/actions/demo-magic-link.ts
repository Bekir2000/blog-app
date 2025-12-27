"use server";

import { signIn } from "@/lib/auth";

/**
 * Handles the "Magic Link" logic for the HR Demo.
 * Validates environment variables and attempts to sign in the guest user.
 */
export async function performDemoLogin() {
  const email = process.env.DEMO_ACCOUNT_EMAIL;
  const password = process.env.DEMO_ACCOUNT_PASSWORD;

  // Security Check: Fail if env vars are missing
  if (!email || !password) {
    console.error("Missing DEMO credentials in environment variables.");
    return false;
  }

  return await signIn(email, password);
}

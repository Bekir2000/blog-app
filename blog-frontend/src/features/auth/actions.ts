"use server";

import { AuthApi } from "@/features/auth/api";
import { AuthResponse, LoginRequest } from "@/features/auth/type";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";

export async function loginAction(values: LoginRequest): Promise<string> {
  const data: AuthResponse = await AuthApi.login(values);
  console.log("Login response:", data);

  // store token in secure cookie
  const cookieStore = await cookies();
  cookieStore.set("access_token", data.token, {
    httpOnly: true,
    secure: true,
    sameSite: "strict",
    path: "/",
  });
  redirect("/"); // Redirect after successful login
}

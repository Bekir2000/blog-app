"use client";

import LoginForm from "@/components/auth/LoginForm";
import { performDemoLogin } from "@/lib/actions/demo-magic-link";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function LoginPage() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [isAutoLoggingIn, setIsAutoLoggingIn] = useState(false);

  useEffect(() => {
    const handleDemoLogin = async () => {
      // 1. Check for the magic query param (e.g. ?demo=hr)
      const demoType = searchParams.get("demo");

      if (demoType === "hr") {
        setIsAutoLoggingIn(true);

        try {
          // 2. Call the Server Action
          // No username/password passed here - keeps it hidden from the browser
          const success = await performDemoLogin();

          if (success) {
            // 3. If true, redirect to dashboard
            router.push("/");
            router.refresh(); // Ensure cookies update the UI
          } else {
            // If login failed, show the form
            console.error(
              "Auto-login failed: Invalid credentials or server error"
            );
            setIsAutoLoggingIn(false);
          }
        } catch (error) {
          console.error("Auto-login error:", error);
          setIsAutoLoggingIn(false);
        }
      }
    };

    handleDemoLogin();
  }, [searchParams, router]);

  // 4. Show a loading spinner while logging in so they don't see the form
  if (isAutoLoggingIn) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-white">
        <div className="text-center space-y-4">
          {/* Simple CSS Spinner */}
          <div className="animate-spin h-10 w-10 border-4 border-indigo-600 border-t-transparent rounded-full mx-auto"></div>

          <h3 className="text-xl font-semibold text-gray-800">
            Accessing Demo Environment...
          </h3>
          <p className="text-sm text-gray-500">
            Please wait while we log you in securely.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="w-full max-w-md space-y-8 rounded-lg border p-8 shadow-md">
        <div className="space-y-2 text-center">
          <h2 className="text-2xl font-bold">Sign in to your account</h2>
          <p className="text-sm text-gray-600">
            Welcome back! Please enter your details.
          </p>
        </div>

        {/* Your existing standard login form */}
        <LoginForm />

        <p className="mt-4 text-center text-sm text-gray-600">
          Don't have an account?{" "}
          <Link
            href="/register"
            className="font-medium text-indigo-600 hover:text-indigo-500"
          >
            Sign up
          </Link>
        </p>
      </div>
    </div>
  );
}

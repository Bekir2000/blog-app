"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import * as z from "zod";
// 💡 Import the Sonner toast function
import { toast } from "sonner";

import { LoginRequest } from "@/api/generated/model";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { signIn } from "@/lib/auth";

const loginSchema = z.object({
  email: z.email(),
  password: z.string().min(1),
}) satisfies z.ZodType<LoginRequest>;

export default function LoginForm() {
  const router = useRouter();

  const form = useForm<z.infer<typeof loginSchema>>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  async function onSubmit(values: LoginRequest) {
    const { email, password } = values;

    if (!email || !password) {
      toast.error("Please provide both email and password.", {
        description: "Both fields are required to log in.",
      });
      return;
    }

    try {
      // 1. Attempt to sign in using NextAuth.js credentials provider
      const isLoggedIn = await signIn(email, password);

      // 2. Handle the result
      if (!isLoggedIn) {
        // Login failed: Use Sonner's error toast
        toast.error("Invalid email or password.", {
          description: "Please check your credentials and try again.",
        });
      } else {
        // Login successful: Use Sonner's success toast
        toast.success("Login Successful!", {
          description: "You are now signed in.",
        });

        router.push("/");
        router.refresh();
      }
    } catch (error) {
      console.error("Login error:", error);
      // Fallback error message
      toast.error("An unexpected error occurred during login.", {
        description: "Please check your network connection.",
      });
    }
  }

  const isSubmitting = form.formState.isSubmitting;

  return (
    <Card>
      <CardHeader>
        <CardTitle>Login</CardTitle>
        <CardDescription>Sign in to your account</CardDescription>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Email</FormLabel>
                  <FormControl>
                    <Input
                      type="email"
                      placeholder="you@example.com"
                      {...field}
                      disabled={isSubmitting}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Password</FormLabel>
                  <FormControl>
                    <Input
                      type="password"
                      placeholder="********"
                      {...field}
                      disabled={isSubmitting}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <CardFooter>
              <Button type="submit" className="w-full" disabled={isSubmitting}>
                {isSubmitting ? "Logging in..." : "Log in"}
              </Button>
            </CardFooter>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
}

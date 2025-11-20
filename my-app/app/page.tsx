import { getUser } from "@/lib/auth";
import Link from "next/link";

export default async function Home() {
  const user = (await getUser()) || null;
  return (
    <div className="flex items-center justify-center h-screen bg-white">
      <main className="text-center text-black space-y-4">
        <h1 className="text-3xl font-bold">Welcome to My Next.js App</h1>
        {user ? (
          <p className="text-lg">Hello, {user.username}!</p>
        ) : (
          <p className="text-lg">You are not logged in.</p>
        )}
        <Link
          href="/auth/login"
          className="inline-block px-6 py-2 text-white bg-blue-600 rounded hover:bg-blue-700 transition"
        >
          Login
        </Link>
      </main>
    </div>
  );
}

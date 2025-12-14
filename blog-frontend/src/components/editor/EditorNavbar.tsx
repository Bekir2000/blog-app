"use client";

import { UserResponse } from "@/api/generated/model";
import { UserNav } from "@/components/navbar/UserNav"; // Reusing your existing component
import { Button } from "@/components/ui/button";
import { MoreHorizontal } from "lucide-react";
import Link from "next/link";
import { Avatar, AvatarFallback } from "../ui/avatar";

interface EditorNavbarProps {
  user: UserResponse | null;
  isPublishing: boolean;
  onPublish: () => void;
  canPublish: boolean;
}

export function EditorNavbar({
  user,
  isPublishing,
  onPublish,
  canPublish,
}: EditorNavbarProps) {
  return (
    <nav className="flex items-center justify-between px-6 py-3 border-b border-gray-100 dark:border-gray-800">
      <div className="flex items-center gap-4">
        <Link href="/" className="text-2xl font-serif font-bold tracking-tight">
          Blogium
        </Link>
        <span className="text-sm text-gray-500">Draft in {user?.username}</span>
      </div>

      <div className="flex items-center gap-4">
        <Button
          onClick={onPublish}
          disabled={!canPublish || isPublishing}
          className="rounded-full bg-green-600 hover:bg-green-700 text-white font-medium px-4 h-8 text-sm disabled:opacity-50"
        >
          {isPublishing ? "Publishing..." : "Publish"}
        </Button>

        <Button variant="ghost" size="icon" className="text-gray-500">
          <MoreHorizontal className="w-5 h-5" />
        </Button>

        {user ? (
          <UserNav user={user} />
        ) : (
          <Avatar className="h-8 w-8">
            <AvatarFallback>?</AvatarFallback>
          </Avatar>
        )}
      </div>
    </nav>
  );
}

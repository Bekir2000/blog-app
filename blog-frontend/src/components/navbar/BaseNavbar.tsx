import { UserResponse } from "@/api/generated/model";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import Link from "next/link";
import { ReactNode } from "react";
import { UserNav } from "./UserNav"; // Import your UserNav component

interface BaseNavbarProps {
  user: UserResponse | null; // ✅ New Prop
  left: ReactNode;
  center?: ReactNode;
  actions?: ReactNode; // Renamed from 'right' to 'actions' for clarity
  className?: string;
}

export function BaseNavbar({
  user,
  left,
  center,
  actions,
  className,
}: BaseNavbarProps) {
  return (
    <nav
      className={cn(
        "sticky top-0 z-50 w-full border-b border-gray-200 bg-white/80 backdrop-blur-md transition-all dark:border-gray-800 dark:bg-zinc-950/80",
        className
      )}
    >
      <div className="flex h-16 items-center justify-between px-6">
        {/* Left Section */}
        <div className="flex items-center gap-4 min-w-0">{left}</div>

        {/* Center Section */}
        <div className="hidden md:flex flex-1 justify-center px-6 max-w-xl">
          {center}
        </div>

        {/* Right Section: Actions + User Logic */}
        <div className="flex items-center gap-3 justify-end min-w-0">
          {/* 1. Contextual Actions (Write/Bell OR Save/Publish) */}
          {actions}

          {/* 2. Standardized User Logic */}
          {user ? (
            <UserNav user={user} />
          ) : (
            <Button asChild className="rounded-full" size="sm">
              <Link href="/login">Login</Link>
            </Button>
          )}
        </div>
      </div>
    </nav>
  );
}

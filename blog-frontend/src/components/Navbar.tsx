// components/Navbar.tsx

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { getUser } from "@/lib/auth";
import { Bell, Search, SquarePenIcon } from "lucide-react";
import Link from "next/link";
import { InfoTooltip } from "./InfoTooltip";
import { SidebarTrigger } from "./ui/sidebar";

export default async function Navbar() {
  const user = await getUser();
  return (
    <nav className="w-full border-b border-gray-200 bg-white">
      <div className="flex items-center justify-between px-6 py-3">
        {/* Left: Menu + Logo */}
        <div className="flex items-center gap-4">
          <InfoTooltip message="Menu">
            <SidebarTrigger />
          </InfoTooltip>
          <Link href="/" className="text-2xl font-serif font-bold">
            Blogium
          </Link>
        </div>

        {/* Center: Search */}
        <div className="flex-1 px-6">
          <div className="relative w-full max-w-md">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 w-5 h-5" />
            <input
              type="text"
              placeholder="Search"
              className="w-full rounded-full border border-gray-200 bg-gray-50 pl-10 pr-4 py-2 text-sm focus:border-gray-400 focus:outline-none"
            />
          </div>
        </div>

        {/* Right: Write, Bell, Avatar or Login */}
        <div className="flex items-center gap-6">
          <button className="flex items-center gap-1 text-gray-700 hover:text-black">
            <SquarePenIcon className="w-4 h-4" /> Write
          </button>
          <InfoTooltip message="Notifications">
            <Bell className="w-5 h-5 text-gray-600 cursor-pointer" />
          </InfoTooltip>

          {user ? (
            <InfoTooltip message="Account">
              <Avatar className="cursor-pointer">
                <AvatarImage src="/avatar.jpg" alt="User Avatar" />
                <AvatarFallback>
                  {user.username?.charAt(0).toUpperCase()}
                </AvatarFallback>
              </Avatar>
            </InfoTooltip>
          ) : (
            <Button asChild>
              <Link href="/login">Login</Link>
            </Button>
          )}
        </div>
      </div>
    </nav>
  );
}

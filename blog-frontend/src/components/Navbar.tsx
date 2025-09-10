// components/Navbar.tsx
"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Bell, Menu, Search, SquarePenIcon } from "lucide-react";
import Link from "next/link";
import { InfoTooltip } from "./InfoToolTip";

export default function Navbar() {
  return (
    <header className="w-full border-b border-gray-200 bg-white">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-3">
        {/* Left: Menu + Logo */}
        <div className="flex items-center gap-4">
          <InfoTooltip message="Menu">
            <Menu className="w-6 h-6 cursor-pointer" />
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

        {/* Right: Write, Bell, Avatar */}
        <div className="flex items-center gap-6">
          <button className="flex items-center gap-1 text-gray-700 hover:text-black">
            <SquarePenIcon className="w-4 h-4" /> Write
          </button>
          <InfoTooltip message="Notifications">
            <Bell className="w-5 h-5 text-gray-600 cursor-pointer" />
          </InfoTooltip>
          <InfoTooltip message="Account">
            <Avatar className="cursor-pointer">
              <AvatarImage src="/avatar.jpg" alt="User Avatar" />
              <AvatarFallback>U</AvatarFallback>
            </Avatar>
          </InfoTooltip>
        </div>
      </div>
    </header>
  );
}

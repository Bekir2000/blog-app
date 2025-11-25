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
    // FIX: Added 'sticky top-0 z-50' and 'backdrop-blur'
    <nav className="sticky top-0 z-50 w-full border-b border-gray-200 bg-white/80 backdrop-blur-md transition-all">
      <div className="flex items-center justify-between px-6 py-3">
        {/* Left: Menu + Logo */}
        <div className="flex items-center gap-4">
          <InfoTooltip message="Menu">
            <SidebarTrigger />
          </InfoTooltip>
          <Link
            href="/"
            className="text-2xl font-serif font-bold tracking-tight"
          >
            Blogium
          </Link>
        </div>

        {/* Center: Search */}
        <div className="hidden md:flex flex-1 max-w-md px-6">
          <div className="relative w-full">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4" />
            <input
              type="text"
              placeholder="Search"
              className="w-full rounded-full border border-gray-100 bg-gray-50 pl-10 pr-4 py-2 text-sm focus:border-gray-300 focus:bg-white focus:outline-none focus:ring-2 focus:ring-gray-100 transition-all"
            />
          </div>
        </div>

        {/* Right: Write, Bell, Avatar or Login */}
        <div className="flex items-center gap-6">
          <button className="hidden sm:flex items-center gap-1 text-gray-600 hover:text-black transition-colors">
            <SquarePenIcon className="w-4 h-4" /> Write
          </button>

          <InfoTooltip message="Notifications">
            <Bell className="w-5 h-5 text-gray-600 hover:text-black cursor-pointer transition-colors" />
          </InfoTooltip>

          {user ? (
            <InfoTooltip message="Account">
              <Avatar className="cursor-pointer border border-gray-200 hover:border-gray-400 transition-colors">
                <AvatarImage src="/avatar.jpg" alt="User Avatar" />
                <AvatarFallback>
                  {user.username?.charAt(0).toUpperCase()}
                </AvatarFallback>
              </Avatar>
            </InfoTooltip>
          ) : (
            <Button asChild className="rounded-full">
              <Link href="/login">Login</Link>
            </Button>
          )}
        </div>
      </div>
    </nav>
  );
}

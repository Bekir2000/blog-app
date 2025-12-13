import { Button } from "@/components/ui/button";
import { getUser } from "@/lib/auth";
import { Bell, SquarePenIcon } from "lucide-react";
import Link from "next/link";
import { InfoTooltip } from "../InfoTooltip";
import { SidebarTrigger } from "../ui/sidebar";
import { SearchBar } from "./SearchBar";
import { UserNav } from "./UserNav"; // <--- Import the new component

export default async function Navbar() {
  const user = await getUser();

  return (
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
            <SearchBar />
          </div>
        </div>

        {/* Right: Write, Bell, User */}
        <div className="flex items-center gap-4">
          <button className="hidden sm:flex items-center gap-1 text-gray-600 hover:text-black transition-colors px-2">
            <SquarePenIcon className="w-4 h-4" /> Write
          </button>

          <InfoTooltip message="Notifications">
            <Button variant="ghost" size="icon" className="rounded-full">
              <Bell className="w-5 h-5 text-gray-600" />
            </Button>
          </InfoTooltip>

          {user ? (
            // Clean abstraction: The logic is now inside UserNav
            <UserNav user={user} />
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

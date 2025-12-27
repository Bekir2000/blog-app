import { SidebarTrigger } from "@/components/ui/sidebar";
import { getUser } from "@/lib/auth";
import { SquarePenIcon } from "lucide-react";
import Link from "next/link";
import { BrandLogo } from "../ui/brand-logo";
import { BaseNavbar } from "./BaseNavbar";
import { NavbarSearch } from "./NavBarSearch";

export async function MainNavbar() {
  const user = await getUser();

  return (
    <BaseNavbar
      user={user}
      left={
        <>
          <SidebarTrigger />
          <BrandLogo />
        </>
      }
      center={<NavbarSearch />}
      // ✅ Only pass the specific buttons for this page
      actions={
        <>
          <Link
            href="/new-story"
            className="hidden sm:flex items-center gap-1 text-gray-600 hover:text-black transition-colors px-2 text-sm font-medium"
          >
            <SquarePenIcon className="w-4 h-4" /> Write
          </Link>

          {/* <InfoTooltip message="Notifications">
            <Button
              variant="ghost"
              size="icon"
              className="rounded-full text-gray-500"
            >
              <Bell className="w-5 h-5" />
            </Button>
          </InfoTooltip> */}
        </>
      }
    />
  );
}

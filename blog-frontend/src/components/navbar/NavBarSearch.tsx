"use client";

import { usePathname } from "next/navigation";
import { SearchBar } from "./SearchBar";

export function NavbarSearch() {
  const pathname = usePathname();

  // Define which paths should show the search bar
  const showSearch = pathname === "/" || pathname === "/bookmarks";

  if (!showSearch) {
    return null; // Don't render anything on other pages
  }

  return (
    <div className="w-full animate-in fade-in zoom-in duration-200">
      <SearchBar />
    </div>
  );
}

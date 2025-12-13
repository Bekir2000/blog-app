"use client";

import { Search } from "lucide-react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { Suspense } from "react";
import { useDebouncedCallback } from "use-debounce";

function SearchInput() {
  const searchParams = useSearchParams();
  const pathname = usePathname();
  const { replace } = useRouter();

  const handleSearch = useDebouncedCallback((term) => {
    console.log(`Searching... ${term}`);

    const params = new URLSearchParams(searchParams);
    if (term) {
      params.set("query", term);
    } else {
      params.delete("query");
    }
    replace(`${pathname}?${params.toString()}`);
  }, 300);

  return (
    <div className="relative w-full">
      <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-4 h-4" />
      <input
        type="text"
        onChange={(e) => handleSearch(e.target.value)}
        placeholder="Search..."
        className="w-full rounded-full border border-gray-100 bg-gray-50 pl-10 pr-4 py-2 text-sm focus:border-gray-300 focus:bg-white focus:outline-none focus:ring-2 focus:ring-gray-100 transition-all"
        defaultValue={searchParams.get("query") ?? ""}
      />
    </div>
  );
}

export function SearchBar() {
  return (
    <div className="hidden md:flex flex-1 max-w-md px-6">
      <Suspense
        fallback={<div className="h-10 w-full bg-gray-50 rounded-full" />}
      >
        <SearchInput />
      </Suspense>
    </div>
  );
}

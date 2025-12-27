"use client";

import { Filter, Search } from "lucide-react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { Suspense, useState } from "react";
import { useDebouncedCallback } from "use-debounce";

// Shadcn UI Imports
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const SEARCH_SCOPES = [
  { value: "title", label: "Title" },
  { value: "authorName", label: "Author" },
  { value: "tag", label: "Tag" },
];

const CATEGORIES = [
  "TECHNOLOGY",
  "LIFESTYLE",
  "TUTORIAL",
  "NEWS",
  "PERSONAL",
  "CODING",
];

function SearchInput() {
  const searchParams = useSearchParams();
  const pathname = usePathname();
  const { replace } = useRouter();

  const getInitialScope = () => {
    if (searchParams.get("authorName")) return "authorName";
    if (searchParams.get("tag")) return "tag";
    return "title";
  };

  const [scope, setScope] = useState(getInitialScope());

  const executeSearch = useDebouncedCallback(
    (term: string, currentScope: string) => {
      const params = new URLSearchParams(searchParams);
      SEARCH_SCOPES.forEach((s) => params.delete(s.value));
      params.delete("query");

      if (term) {
        params.set(currentScope, term);
      }
      replace(`${pathname}?${params.toString()}`);
    },
    300
  );

  const handleCategoryChange = (category: string) => {
    const params = new URLSearchParams(searchParams);
    if (category && category !== "ALL") {
      params.set("category", category);
    } else {
      params.delete("category");
    }
    replace(`${pathname}?${params.toString()}`);
  };

  const handleScopeChange = (newScope: string) => {
    setScope(newScope);
    const currentInput =
      document.querySelector<HTMLInputElement>("#shadcn-search-input")?.value ||
      "";
    if (currentInput) {
      executeSearch(currentInput, newScope);
    }
  };

  const currentCategory = searchParams.get("category") || "ALL";

  return (
    // Main Container: One border, rounded corners, unified look
    <div className="flex w-full max-w-2xl items-center rounded-lg border bg-background p-0.5 focus-within:ring-2 focus-within:ring-ring focus-within:ring-offset-2">
      {/* 1. Category Dropdown */}
      <Select value={currentCategory} onValueChange={handleCategoryChange}>
        <SelectTrigger className="h-10 rounded-r-none border-0 bg-transparent px-3 focus:ring-0 focus:ring-offset-0">
          <Filter className="mr-2 h-4 w-4 opacity-50" />
          <SelectValue placeholder="All Categories" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="ALL">All Categories</SelectItem>
          {CATEGORIES.map((cat) => (
            <SelectItem key={cat} value={cat}>
              {cat.charAt(0) + cat.slice(1).toLowerCase()}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>

      {/* Divider */}
      <div className="h-6 w-[1px] bg-border" />

      {/* 2. Scope Dropdown (Title, Author, etc.) */}
      <Select value={scope} onValueChange={handleScopeChange}>
        <SelectTrigger className="h-10 w-fit rounded-none border-0 bg-transparent px-3 focus:ring-0 focus:ring-offset-0 text-muted-foreground hover:text-foreground">
          <SelectValue />
        </SelectTrigger>
        <SelectContent>
          {SEARCH_SCOPES.map((s) => (
            <SelectItem key={s.value} value={s.value}>
              {s.label}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>

      {/* Divider */}
      <div className="h-6 w-[1px] bg-border" />

      {/* 3. Search Input & Icon */}
      <div className="flex flex-1 items-center px-3">
        <Search className="mr-2 h-4 w-4 shrink-0 opacity-50" />
        <Input
          id="shadcn-search-input"
          type="text"
          placeholder={`Search...`}
          defaultValue={searchParams.get(scope) ?? ""}
          onChange={(e) => executeSearch(e.target.value, scope)}
          // Remove border and focus ring from the input itself
          className="h-10 flex-1 border-0 bg-transparent p-0 focus-visible:ring-0 focus-visible:ring-offset-0 placeholder:text-muted-foreground"
        />
      </div>
    </div>
  );
}

export function SearchBar() {
  return (
    <div className="hidden md:flex flex-1 px-6">
      <Suspense
        fallback={<div className="h-10 w-full max-w-2xl bg-muted rounded-lg" />}
      >
        <SearchInput />
      </Suspense>
    </div>
  );
}

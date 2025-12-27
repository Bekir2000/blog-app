"use client";

import { Badge } from "@/components/ui/badge";
import { usePathname, useRouter, useSearchParams } from "next/navigation";

// ... (CategoryList component remains the same as previous steps) ...
export function CategoryList() {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const currentCategory = searchParams.get("category");
  const categories = [
    "TECHNOLOGY",
    "LIFESTYLE",
    "TUTORIAL",
    "NEWS",
    "PERSONAL",
    "CODING",
  ];

  const handleCategoryClick = (category: string) => {
    const params = new URLSearchParams(searchParams);
    if (currentCategory === category) params.delete("category");
    else params.set("category", category);
    router.replace(`${pathname}?${params.toString()}`, { scroll: false });
  };

  return (
    <div className="flex flex-wrap gap-2">
      {categories.map((category) => (
        <Badge
          key={category}
          variant={currentCategory === category ? "default" : "secondary"}
          onClick={() => handleCategoryClick(category)}
          className="rounded-full px-4 py-1.5 text-sm font-normal cursor-pointer hover:bg-primary/40"
        >
          {category}
        </Badge>
      ))}
    </div>
  );
}

import { cn } from "@/lib/utils";
import Link from "next/link";

interface BrandLogoProps {
  className?: string;
}

export function BrandLogo({ className }: BrandLogoProps) {
  return (
    <Link
      href="/"
      className={cn(
        "flex items-center gap-2 font-serif font-bold text-xl tracking-tight text-black dark:text-white hover:opacity-80 transition-opacity",
        className
      )}
    >
      {/* You can add an SVG Icon here later if you want */}
      Blogium
    </Link>
  );
}

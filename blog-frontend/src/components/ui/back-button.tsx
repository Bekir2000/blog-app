"use client";

import { Button } from "@/components/ui/button";
import { ChevronLeft } from "lucide-react";
import { useRouter } from "next/navigation";

export function BackButton() {
  const router = useRouter();

  return (
    <Button
      variant="ghost"
      size="icon"
      onClick={() => router.back()}
      className="-ml-3 text-gray-500 hover:text-black mr-1"
      aria-label="Go back"
    >
      <ChevronLeft className="w-6 h-6" />
    </Button>
  );
}

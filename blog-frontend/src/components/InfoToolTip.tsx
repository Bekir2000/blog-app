"use client";

import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

export function InfoTooltip({
  children,
  message,
}: {
  children: React.ReactNode;
  message: string;
}) {
  return (
    <TooltipProvider delayDuration={100}>
      <Tooltip>
        <TooltipTrigger asChild>{children}</TooltipTrigger>
        <TooltipContent
          side="bottom"
          align="center"
          className="bg-black text-white text-sm rounded px-3 py-2"
        >
          {message}
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}

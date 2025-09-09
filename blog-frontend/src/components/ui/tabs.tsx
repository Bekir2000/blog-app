"use client";

import { cn } from "@/lib/utils";
import * as TabsPrimitive from "@radix-ui/react-tabs";
import * as React from "react";

/** Container stays the same (shadcn layout) */
function Tabs({
  className,
  ...props
}: React.ComponentProps<typeof TabsPrimitive.Root>) {
  return (
    <TabsPrimitive.Root
      data-slot="tabs"
      className={cn("flex flex-col gap-2", className)}
      {...props}
    />
  );
}

/** Header: full-width thin bottom line, no pill bg */
function TabsList({
  className,
  ...props
}: React.ComponentProps<typeof TabsPrimitive.List>) {
  return (
    <TabsPrimitive.List
      data-slot="tabs-list"
      className={cn(
        // underline baseline
        "w-full justify-start bg-transparent p-0 h-auto rounded-none",
        "border-b border-gray-200",
        // spacing between triggers
        "inline-flex gap-6",
        className
      )}
      {...props}
    />
  );
}

/** Trigger: gray text by default, black + underline when active, no box */
function TabsTrigger({
  className,
  ...props
}: React.ComponentProps<typeof TabsPrimitive.Trigger>) {
  return (
    <TabsPrimitive.Trigger
      data-slot="tabs-trigger"
      className={cn(
        "px-0 pb-2 h-auto rounded-none",
        "text-sm font-medium text-gray-500 hover:text-black",
        // kill any box/border/shadow
        "bg-transparent border-0 [appearance:none] shadow-none",
        "focus:outline-none focus-visible:outline-none focus:ring-0 focus-visible:ring-0 ring-0 ring-offset-0",
        // bottom underline behavior
        "border-b-2 border-transparent",
        "data-[state=active]:text-black data-[state=active]:border-black",
        // keep icons sane if you add them later
        "[&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4",
        className
      )}
      {...props}
    />
  );
}

/** Content area unchanged; add padding-top if you want spacing below the header */
function TabsContent({
  className,
  ...props
}: React.ComponentProps<typeof TabsPrimitive.Content>) {
  return (
    <TabsPrimitive.Content
      data-slot="tabs-content"
      className={cn("flex-1 outline-none pt-4", className)}
      {...props}
    />
  );
}

export { Tabs, TabsContent, TabsList, TabsTrigger };

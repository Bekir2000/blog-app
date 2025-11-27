"use client"; // This directive is crucial!

import { UserResponse } from "@/api/generated/model";
import {
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu, // Optional: if you want them clickable
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

interface FollowingListProps {
  initialItems: UserResponse[];
}

export function FollowingList({ initialItems }: FollowingListProps) {
  const router = useRouter();

  useEffect(() => {
    // Poll for updates every 10 seconds
    const interval = setInterval(() => {
      // router.refresh() triggers a silent re-fetch of server components
      // It keeps the state but updates the data (props)
      router.refresh();
    }, 10000);

    return () => clearInterval(interval);
  }, [router]);

  return (
    <SidebarGroup>
      <SidebarGroupLabel className="text-sm text-muted-foreground mt-6 mb-3">
        Following
      </SidebarGroupLabel>
      <SidebarGroupContent>
        <SidebarMenu>
          {initialItems.map((user) => (
            <SidebarMenuItem
              key={user.id}
              className="mb-1 last:mb-0 cursor-pointer"
            >
              {/* Wrapped in a div or button for layout */}
              <div className="flex items-center space-x-3 py-2 px-2 hover:bg-sidebar-accent rounded-md transition-colors">
                <div className="relative">
                  <img
                    src={user.profileImageUrl}
                    alt={user.username}
                    className="w-8 h-8 rounded-full object-cover"
                  />
                  {/* The Green Dot - Now Dynamic */}
                  {user.isOnline && (
                    <span className="absolute bottom-0 right-0 block w-2.5 h-2.5 rounded-full border-2 border-white bg-green-500 shadow-sm" />
                  )}
                </div>
                <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                  {user.username}
                </span>
              </div>
            </SidebarMenuItem>
          ))}
        </SidebarMenu>
      </SidebarGroupContent>
    </SidebarGroup>
  );
}

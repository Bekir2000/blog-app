import { UserResponse } from "@/api/generated/model";
import { getMyFollowing } from "@/api/generated/server/me-controller/me-controller";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { getUser } from "@/lib/auth";
import { Bookmark, FileText, Home } from "lucide-react";
import Link from "next/link";
// Import the new Client Component
import { FollowingList } from "./following-list";

// Menu items configuration
const menuItems = [
  {
    title: "Home",
    url: "/",
    icon: Home,
  },
  {
    title: "Library",
    url: "/bookmarks",
    icon: Bookmark,
  },
  // {
  //   title: "Profile",
  //   url: "#",
  //   icon: User,
  // },
  {
    title: "Stories",
    url: "/me/stories",
    icon: FileText,
  },
  // {
  //   title: "Stats",
  //   url: "#",
  //   icon: BarChart,
  // },
];

export async function MenuSidebar() {
  let followingItems: UserResponse[] = [];
  const user = await getUser();

  if (user) {
    // 1. Fetch initial data on the server
    followingItems = await getMyFollowing();
  }

  return (
    <Sidebar>
      <SidebarContent className="flex flex-col p-4 space-y-6">
        {/* Top Section: Static Application Menu */}
        <SidebarGroup>
          <SidebarGroupLabel className="text-sm text-muted-foreground mt-2 mb-3">
            Application
          </SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {menuItems.map((item) => (
                <SidebarMenuItem key={item.title} className="mb-1 last:mb-0">
                  <SidebarMenuButton asChild>
                    <Link
                      href={item.url}
                      className="flex items-center space-x-3 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-200"
                    >
                      <item.icon className="w-5 h-5" />
                      <span className="text-sm font-medium">{item.title}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        {/* Bottom Section: Dynamic Following List 
            We pass the server data to the client component here */}
        <FollowingList initialItems={followingItems} />
      </SidebarContent>
    </Sidebar>
  );
}

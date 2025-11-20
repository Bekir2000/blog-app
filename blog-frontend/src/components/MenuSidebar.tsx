import { BarChart, Bookmark, FileText, Home, User } from "lucide-react";

import { getMyFollowing } from "@/api/generated/me-controller/me-controller";
import { UserResponse } from "@/api/generated/model";
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

// Menu items.
const menuItems = [
  {
    title: "Home",
    url: "#",
    icon: Home,
  },
  {
    title: "Library",
    url: "#",
    icon: Bookmark,
  },
  {
    title: "Profile",
    url: "#",
    icon: User,
  },
  {
    title: "Stories",
    url: "#",
    icon: FileText,
  },
  {
    title: "Stats",
    url: "#",
    icon: BarChart,
  },
];

export async function MenuSidebar() {
  let followingItems: UserResponse[] = [];
  const user = await getUser();
  if (user) {
    followingItems = await getMyFollowing();
  }
  return (
    <Sidebar>
      <SidebarContent className="flex flex-col p-4 space-y-6">
        <SidebarGroup>
          <SidebarGroupLabel className="text-sm text-muted-foreground mt-2 mb-3">
            Application
          </SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {menuItems.map((item) => (
                <SidebarMenuItem key={item.title} className="mb-1 last:mb-0">
                  <SidebarMenuButton asChild>
                    <a
                      href={item.url}
                      className="flex items-center space-x-3 text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-200"
                    >
                      <item.icon className="w-5 h-5" />
                      <span className="text-sm font-medium">{item.title}</span>
                    </a>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup>
          <SidebarGroupLabel className="text-sm text-muted-foreground mt-6 mb-3">
            Following
          </SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {followingItems.map((user) => (
                <SidebarMenuItem
                  key={user.id}
                  className="mb-1 last:mb-0 cursor-pointer"
                >
                  <div className="flex items-center space-x-3">
                    <div className="relative">
                      <img
                        src={user.profileImageUrl}
                        alt={user.username}
                        className="w-8 h-8 rounded-full object-cover"
                      />
                      {/* {user.online && (
                        <span className="absolute bottom-0 right-0 block w-2.5 h-2.5 rounded-full border-2 border-white bg-green-500" />
                      )} */}
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
      </SidebarContent>
    </Sidebar>
  );
}

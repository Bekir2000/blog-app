import { BarChart, Bookmark, FileText, Home, User } from "lucide-react";

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

const followingItems = [
  {
    name: "Manpreet Singh",
    avatarUrl: "https://randomuser.me/api/portraits/men/32.jpg",
    online: true,
  },
  {
    name: "Data Science Collective",
    avatarUrl: "https://randomuser.me/api/portraits/lego/1.jpg",
    online: true,
  },
  {
    name: "Liu Zuo Lin",
    avatarUrl: "https://randomuser.me/api/portraits/women/44.jpg",
    online: false,
  },
  {
    name: "Sophia Chen",
    avatarUrl: "https://randomuser.me/api/portraits/women/68.jpg",
    online: true,
  },
  {
    name: "Carlos Diaz",
    avatarUrl: "https://randomuser.me/api/portraits/men/85.jpg",
    online: false,
  },
];

export function MenuSidebar() {
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
                  key={user.name}
                  className="mb-1 last:mb-0 cursor-pointer"
                >
                  <div className="flex items-center space-x-3">
                    <div className="relative">
                      <img
                        src={user.avatarUrl}
                        alt={user.name}
                        className="w-8 h-8 rounded-full object-cover"
                      />
                      {user.online && (
                        <span className="absolute bottom-0 right-0 block w-2.5 h-2.5 rounded-full border-2 border-white bg-green-500" />
                      )}
                    </div>
                    <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
                      {user.name}
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

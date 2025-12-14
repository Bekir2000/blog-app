import { MainNavbar } from "@/components/navbar/MainNavbar";
import { MenuSidebar } from "@/components/sidebar/MenuSidebar";
import { SidebarInset, SidebarProvider } from "@/components/ui/sidebar";

export default function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <SidebarProvider defaultOpen={false}>
      <MenuSidebar />
      <SidebarInset>
        <MainNavbar />
        <main className="flex-1">{children}</main>
      </SidebarInset>
    </SidebarProvider>
  );
}

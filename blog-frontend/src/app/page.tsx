import { RightSidebar } from "@/components/RightSidebar";
import { FeedTabs } from "@/features/posts/components/FeedTabs";

export default function HomePage() {
  return (
    <main className="h-screen w-screen flex flex-row gap-10 p-4">
      <div className="flex-1">
        <FeedTabs />
      </div>
      <RightSidebar />
    </main>
  );
}

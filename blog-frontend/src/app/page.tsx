import RightSidebar from "@/components/RightSidebar";
import { FeedTabs } from "@/features/posts/components/FeedTabs";

export default function HomePage() {
  return (
    <main className="mt-10 ">
      <div className="flex  gap-10">
        <FeedTabs />

        <RightSidebar />
      </div>
    </main>
  );
}

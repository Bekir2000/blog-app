import RightSidebar from "@/components/RightSidebar";
import { FeedTabs } from "@/features/posts/components/FeedTabs";

export default function HomePage() {
  return (
    <div className="flex  flex-col gap-4 p-4">
      <div className="flex justify-between">
        <div className="ml-50">
          <FeedTabs />
        </div>

        <RightSidebar />
      </div>
    </div>
  );
}

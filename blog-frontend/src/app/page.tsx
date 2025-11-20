export const dynamic = "force-dynamic";

import RightSidebar from "@/components/RightSidebar";
import { FeedTabs } from "@/components/posts/FeedTabs";

export default async function HomePage() {
  return (
    <main className="mt-10 ">
      <div className="flex  gap-10">
        <FeedTabs />

        <RightSidebar />
      </div>
    </main>
  );
}

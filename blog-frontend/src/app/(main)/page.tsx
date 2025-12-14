import RightSidebar from "@/components/RightSidebar";
import { FeedTabs } from "@/components/posts/FeedTabs";

interface HomePageProps {
  searchParams?: Promise<{
    query?: string;
  }>;
}

export default async function HomePage(props: HomePageProps) {
  const searchParams = await props.searchParams;
  const query = searchParams?.query || "";

  return (
    // Updated: Uses w-full and px-4 for mobile padding, max-w-7xl for large screens
    <main className="mt-10 w-full px-4 md:px-6">
      <div className="mx-auto flex max-w-7xl justify-center gap-10">
        <div className="w-full max-w-3xl">
          <FeedTabs searchQuery={query} />
        </div>

        {/* Sidebar hides on mobile/tablet, shows on large screens */}
        <div className="hidden w-[350px] shrink-0 lg:block">
          <RightSidebar />
        </div>
      </div>
    </main>
  );
}

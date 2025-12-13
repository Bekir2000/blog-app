import RightSidebar from "@/components/RightSidebar";
import { FeedTabs } from "@/components/posts/FeedTabs";

interface HomePageProps {
  // 👇 1. Update the type to Promise
  searchParams?: Promise<{
    query?: string;
  }>;
}

export default async function HomePage(props: HomePageProps) {
  // 👇 2. Await the searchParams before using properties
  const searchParams = await props.searchParams;
  const query = searchParams?.query || "";

  return (
    <main className="mt-10 container mx-auto px-4">
      <div className="flex gap-10 justify-center">
        <div className="w-full max-w-3xl">
          <FeedTabs searchQuery={query} />
        </div>

        <div className="hidden lg:block w-[350px] shrink-0">
          <RightSidebar />
        </div>
      </div>
    </main>
  );
}

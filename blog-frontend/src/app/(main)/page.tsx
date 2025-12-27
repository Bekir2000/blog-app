import { AuthorSummary } from "@/api/generated/model";
import { getBookmarkedPosts } from "@/api/generated/server/me-controller/me-controller";
import { getAllPostCards } from "@/api/generated/server/post-controller/post-controller";
import RightSidebar from "@/components/home/rightsidebar/RightSidebar";
import { FeedTabs } from "@/components/posts/FeedTabs";
import { getUser } from "@/lib/auth";
import { getPostFiltersFromParams } from "@/lib/search-utils";

interface HomePageProps {
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
}

export default async function HomePage(props: HomePageProps) {
  const searchParams = await props.searchParams;
  const apiParams = getPostFiltersFromParams(searchParams);

  const [postsPage, currentUser, bookmarksPage] = await Promise.all([
    getAllPostCards(apiParams),
    getUser(),
    // Fetch only top 3 bookmarks
    getBookmarkedPosts({ page: 0, size: 2 }).catch(() => ({ content: [] })),
  ]);

  const postCards = postsPage.content ?? [];
  const bookmarkCards = bookmarksPage.content ?? [];

  // Extract unique authors logic
  const uniqueAuthorsMap = new Map<string, AuthorSummary>();
  postCards.forEach((post) => {
    if (post.author?.id && !uniqueAuthorsMap.has(post.author.id)) {
      if (post.author.id !== currentUser?.id) {
        // @ts-ignore
        uniqueAuthorsMap.set(post.author.id, post.author);
      }
    }
  });
  const suggestedAuthors = Array.from(uniqueAuthorsMap.values()).slice(0, 3);

  return (
    <main className="mt-10 w-full px-4 md:px-6">
      <div className="mx-auto flex max-w-7xl justify-center gap-10">
        <div className="w-full max-w-3xl">
          <FeedTabs postCards={postCards} currentUser={currentUser} />
        </div>

        <div className="hidden w-[350px] shrink-0 lg:block">
          <RightSidebar
            suggestions={suggestedAuthors}
            currentUser={currentUser}
            bookmarks={bookmarkCards} // 👈 Pass the list here
          />
        </div>
      </div>
    </main>
  );
}

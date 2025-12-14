import { getBookmarkedPosts } from "@/api/generated/server/me-controller/me-controller";
import { BookmarkFeed } from "@/components/me/BookmarkFeed";
// 1. Import the BackButton
import { BackButton } from "@/components/ui/back-button";
import { getUser } from "@/lib/auth";

export default async function BookmarkPage() {
  try {
    const currentUser = await getUser();

    // --- 2. Shared Header Function (Keeps it DRY) ---
    const Header = ({ title }: { title: string }) => (
      <div className="flex items-center gap-2 mb-6">
        <BackButton />
        <h1 className="text-2xl font-bold">{title}</h1>
      </div>
    );

    if (!currentUser) {
      return (
        <main className="flex justify-center p-6">
          <div className="w-full max-w-4xl">
            <Header title="My Bookmarks" />
            <p>You must be logged in to view bookmarks.</p>
          </div>
        </main>
      );
    }

    const postPage = await getBookmarkedPosts();
    const postCards = postPage.content || [];

    return (
      <main className="flex justify-center p-6">
        <div className="w-full max-w-4xl">
          <Header title="My Bookmarks" />

          {postCards.length === 0 ? (
            <div className="text-center py-10 rounded-lg border-2 border-dashed border-gray-100">
              <p className="text-gray-500">
                You haven’t bookmarked any posts yet.
              </p>
            </div>
          ) : (
            <BookmarkFeed initialPosts={postCards} currentUser={currentUser} />
          )}
        </div>
      </main>
    );
  } catch (error) {
    console.error("Failed to load library:", error);
    return (
      <main className="flex justify-center p-6">
        <div className="w-full max-w-4xl">
          {/* Even on error, we show the header + back button */}
          <div className="flex items-center gap-2 mb-6">
            <BackButton />
            <h1 className="text-2xl font-bold">My Library</h1>
          </div>
          <p className="text-red-500">
            Failed to load your bookmarks. Please try again later.
          </p>
        </div>
      </main>
    );
  }
}

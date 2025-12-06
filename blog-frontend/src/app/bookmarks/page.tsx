import { getBookmarkedPosts } from "@/api/generated/server/me-controller/me-controller";
import { BookmarkFeed } from "@/components/me/BookmarkFeed";
import { getUser } from "@/lib/auth";

export default async function BookmarkPage() {
  try {
    const currentUser = await getUser();
    if (!currentUser) {
      return (
        <main className="flex justify-center p-6">
          <div className="w-full max-w-4xl">
            <h1 className="text-2xl font-bold mb-4">My Bookmarks</h1>
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
          <h1 className="text-2xl font-bold mb-4">My Bookmarks</h1>
          {postCards.length === 0 ? (
            <p className="text-gray-500">
              You haven’t bookmarked any posts yet.
            </p>
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
          <h1 className="text-2xl font-bold mb-4">My Library</h1>
          <p className="text-red-500">
            Failed to load your bookmarks. Please try again later.
          </p>
        </div>
      </main>
    );
  }
}

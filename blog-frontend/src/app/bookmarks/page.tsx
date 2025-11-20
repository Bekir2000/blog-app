import { getBookmarkedPosts } from "@/api/generated/me-controller/me-controller";
import { PostsGrid } from "@/components/posts/PostGrid";
import { getUser } from "@/lib/auth";

export default async function BookmarkPage() {
  try {
    const user = await getUser();
    if (!user) {
      return (
        <main className="flex justify-center p-6">
          <div className="w-full max-w-4xl">
            <h1 className="text-2xl font-bold mb-4">My Bookmarks</h1>
            <p>You must be logged in to view bookmarks.</p>
          </div>
        </main>
      );
    }
    const posts = await getBookmarkedPosts();

    return (
      <main className="flex justify-center p-6">
        <div className="w-full max-w-4xl">
          <h1 className="text-2xl font-bold mb-4">My Bookmarks</h1>
          {posts.length === 0 ? (
            <p className="text-gray-500">
              You haven’t bookmarked any posts yet.
            </p>
          ) : (
            <PostsGrid
              postWithBookMarks={posts.map((post) => ({
                post,
                isBookmarked: true,
              }))}
            />
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

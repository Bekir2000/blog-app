import {
  getDrafts,
  getMyPublishedPosts,
} from "@/api/generated/server/post-controller/post-controller";
import { UserStoriesTabs } from "@/components/stories/UserStoriesTabs";
import { getUser } from "@/lib/auth";
import { redirect } from "next/navigation";

export const dynamic = "force-dynamic";

export default async function StoriesPage() {
  // 1. Fetch User First
  const currentUser = await getUser();

  // 2. Security Guard: If not logged in, kick them out immediately
  if (!currentUser) {
    redirect("/login?next=/me/stories"); // Redirect to login
  }

  // 3. Only if logged in, fetch the data in parallel
  const [draftsRes, publishedRes] = await Promise.all([
    getDrafts({ page: 0, size: 5 }),
    getMyPublishedPosts({ page: 0, size: 5 }),
  ]);

  return (
    <div className="container max-w-4xl mx-auto py-10 px-6">
      <UserStoriesTabs
        currentUser={currentUser}
        initialDrafts={draftsRes.content ?? []}
        initialPublished={publishedRes.content ?? []}
      />
    </div>
  );
}

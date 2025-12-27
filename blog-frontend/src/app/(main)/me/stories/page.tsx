import {
  getAllPostCards,
  getMyDrafts,
} from "@/api/generated/server/post-controller/post-controller";
import { UserStoriesTabs } from "@/components/stories/UserStoriesTabs";
import { getUser } from "@/lib/auth";
import { redirect } from "next/navigation";

export const dynamic = "force-dynamic";

export default async function StoriesPage() {
  const currentUser = await getUser();

  if (!currentUser) {
    redirect("/login?next=/me/stories");
  }

  // Fetch initial data server-side
  const [draftsRes, publishedRes] = await Promise.all([
    getMyDrafts({ page: 0, size: 5 }),
    getAllPostCards({
      page: 0,
      size: 5,
      authorName: `${currentUser.firstName} ${currentUser.lastName}`,
    }),
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

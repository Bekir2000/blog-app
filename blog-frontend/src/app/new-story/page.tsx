import { PostEditor } from "@/components/editor/PostEditor";
import { getUser } from "@/lib/auth";
import { redirect } from "next/navigation";

export default async function NewStoryPage() {
  const user = await getUser();

  // Protect the route
  if (!user) {
    redirect("/login?redirect=/new-story");
  }

  return <PostEditor currentUser={user} />;
}

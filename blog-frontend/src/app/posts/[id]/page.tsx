import { getPostById } from "@/api/generated/server/post-controller/post-controller";
import { PostDetail } from "@/components/posts/PostDetail";
import { getUser } from "@/lib/auth";

export default async function Page({ params }: { params: { id: string } }) {
  const postId = (await params).id;
  const post = await getPostById(postId);
  const user = await getUser();

  return <PostDetail postWithBookmark={post} currentUser={user} />;
}

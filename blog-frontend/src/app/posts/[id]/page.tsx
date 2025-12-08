import { getPostById } from "@/api/generated/server/post-controller/post-controller";
import { PostDetail } from "@/components/post-detail/PostDetail";
import { getUser } from "@/lib/auth";

export default async function Page({ params }: { params: { id: string } }) {
  const postId = (await params).id;
  const post = await getPostById(postId);
  const user = await getUser();

  return <PostDetail post={post} currentUser={user} />;
}

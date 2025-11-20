import { PostWithBookmarkResponse } from "@/api/generated/model";
import { getUser } from "@/lib/auth";
import { PostCard } from "./PostCard";

export async function PostsGrid({
  postWithBookMarks,
}: {
  postWithBookMarks: PostWithBookmarkResponse[] | null;
}) {
  const currentUser = await getUser();
  return (
    <div className=" gap-6 flex flex-col">
      {postWithBookMarks?.map((postWithBookMark) => {
        if (!postWithBookMark?.post) return null;
        return (
          <PostCard
            key={postWithBookMark.post.id}
            postWithBookMark={postWithBookMark}
            currentUser={currentUser}
          />
        );
      })}
    </div>
  );
}

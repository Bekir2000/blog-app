"use client";

import { PostDetailResponse, UserResponse } from "@/api/generated/model";
// Hooks
import { usePostBookmark } from "@/hooks/usePostBookmark"; // <--- Import new hook
import { usePostFollow } from "@/hooks/usePostFollow";

// Sub-components
import { PostActionBar } from "./PostActionBar"; // <--- Import new component
import { PostAuthorMeta } from "./PostAuthorMeta";
import { PostContent } from "./PostContent";
import { PostHeader } from "./PostHeader";

interface PostDetailProps {
  post: PostDetailResponse | null;
  currentUser?: UserResponse | null;
}

export function PostDetail({ post, currentUser }: PostDetailProps) {
  // 1. Follow Logic
  const {
    isFollowing,
    isLoading: isFollowLoading,
    isOwnPost,
    toggleFollow,
  } = usePostFollow({
    author: post?.author,
    currentUser,
    initialIsFollowing: post?.followingAuthor,
  });

  const { isBookmarked, isBookmarkLoading, toggleBookmark } = usePostBookmark({
    postId: post?.id ?? "",
    initialIsBookmarked: post?.isBookmarked ?? false, // <--- Correct
  });

  if (!post) return null;

  return (
    <article className="min-h-screen bg-white dark:bg-zinc-950">
      <div className="mx-auto max-w-[680px] px-6 py-10 md:py-14">
        <PostHeader title={post.title} description={post.description} />
        <PostAuthorMeta
          author={post.author}
          createdAt={post.createdAt}
          readingTime={post.readingTime}
          isFollowing={isFollowing}
          isLoading={isFollowLoading}
          isOwnPost={isOwnPost}
          onToggleFollow={toggleFollow}
        />

        <PostActionBar
          likes={post.likes || 0}
          comments={post.commentsCount || 0}
          isBookmarked={isBookmarked} // <--- Uses the local state from the hook
          isLoading={isBookmarkLoading}
          onToggleBookmark={toggleBookmark}
        />

        <PostContent post={post} />

        {/* Footer Tags / More from author etc... (retained from previous steps) */}
        {post.tags && post.tags.length > 0 && (
          <div className="mt-14 mb-10 flex flex-wrap gap-2">
            {post.tags.map((tag) => (
              <span
                key={tag.id}
                className="rounded-full bg-gray-100 px-4 py-2 text-sm text-gray-600 hover:bg-gray-200 cursor-pointer dark:bg-gray-800 dark:text-gray-300"
              >
                {tag.name}
              </span>
            ))}
          </div>
        )}
      </div>
    </article>
  );
}

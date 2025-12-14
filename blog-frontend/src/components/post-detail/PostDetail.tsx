"use client";

import { PostDetailResponse, UserResponse } from "@/api/generated/model";
// Hooks
import { usePostBookmark } from "@/hooks/usePostBookmark";
import { usePostFollow } from "@/hooks/usePostFollow";
import { usePostLike } from "@/hooks/usePostLike";

// Sub-components
// Ensure this path matches where you saved PostComments.tsx.
// If it's in the same folder as PostDetail, use "./PostComments"
import { PostComments } from "./comments/PostComments";
import { PostActionBar } from "./PostActionBar";
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

  // 2. Bookmark Logic
  const { isBookmarked, isBookmarkLoading, toggleBookmark } = usePostBookmark({
    postId: post?.id ?? "",
    initialIsBookmarked: post?.isBookmarked ?? false,
  });

  // 3. Like Logic
  const { isLiked, likeCount, handleToggleLike } = usePostLike({
    postId: post?.id ?? "",
    initialIsLiked: post?.isLiked ?? false,
    initialLikeCount: post?.likes ?? 0,
  });

  // 4. Scroll to Comments Handler
  const handleScrollToComments = () => {
    const element = document.getElementById("comments");
    if (element) {
      element.scrollIntoView({ behavior: "smooth" });
    }
  };

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
          postId={post.id!}
        />

        <PostActionBar
          likes={likeCount}
          comments={post.commentsCount || 0}
          isBookmarked={isBookmarked}
          isLiked={isLiked}
          isLoading={isBookmarkLoading}
          onToggleBookmark={toggleBookmark}
          onToggleLike={handleToggleLike}
          onCommentClick={handleScrollToComments} // <--- Pass the scroll handler here
        />

        <PostContent post={post} />

        {/* Tags */}
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

        {/* --- COMMENTS SECTION --- */}
        {/* Placed INSIDE the max-w container so it stays centered */}
        <PostComments
          postId={post.id!}
          currentUser={currentUser}
          commentsCount={post.commentsCount}
        />
      </div>
    </article>
  );
}

"use client";

import { PostDetailResponse, UserResponse } from "@/api/generated/model";
import { useState } from "react";

// Hooks
import { usePostBookmark } from "@/hooks/usePostBookmark";
import { usePostFollow } from "@/hooks/usePostFollow";
import { usePostLike } from "@/hooks/usePostLike";

// Components
import { PostComments } from "./comments/PostComments";
import { PostActionBar } from "./PostActionBar";
import { PostAuthorMeta } from "./PostAuthorMeta";
import { PostContent } from "./PostContent";
import { PostHeader } from "./PostHeader";

interface PostDetailProps {
  post: PostDetailResponse | null;
  currentUser?: UserResponse | undefined;
}

export function PostDetail({ post, currentUser }: PostDetailProps) {
  // 1. Local State for Comment Count
  const [currentCommentCount, setCurrentCommentCount] = useState(
    post?.metrics?.commentCount ?? 0
  );

  // 2. Follow Logic
  const {
    isFollowing,
    isLoading: isFollowLoading,
    isOwnPost,
    toggleFollow,
  } = usePostFollow({
    author: post?.author,
    currentUser,
    initialIsFollowing: post?.author?.isFollowed ?? false,
  });

  // 3. Bookmark Logic
  const { isBookmarked, isBookmarkLoading, toggleBookmark } = usePostBookmark({
    postId: post?.id ?? "",
    initialIsBookmarked: post?.metrics?.bookmarked ?? false,
  });

  // 4. Like Logic
  const { isLiked, likeCount, handleToggleLike } = usePostLike({
    postId: post?.id ?? "",
    initialIsLiked: post?.metrics?.liked ?? false,
    initialLikeCount: post?.metrics?.likeCount ?? 0,
  });

  // 5. Scroll to Comments Handler
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
        {/* --- BACK BUTTON --- */}

        <PostHeader
          category={post.category}
          title={post.title}
          description={post.description}
        />

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
          comments={currentCommentCount}
          isBookmarked={isBookmarked}
          isLiked={isLiked}
          isLoading={isBookmarkLoading}
          onToggleBookmark={toggleBookmark}
          onToggleLike={handleToggleLike}
          onCommentClick={handleScrollToComments}
        />

        <PostContent post={post} />

        {post.tags && post.tags.length > 0 && (
          <div className="mt-14 mb-10 flex flex-wrap gap-2">
            {post.tags.map((tag) => (
              <span
                key={tag}
                className="rounded-full bg-gray-100 px-4 py-2 text-sm text-gray-600 hover:bg-gray-200 cursor-pointer dark:bg-gray-800 dark:text-gray-300"
              >
                {tag}
              </span>
            ))}
          </div>
        )}

        {/* --- COMMENTS SECTION --- */}
        <PostComments
          postId={post.id!}
          currentUser={currentUser}
          initialCount={post.metrics?.commentCount ?? 0}
          onCountChange={setCurrentCommentCount}
        />
      </div>
    </article>
  );
}

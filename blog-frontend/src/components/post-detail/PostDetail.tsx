"use client";

import { PostDetailResponse, UserResponse } from "@/api/generated/model";
import { Button } from "@/components/ui/button";
import { usePostFollow } from "@/hooks/usePostFollow"; // Assuming you saved the hook
import { Bookmark, MessageCircle, ThumbsUp } from "lucide-react";

// Sub-components (Import these if in separate files)
import { PostAuthorMeta } from "./PostAuthorMeta";
import { PostContent } from "./PostContent";
import { PostHeader } from "./PostHeader";

interface PostDetailProps {
  post: PostDetailResponse | null;
  currentUser?: UserResponse | null;
}

export function PostDetail({ post, currentUser }: PostDetailProps) {
  // 1. Hook Integration
  const { isFollowing, isLoading, isOwnPost, toggleFollow } = usePostFollow({
    author: post?.author,
    currentUser,
    initialIsFollowing: post?.followingAuthor,
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
          isLoading={isLoading}
          isOwnPost={isOwnPost}
          onToggleFollow={toggleFollow}
        />

        {/* Action Bar (Simple enough to keep inline or extract if reused) */}
        <div className="flex items-center justify-between border-y border-gray-100 py-3 dark:border-gray-800 mb-8">
          <div className="flex items-center gap-6">
            <Button
              variant="ghost"
              className="flex items-center gap-2 px-0 hover:bg-transparent"
            >
              <ThumbsUp className="w-5 h-5 text-gray-500" />
              <span className="text-sm text-gray-500">{post.likes || 0}</span>
            </Button>
            <Button
              variant="ghost"
              className="flex items-center gap-2 px-0 hover:bg-transparent"
            >
              <MessageCircle className="w-5 h-5 text-gray-500" />
              <span className="text-sm text-gray-500">
                {post.commentsCount || 0}
              </span>
            </Button>
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="text-gray-500 hover:text-black"
          >
            <Bookmark className="w-5 h-5" />
          </Button>
        </div>

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
      </div>
    </article>
  );
}

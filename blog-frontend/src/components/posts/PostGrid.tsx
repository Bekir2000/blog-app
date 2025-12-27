"use client";

import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { UseInfiniteQueryResult } from "@tanstack/react-query";
import { useEffect, useMemo } from "react";
import { useInView } from "react-intersection-observer";
import { PostCard } from "./PostCard";
import { PostSkeleton } from "./PostSkeleton";

interface PageData {
  content?: PostCardResponse[];
  last?: boolean;
  page?: number;
}

interface PostsGridProps {
  initialPosts: PostCardResponse[] | null;
  currentUser: UserResponse | undefined;
  queryResult: UseInfiniteQueryResult<
    { pages: Array<{ data: PageData }> },
    unknown
  >;
}

export function PostsGrid({
  initialPosts,
  currentUser,
  queryResult,
}: PostsGridProps) {
  const { ref, inView } = useInView({
    threshold: 0,
    rootMargin: "600px",
  });

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } = queryResult;

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  // Combine SSR data (initialPosts) with Client data (data)
  const allPosts = useMemo(() => {
    if (data && data.pages.length > 0) {
      const rawPosts =
        data.pages.flatMap((page) => page.data.content ?? []) || [];

      // Deduplicate posts based on ID
      const seen = new Set();
      return rawPosts.filter((post) => {
        if (!post?.id) return false;
        if (seen.has(post.id)) return false;
        seen.add(post.id);
        return true;
      });
    }

    return initialPosts || [];
  }, [data, initialPosts]);

  return (
    // Updated: Added w-full to ensure it fills the container
    <div className="flex w-full flex-col gap-6 pb-10">
      {allPosts.map((post) => (
        <PostCard key={post.id} postCard={post} currentUser={currentUser} />
      ))}

      {(isFetchingNextPage || hasNextPage) && (
        <div ref={ref} className="flex flex-col gap-6">
          {Array.from({ length: 3 }).map((_, i) => (
            <PostSkeleton key={`skeleton-${i}`} />
          ))}
        </div>
      )}

      {!hasNextPage && allPosts.length > 0 && (
        <div className="py-6 text-center text-sm text-gray-400">
          You&apos;ve reached the end.
        </div>
      )}
    </div>
  );
}

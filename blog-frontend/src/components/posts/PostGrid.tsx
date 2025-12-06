"use client";

import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { UseInfiniteQueryResult } from "@tanstack/react-query";
import { useEffect, useMemo } from "react";
import { useInView } from "react-intersection-observer";
import { PostCard } from "./PostCard";
import { PostSkeleton } from "./PostSkeleton";

// Define a generic type for the data structure your hooks return
interface PageData {
  content?: PostCardResponse[];
  last?: boolean;
  page?: number;
}

interface PostsGridProps {
  initialPosts: PostCardResponse[] | null;
  currentUser: UserResponse | null;

  // The crucial change: Accept the query result as a prop
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

  // Destructure logic from the passed hook result
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } = queryResult;

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  // Combine SSR data (initialPosts) with Client data (data)
  const allPosts = useMemo(() => {
    // 1. If we have client-side fetched data, use it (it's the source of truth)
    if (data && data.pages.length > 0) {
      const rawPosts =
        data.pages.flatMap((page) => page.data.content ?? []) || [];

      // Deduplicate
      const seen = new Set();
      return rawPosts.filter((post) => {
        if (!post?.id) return false;
        if (seen.has(post.id)) return false;
        seen.add(post.id);
        return true;
      });
    }

    // 2. Fallback to SSR initial data
    return initialPosts || [];
  }, [data, initialPosts]);

  return (
    <div className="gap-6 flex flex-col pb-10">
      {allPosts.map((post) => (
        <PostCard key={post.id} postCard={post} currentUser={currentUser} />
      ))}

      {(isFetchingNextPage || hasNextPage) && (
        <div ref={ref} className="flex flex-col gap-6">
          {Array.from({ length: 5 }).map((_, i) => (
            <PostSkeleton key={`skeleton-${i}`} />
          ))}
        </div>
      )}

      {!hasNextPage && allPosts.length > 0 && (
        <div className="text-center py-6 text-gray-400 text-sm">
          You've reached the end.
        </div>
      )}
    </div>
  );
}

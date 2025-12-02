"use client";

import {
  getAllPostCards,
  useGetAllPostCardsInfinite,
} from "@/api/generated/client/post-controller/post-controller";
import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { useEffect, useMemo } from "react";
import { useInView } from "react-intersection-observer";
import { PostCard } from "./PostCard";
import { PostSkeleton } from "./PostSkeleton";

interface PostsGridProps {
  initialPosts: PostCardResponse[] | null;
  currentUser: UserResponse | null;
}

export function PostsGrid({ initialPosts, currentUser }: PostsGridProps) {
  const { ref, inView } = useInView({
    threshold: 0,
    rootMargin: "600px", // Adjusted for smoother prefetching
  });

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useGetAllPostCardsInfinite(
      // Base Params (static)
      { size: 5 },
      {
        query: {
          queryKey: ["posts", "infinite"],

          // 1. IMPORTANT: Map pageParam to API call
          queryFn: async ({ pageParam = 0 }) => {
            return getAllPostCards({
              page: Number(pageParam),
              size: 5,
            });
          },

          initialPageParam: 0,

          // 2. IMPORTANT: Mock the full Axios + Spring Boot response structure
          initialData: initialPosts
            ? {
                pages: [
                  {
                    status: 200,
                    headers: {} as any,
                    // Mocking the PagedResponse structure from Spring
                    data: {
                      content: initialPosts,
                      page: 0,
                      size: 5,
                      last: initialPosts.length < 5, // Infer 'last' based on size
                      totalElements: 0, // Not needed for infinite scroll
                      totalPages: 0, // Not needed for infinite scroll
                    } as any,
                  },
                ],
                pageParams: [0],
              }
            : undefined,

          // 3. IMPORTANT: Calculate next page based on Spring Response
          getNextPageParam: (lastPage) => {
            const response = lastPage.data;

            if (!response || response.isLast) {
              return undefined;
            }

            const currentPage = response.page ?? 0; // or response.number
            return currentPage + 1;
          },
        },
      }
    );

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  // 4. Flatten and Deduplicate Data
  // Spring Boot offset pagination can cause duplicates if new posts are added
  // while the user is scrolling. We filter by ID to be safe.
  const allPosts = useMemo(() => {
    const rawPosts =
      data?.pages.flatMap((page) => page.data.content ?? []) || [];

    const seen = new Set();
    return rawPosts.filter((post) => {
      if (!post || !post.id) return false;
      const isDuplicate = seen.has(post.id);
      seen.add(post.id);
      return !isDuplicate;
    });
  }, [data]);

  return (
    <div className="gap-6 flex flex-col pb-10">
      {allPosts.map((post) => {
        return (
          <PostCard
            key={post.id}
            // Use 'post' or 'postCard' depending on your component props
            postCard={post}
            currentUser={currentUser}
          />
        );
      })}

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

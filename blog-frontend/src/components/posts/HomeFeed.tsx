"use client";

import {
  getAllPostCards,
  useGetAllPostCardsInfinite,
} from "@/api/generated/client/post-controller/post-controller";
import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { PostsGrid } from "@/components/posts/PostGrid";
import { getPostFiltersFromParams } from "@/lib/search-utils";
import { useSearchParams } from "next/navigation";
import { useMemo } from "react";

interface HomeFeedProps {
  initialPosts: PostCardResponse[] | null;
  currentUser: UserResponse | undefined;
  // removed legacy searchQuery prop since we read from URL now
}

export function HomeFeed({ initialPosts, currentUser }: HomeFeedProps) {
  const searchParams = useSearchParams();

  // 1. Memoize the filters object.
  // This ensures we have a stable object unless the URL parameters actually change.
  const filters = useMemo(() => {
    return getPostFiltersFromParams(Object.fromEntries(searchParams.entries()));
  }, [searchParams.toString()]); // Re-run only when URL string changes

  const queryResult = useGetAllPostCardsInfinite(
    // Pass filters here for Orval's internal type safety
    {
      size: 5,
      ...filters,
    },
    {
      query: {
        // 2. CRITICAL FIX: Add 'filters' to the queryKey.
        // Now, if category changes from 'TECH' to 'NEWS', the key changes, and it refetches.
        queryKey: ["posts", "infinite", currentUser?.id, filters],
        staleTime: 0,
        refetchOnMount: true,

        queryFn: async ({ pageParam = 0 }) => {
          console.log("Fetching with filters:", filters);
          return getAllPostCards({
            page: Number(pageParam),
            size: 5,
            ...filters, // Spread: authorName, category, tag, title
          });
        },

        getNextPageParam: (lastPage) => {
          const response = lastPage.data;
          if (!response || response.isLast) return undefined;
          return (response.page ?? 0) + 1;
        },

        initialPageParam: 0,

        initialData: initialPosts
          ? {
              pages: [
                {
                  status: 200,
                  headers: {} as any,
                  data: {
                    content: initialPosts,
                    page: 0,
                    size: 5,
                    last: initialPosts.length < 5,
                  } as any,
                },
              ],
              pageParams: [0],
            }
          : undefined,
      },
    }
  );

  return (
    <PostsGrid
      initialPosts={initialPosts}
      currentUser={currentUser}
      queryResult={queryResult}
    />
  );
}

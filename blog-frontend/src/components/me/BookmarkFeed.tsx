"use client";

import {
  getBookmarkedPosts,
  useGetBookmarkedPostsInfinite,
} from "@/api/generated/client/me-controller/me-controller";
import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { PostsGrid } from "@/components/posts/PostGrid";
import { getPostFiltersFromParams } from "@/lib/search-utils";
import { useSearchParams } from "next/navigation";
import { useMemo } from "react";

interface BookmarkFeedProps {
  initialPosts: PostCardResponse[];
  currentUser: UserResponse | null;
}

export function BookmarkFeed({ initialPosts, currentUser }: BookmarkFeedProps) {
  const searchParams = useSearchParams();

  // 1. Memoize the filters object based on URL params
  const filters = useMemo(() => {
    return getPostFiltersFromParams(Object.fromEntries(searchParams.entries()));
  }, [searchParams.toString()]);

  const queryResult = useGetBookmarkedPostsInfinite(
    // 2. Pass filters here for TypeScript definitions
    {
      size: 5,
      ...filters,
    },
    {
      query: {
        // 3. Add 'filters' to queryKey to trigger refetch when URL changes
        queryKey: ["me", "bookmarks", filters],
        staleTime: 0,
        refetchOnMount: true,
        initialPageParam: 0,

        queryFn: async ({ pageParam = 0 }) => {
          console.log("Fetching bookmarks with filters:", filters);
          // 4. Spread filters into the API call
          return getBookmarkedPosts({
            page: Number(pageParam),
            size: 5,
            ...filters, // authorName, category, tag, title
          });
        },

        getNextPageParam: (lastPage) => {
          const response = lastPage.data;
          if (!response || response.isLast) return undefined;
          return (response.page ?? 0) + 1;
        },

        initialData: {
          pages: [
            {
              status: 200,
              headers: {} as any,
              data: {
                content: initialPosts,
                page: 0,
                last: initialPosts.length < 5,
              } as any,
            },
          ],
          pageParams: [0],
        },
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

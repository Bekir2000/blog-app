"use client";

import {
  getBookmarkedPosts,
  useGetBookmarkedPostsInfinite,
} from "@/api/generated/client/me-controller/me-controller";
import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { PostsGrid } from "@/components/posts/PostGrid";

interface BookmarkFeedProps {
  initialPosts: PostCardResponse[];
  currentUser: UserResponse | null;
}

export function BookmarkFeed({ initialPosts, currentUser }: BookmarkFeedProps) {
  const queryResult = useGetBookmarkedPostsInfinite(
    { size: 5 },
    {
      query: {
        queryKey: ["me", "bookmarks"],
        staleTime: 0,
        refetchOnMount: true,
        initialPageParam: 0,

        queryFn: async ({ pageParam = 0 }) => {
          return getBookmarkedPosts({
            page: Number(pageParam),
            size: 5,
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

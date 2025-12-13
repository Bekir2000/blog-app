"use client";

import {
  getAllPostCards,
  useGetAllPostCardsInfinite,
} from "@/api/generated/client/post-controller/post-controller";
import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { PostsGrid } from "@/components/posts/PostGrid";
import { useSearchParams } from "next/navigation";

interface HomeFeedProps {
  initialPosts: PostCardResponse[] | null;
  currentUser: UserResponse | null;
  searchQuery?: string;
}

export function HomeFeed({ initialPosts, currentUser }: HomeFeedProps) {
  const searchParams = useSearchParams();
  const searchQuery = searchParams.get("query") ?? "";

  const queryResult = useGetAllPostCardsInfinite(
    { size: 5 },
    {
      query: {
        // Ensure the key relies on the user ID
        queryKey: ["posts", "infinite", currentUser?.id, searchQuery || "all"],

        // 1. Fetch immediately in background
        staleTime: 0,
        refetchOnMount: true,

        queryFn: async ({ pageParam = 0 }) => {
          // DEBUG: check if this is actually running in your browser console
          console.log("Fetching page:", pageParam);
          return getAllPostCards({
            page: Number(pageParam),
            size: 5,
            query: searchQuery,
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

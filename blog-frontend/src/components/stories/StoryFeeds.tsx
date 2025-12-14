"use client";

import {
  useGetDraftsInfinite,
  useGetMyPublishedPostsInfinite,
} from "@/api/generated/client/post-controller/post-controller";
import {
  PagedResponsePostCardResponse,
  PostCardResponse,
  UserResponse,
} from "@/api/generated/model";
import { StoryList } from "./StoryList";

// Define the structure of the response wrapper from Orval
interface WrapperResponse {
  data: PagedResponsePostCardResponse;
  status: number;
}

// --- Shared Helper ---
const getInfiniteOptions = (initialData: PostCardResponse[]) => ({
  query: {
    staleTime: 0,
    initialPageParam: 0,
    // Explicitly type lastPage so we can access .data safely
    getNextPageParam: (lastPage: WrapperResponse) => {
      const responseData = lastPage.data;
      if (!responseData || responseData.isLast) return undefined;
      return (responseData.page ?? 0) + 1;
    },
    // Hydrate SSR data
    initialData: initialData
      ? {
          pages: [
            {
              status: 200,
              headers: {} as any,
              data: {
                content: initialData,
                page: 0,
                size: 5,
                // Check if we fetched fewer than requested to determine if it's the last page
                isLast: initialData.length < 5,
              } as PagedResponsePostCardResponse,
            },
          ],
          pageParams: [0],
        }
      : undefined,
  },
});

// --- 1. Drafts Feed ---
export function DraftsFeed({
  initialPosts,
  currentUser,
}: {
  initialPosts: PostCardResponse[];
  currentUser: UserResponse | null;
}) {
  const queryResult = useGetDraftsInfinite(
    { size: 5 },
    getInfiniteOptions(initialPosts) as any
  );

  return (
    <StoryList
      type="DRAFT"
      queryResult={queryResult}
      initialPosts={initialPosts}
    />
  );
}

// --- 2. Published Feed ---
export function PublishedFeed({
  initialPosts,
  currentUser,
}: {
  initialPosts: PostCardResponse[];
  currentUser: UserResponse | null;
}) {
  const queryResult = useGetMyPublishedPostsInfinite(
    { size: 5 },
    getInfiniteOptions(initialPosts) as any
  );

  return (
    <StoryList
      type="PUBLISHED"
      queryResult={queryResult}
      initialPosts={initialPosts}
    />
  );
}

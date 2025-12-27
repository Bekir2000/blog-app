"use client";

import {
  useGetAllPostCardsInfinite,
  useGetMyDraftsInfinite,
} from "@/api/generated/client/post-controller/post-controller";
import {
  PagedResponsePostCardResponse,
  PostCardResponse,
  UserResponse,
} from "@/api/generated/model";
import { StoryList } from "./StoryList";

// Helper to standardise React Query Infinite Options
const getInfiniteOptions = (initialData: PostCardResponse[]) => ({
  query: {
    staleTime: 0, // 5 minutes
    initialPageParam: 0,
    getNextPageParam: (lastPage: { data: PagedResponsePostCardResponse }) => {
      const { page, isLast } = lastPage.data;
      if (isLast || page === undefined) return undefined;
      return page + 1;
    },
    // Hydrate Server Data into the Cache
    initialData: initialData
      ? {
          pages: [
            {
              status: 200,
              data: {
                content: initialData,
                page: 0,
                size: 5,
                isLast: initialData.length < 5,
                totalPages: 1,
                totalElements: initialData.length,
              } as PagedResponsePostCardResponse,
            },
          ],
          pageParams: [0],
        }
      : undefined,
  },
});

export function DraftsFeed({
  initialPosts,
}: {
  initialPosts: PostCardResponse[];
  currentUser: UserResponse | null;
}) {
  const queryResult = useGetMyDraftsInfinite(
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

export function PublishedFeed({
  initialPosts,
  currentUser,
}: {
  initialPosts: PostCardResponse[];
  currentUser: UserResponse | null;
}) {
  const queryResult = useGetAllPostCardsInfinite(
    {
      size: 5,
      authorName: `${currentUser?.firstName} ${currentUser?.lastName}`,
    },
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

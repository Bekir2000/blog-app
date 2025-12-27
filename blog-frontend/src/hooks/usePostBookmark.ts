"use client";

import {
  useBookmarkPost,
  useUnbookmarkPost,
} from "@/api/generated/client/me-controller/me-controller";
import { InfiniteData, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";

interface UsePostBookmarkProps {
  postId: string;
  initialIsBookmarked: boolean;
}

export function usePostBookmark({
  postId,
  initialIsBookmarked,
}: UsePostBookmarkProps) {
  const queryClient = useQueryClient();

  // 1. Local state for instant feedback (no waiting for API)
  const [isBookmarked, setIsBookmarked] = useState(initialIsBookmarked);

  // 2. Mutations
  const { mutate: bookmarkApi, isPending: isBookmarkLoading } =
    useBookmarkPost();
  const { mutate: unbookmarkApi, isPending: isUnbookmarkLoading } =
    useUnbookmarkPost();

  const toggleBookmark = () => {
    if (!postId) return;

    // A. Optimistic Update (Local)
    const previousState = isBookmarked;
    const newState = !isBookmarked;
    setIsBookmarked(newState);

    // B. Optimistic Update (Global Cache)
    // This fixes the "delay" by updating the Feed list immediately
    updateAllCaches(postId, newState);

    // C. API Call
    if (newState) {
      bookmarkApi(
        { data: { postId } },
        {
          onError: () => revert(previousState),
        }
      );
    } else {
      unbookmarkApi(
        { postId },
        {
          onError: () => revert(previousState),
        }
      );
    }
  };

  // Helper: Revert changes if API fails
  const revert = (previousState: boolean) => {
    setIsBookmarked(previousState);
    updateAllCaches(postId, previousState);
    toast.error("Failed to update bookmark");
  };

  // Helper: Find EVERY query that looks like a post list and update this specific post
  const updateAllCaches = (targetPostId: string, newBookmarkState: boolean) => {
    queryClient.setQueriesData<InfiniteData<any>>(
      { queryKey: ["posts"] }, // <--- Matches "posts", "posts infinite", etc.
      (oldData) => {
        if (!oldData) return oldData;

        // Handle Infinite Query Structure (pages -> content)
        return {
          ...oldData,
          pages: oldData.pages.map((page) => ({
            ...page,
            data: {
              ...page.data,
              content: page.data.content?.map((post: any) =>
                post.id === targetPostId
                  ? {
                      ...post,
                      meta: { ...post.meta, isBookmarked: newBookmarkState },
                    }
                  : post
              ),
            },
          })),
        };
      }
    );
  };

  return {
    isBookmarked,
    isBookmarkLoading: isBookmarkLoading || isUnbookmarkLoading,
    toggleBookmark,
  };
}

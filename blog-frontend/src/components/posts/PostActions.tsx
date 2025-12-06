"use client";

import {
  useBookmarkPost,
  useUnbookmarkPost,
} from "@/api/generated/client/me-controller/me-controller";
import { UserResponse } from "@/api/generated/model";
import { InfiniteData, useQueryClient } from "@tanstack/react-query";
import { Bookmark, BookmarkPlus, CircleMinus, Ellipsis } from "lucide-react";

interface PostActionsProps {
  postId: string;
  currentUser: UserResponse | null;
  isBookmarked?: boolean;
}

export function PostActions({
  postId,
  currentUser,
  isBookmarked,
}: PostActionsProps) {
  const queryClient = useQueryClient();

  // Orval generated hooks
  const { mutate: bookmark } = useBookmarkPost();
  const { mutate: unbookmark } = useUnbookmarkPost();

  const handleToggle = async (e: React.MouseEvent) => {
    e.stopPropagation(); // Stop click from opening the post details
    e.preventDefault();
    if (!currentUser) return;

    const nextState = !isBookmarked;

    // 1. CANCEL ONGOING FETCHES (Crucial Fix)
    // This stops any background refresh (e.g., from loading the page)
    // from overwriting our optimistic update with old data.
    await queryClient.cancelQueries({ queryKey: ["posts"] });
    await queryClient.cancelQueries({ queryKey: ["me", "bookmarks"] });

    // 2. OPTIMISTIC UPDATE
    // Update the UI immediately in all lists (Home, Search, Bookmarks)
    updateAllCaches(nextState);

    // 3. PERFORM API CALL
    if (isBookmarked) {
      // Unbookmark Logic
      unbookmark(
        { postId }, // Usually DELETE takes path param only
        {
          onError: () => {
            // Revert UI on error
            updateAllCaches(!nextState);
          },
        }
      );
    } else {
      // Bookmark Logic
      bookmark(
        { data: { postId } }, // Wrapped in 'data' for Orval body
        {
          onError: () => {
            // Revert UI on error
            updateAllCaches(!nextState);
          },
        }
      );
    }
  };

  const updateAllCaches = (newState: boolean) => {
    // We target two main groups of queries:
    // 1. ["posts"] -> Covers Home Feed, Search Results, Category Feeds
    // 2. ["me", "bookmarks"] -> Covers the Library/Bookmark page
    const queryKeys = [["posts"], ["me", "bookmarks"]];

    queryKeys.forEach((key) => {
      // setQueriesData (plural) updates all queries matching the key prefix
      queryClient.setQueriesData<InfiniteData<any>>(
        { queryKey: key },
        (oldData) => {
          if (!oldData) return oldData;

          return {
            ...oldData,
            pages: oldData.pages.map((page) => ({
              ...page,
              data: {
                ...page.data,
                // Find the specific post in the list and flip the flag
                content: page.data.content?.map((post: any) =>
                  post.id === postId
                    ? { ...post, isBookmarked: newState }
                    : post
                ),
              },
            })),
          };
        }
      );
    });
  };

  return (
    <div className="flex gap-4 mr-50">
      <CircleMinus className="cursor-pointer hover:text-gray-600" />

      {currentUser ? (
        <div onClick={handleToggle} className="cursor-pointer">
          {isBookmarked ? (
            <Bookmark className="text-red-600 fill-red-600" />
          ) : (
            <BookmarkPlus className="text-gray-500 hover:text-red-600" />
          )}
        </div>
      ) : (
        <BookmarkPlus className="cursor-pointer text-gray-300 opacity-50" />
      )}

      <Ellipsis className="cursor-pointer hover:text-gray-600" />
    </div>
  );
}

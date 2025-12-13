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
    e.stopPropagation();
    e.preventDefault();
    if (!currentUser) return;

    const nextState = !isBookmarked;

    await queryClient.cancelQueries({ queryKey: ["posts"] });
    await queryClient.cancelQueries({ queryKey: ["me", "bookmarks"] });

    updateAllCaches(nextState);

    if (isBookmarked) {
      unbookmark({ postId }, { onError: () => updateAllCaches(!nextState) });
    } else {
      bookmark(
        { data: { postId } },
        { onError: () => updateAllCaches(!nextState) }
      );
    }
  };

  const updateAllCaches = (newState: boolean) => {
    const queryKeys = [["posts"], ["me", "bookmarks"]];

    queryKeys.forEach((key) => {
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
    // FIX 1: Removed 'mr-50'. Added 'items-center' and responsive gap.
    // The parent PostCard already handles the spacing between left/right elements.
    <div className="flex items-center gap-3 sm:gap-4 text-gray-500">
      <CircleMinus className="h-5 w-5 cursor-pointer hover:text-gray-700 transition-colors" />

      {currentUser ? (
        <div onClick={handleToggle} className="cursor-pointer">
          {isBookmarked ? (
            <Bookmark className="h-5 w-5 text-red-600 fill-red-600 transition-colors" />
          ) : (
            <BookmarkPlus className="h-5 w-5 hover:text-gray-700 transition-colors" />
          )}
        </div>
      ) : (
        <BookmarkPlus className="h-5 w-5 cursor-pointer text-gray-300 opacity-50" />
      )}

      <Ellipsis className="h-5 w-5 cursor-pointer hover:text-gray-700 transition-colors" />
    </div>
  );
}

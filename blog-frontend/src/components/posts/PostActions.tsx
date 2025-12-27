"use client";

import { UserResponse } from "@/api/generated/model";
import { usePostBookmark } from "@/hooks/usePostBookmark";
import { Bookmark, BookmarkPlus } from "lucide-react";
import { MouseEvent } from "react";

interface PostActionsProps {
  postId: string;
  currentUser: UserResponse | undefined;
  isBookmarked?: boolean;
}

export function PostActions({
  postId,
  currentUser,
  isBookmarked: initialIsBookmarked = false,
}: PostActionsProps) {
  // The hook now handles global cache updates automatically!
  const { isBookmarked, toggleBookmark } = usePostBookmark({
    postId,
    initialIsBookmarked,
  });

  const handleToggle = (e: MouseEvent) => {
    e.stopPropagation(); // Stop click from opening the post
    e.preventDefault();

    if (!currentUser) return;
    toggleBookmark();
  };

  return (
    <div className="flex items-center gap-3 sm:gap-4 text-gray-500">
      {/* <CircleMinus className="h-5 w-5 cursor-pointer hover:text-gray-700 transition-colors" /> */}

      {currentUser ? (
        <div onClick={handleToggle} className="cursor-pointer p-1 -m-1">
          {isBookmarked ? (
            <Bookmark className="h-5 w-5 text-red-600 fill-red-600 transition-colors" />
          ) : (
            <BookmarkPlus className="h-5 w-5 hover:text-gray-700 transition-colors" />
          )}
        </div>
      ) : (
        <BookmarkPlus className="h-5 w-5 cursor-pointer text-gray-300 opacity-50" />
      )}

      {/* <Ellipsis className="h-5 w-5 cursor-pointer hover:text-gray-700 transition-colors" /> */}
    </div>
  );
}

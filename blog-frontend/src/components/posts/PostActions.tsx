// PostActions.tsx
"use client";

import { UserResponse } from "@/api/generated/model";
import {
  createBookmarkAction,
  deleteBookmarkAction,
} from "@/lib/actions/users/actions";
import { Bookmark, BookmarkPlus, CircleMinus, Ellipsis } from "lucide-react";
import { useState } from "react";

export function PostActions({
  postId,
  currentUser,
  isBookmarked,
}: {
  postId: string;
  currentUser: UserResponse | null;
  isBookmarked?: boolean;
}) {
  const [bookmarked, setBookmarked] = useState(isBookmarked || false);

  const handleBookmarkToggle = async () => {
    console.log("Toggling bookmark for postId:", postId);
    if (bookmarked) {
      await deleteBookmarkAction(postId);
    } else {
      await createBookmarkAction(postId);
    }
    setBookmarked((prev) => !prev);
  };

  return (
    <div className="flex gap-4 mr-50">
      <CircleMinus className="cursor-pointer" />
      {currentUser ? (
        bookmarked ? (
          <Bookmark
            className="cursor-pointer text-red-600 fill-red-600"
            onClick={handleBookmarkToggle}
          />
        ) : (
          <BookmarkPlus
            className="cursor-pointer text-red-600"
            onClick={handleBookmarkToggle}
          />
        )
      ) : (
        <BookmarkPlus className="cursor-pointer text-red-600 opacity-50 " />
      )}
      {/* options */}
      <Ellipsis className="cursor-pointer" />
    </div>
  );
}

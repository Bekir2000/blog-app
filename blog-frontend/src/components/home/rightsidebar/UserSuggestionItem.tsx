"use client";

import { AuthorSummary, UserResponse } from "@/api/generated/model";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { usePostFollow } from "@/hooks/usePostFollow";
import { Loader2 } from "lucide-react";

export function UserSuggestionItem({
  author,
  currentUser,
}: {
  author: AuthorSummary;
  currentUser?: UserResponse | null;
}) {
  // We cast AuthorCardSummary to the type expected by the hook.
  // Ideally, ensure your API returns 'isFollowed' in the AuthorCardSummary.
  const { isFollowing, toggleFollow, isLoading } = usePostFollow({
    author: author,
    currentUser,
    // @ts-ignore: Assuming backend now sends isFollowed in author summary
    initialIsFollowing: author.isFollowed ?? false,
  });

  return (
    <div className="flex items-start justify-between gap-3">
      <div className="flex items-center gap-3">
        <Avatar className="h-9 w-9">
          <AvatarImage src={author.imageUrl} />
          <AvatarFallback>{author.firstName?.[0]}</AvatarFallback>
        </Avatar>
        <div className="flex-1 min-w-0">
          <p className="text-sm font-semibold text-gray-900 truncate">
            {author.firstName} {author.lastName}
          </p>
          <p className="text-xs text-gray-500 line-clamp-1 break-all">Writer</p>
        </div>
      </div>
      <Button
        variant={isFollowing ? "secondary" : "outline"}
        size="sm"
        onClick={toggleFollow}
        disabled={isLoading}
        className={`rounded-full h-8 px-4 transition-colors ${
          isFollowing
            ? "bg-gray-200 text-gray-800 hover:bg-gray-300"
            : "border-gray-400 hover:border-black hover:bg-transparent"
        }`}
      >
        {isLoading ? (
          <Loader2 className="h-3 w-3 animate-spin" />
        ) : isFollowing ? (
          "Unfollow"
        ) : (
          "Follow"
        )}
      </Button>
    </div>
  );
}

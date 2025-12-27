"use client";

import { PostDetailResponse } from "@/api/generated/model";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { format } from "date-fns";

// Import your new component
import { PostShareMenu } from "./PostShareMenu";

interface PostAuthorMetaProps {
  author: PostDetailResponse["author"];
  createdAt?: string;
  readingTime?: number;
  isFollowing: boolean;
  isLoading: boolean;
  isOwnPost: boolean;
  onToggleFollow: () => void;
  postTitle?: string;
  postId?: string;
}

export function PostAuthorMeta({
  author,
  createdAt,
  readingTime,
  isFollowing,
  isLoading,
  isOwnPost,
  onToggleFollow,
  postTitle,
  postId,
}: PostAuthorMetaProps) {
  return (
    <div className="mb-8 flex items-center justify-between">
      {/* LEFT: Author Profile & Meta */}
      <div className="flex items-center gap-3">
        <Avatar className="h-10 w-10 cursor-pointer border border-border/50">
          <AvatarImage src={author?.imageUrl} alt={author?.firstName} />
          <AvatarFallback className="text-xs bg-muted text-muted-foreground font-medium">
            {author?.firstName?.[0]?.toUpperCase() || "?"}
          </AvatarFallback>
        </Avatar>

        <div className="flex flex-col text-sm">
          {/* Top Row: Name + Follow Button */}
          <div className="flex items-center gap-2">
            <span className="font-semibold text-foreground">
              {author?.firstName} {author?.lastName}
            </span>

            {!isOwnPost && (
              <>
                <span className="text-muted-foreground">·</span>
                <button
                  type="button"
                  onClick={onToggleFollow}
                  disabled={isLoading}
                  className={`font-medium text-xs hover:underline disabled:opacity-50 transition-colors ${
                    isFollowing
                      ? "text-muted-foreground hover:text-foreground"
                      : "text-green-600 hover:text-green-700"
                  }`}
                >
                  {isFollowing ? "Unfollow" : "Follow"}
                </button>
              </>
            )}
          </div>

          {/* Bottom Row: Date + Read Time */}
          <div className="flex items-center gap-1 text-muted-foreground text-xs">
            <span>{readingTime || 1} min read</span>
            <span>·</span>
            <span>
              {createdAt
                ? format(new Date(createdAt), "MMM d, yyyy")
                : "Just now"}
            </span>
            {/* <span
              className="ml-2 cursor-pointer hover:text-foreground transition-colors"
              title="Listen to post"
            >
              <PlayCircle className="w-4 h-4" />
            </span> */}
          </div>
        </div>
      </div>

      {/* RIGHT: Actions */}
      <div className="flex gap-1">
        <PostShareMenu postTitle={postTitle} postsId={postId} />

        {/* <Button
          variant="ghost"
          size="icon"
          className="text-muted-foreground hover:text-foreground hover:bg-muted"
        >
          <MoreHorizontal className="w-5 h-5" />
        </Button> */}
      </div>
    </div>
  );
}

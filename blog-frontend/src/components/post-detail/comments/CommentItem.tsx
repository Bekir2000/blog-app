"use client";

import { CommentResponse } from "@/api/generated/model";
import { useState } from "react";
// 👇 Import the generated Like hook
import { useToggleLike1 } from "@/api/generated/client/comment-controller/comment-controller";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Flag,
  MessageSquare,
  MoreHorizontal,
  ThumbsUp,
  Trash2,
} from "lucide-react";

interface CommentItemProps {
  comment: CommentResponse;
  postId: string; // 👈 We need this for the API call
  currentUserId?: string;
  onDelete?: (commentId: string) => void;
  onReply?: (commentId: string, authorName: string) => void;
}

export function CommentItem({
  comment,
  postId,
  currentUserId,
  onDelete,
  onReply,
}: CommentItemProps) {
  // Setup the mutation
  const { mutate: toggleLike } = useToggleLike1();

  // Local state for optimistic UI updates
  // TODO: If your backend adds 'isLiked' to CommentResponse, initialize this with 'comment.isLiked'
  const [isLiked, setIsLiked] = useState(false);
  const [likesCount, setLikesCount] = useState(comment.likesCount || 0);

  if (!comment.author || !comment.id) return null;

  const { author } = comment;
  const fullName =
    author.firstName && author.lastName
      ? `${author.firstName} ${author.lastName}`
      : author.username;
  const initials = (author.firstName?.[0] || "") + (author.lastName?.[0] || "");
  const isOwnComment = currentUserId === author.id;

  const handleLike = () => {
    if (!comment.id) return;

    // Optimistic Update: Update UI immediately before API returns
    const newLikedState = !isLiked;
    setIsLiked(newLikedState);
    setLikesCount((prev) => (newLikedState ? prev + 1 : prev - 1));

    // Call API
    toggleLike(
      { postId, commentId: comment.id },
      {
        onError: () => {
          // Revert on error
          setIsLiked(!newLikedState);
          setLikesCount((prev) => (!newLikedState ? prev + 1 : prev - 1));
        },
      }
    );
  };

  return (
    <div className="group flex gap-4 items-start">
      <Avatar className="h-9 w-9 border border-border/50">
        <AvatarImage src={author.profileImageUrl || ""} alt={fullName} />
        <AvatarFallback className="text-xs font-medium uppercase">
          {initials || author.username.slice(0, 2)}
        </AvatarFallback>
      </Avatar>

      <div className="flex-1 space-y-1">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-sm font-semibold text-foreground">
              {fullName}
            </span>
            {/* Optional: Add createdAt here if available in DTO */}
          </div>

          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                variant="ghost"
                size="icon"
                className="h-6 w-6 opacity-0 group-hover:opacity-100 transition-opacity data-[state=open]:opacity-100"
              >
                <MoreHorizontal className="h-4 w-4 text-muted-foreground" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              {isOwnComment ? (
                <DropdownMenuItem
                  onClick={() => comment.id && onDelete?.(comment.id)}
                  className="text-destructive focus:text-destructive cursor-pointer"
                >
                  <Trash2 className="mr-2 h-4 w-4" />
                  Delete
                </DropdownMenuItem>
              ) : (
                <DropdownMenuItem className="cursor-pointer">
                  <Flag className="mr-2 h-4 w-4" />
                  Report
                </DropdownMenuItem>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        </div>

        <p className="text-sm text-foreground leading-relaxed break-words whitespace-pre-wrap">
          {comment.content || ""}
        </p>

        <div className="flex items-center gap-4 pt-1">
          {/* Like Button */}
          <Button
            variant="ghost"
            size="sm"
            onClick={handleLike}
            className={`h-auto p-0 hover:bg-transparent transition-colors ${
              isLiked
                ? "text-blue-600 hover:text-blue-700"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            <ThumbsUp
              className={`mr-1.5 h-3.5 w-3.5 ${isLiked ? "fill-current" : ""}`}
            />
            <span className="text-xs font-medium">
              {likesCount > 0 ? likesCount : "Like"}
            </span>
          </Button>

          {/* Reply Button */}
          <Button
            variant="ghost"
            size="sm"
            onClick={() => comment.id && onReply?.(comment.id, fullName)}
            className="h-auto p-0 text-muted-foreground hover:bg-transparent hover:text-foreground"
          >
            <MessageSquare className="mr-1.5 h-3.5 w-3.5" />
            <span className="text-xs font-medium">Reply</span>
          </Button>
        </div>
      </div>
    </div>
  );
}

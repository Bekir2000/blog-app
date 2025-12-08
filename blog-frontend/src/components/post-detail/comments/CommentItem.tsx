"use client";

import { useState } from "react";
// Import your generated type
import { CommentResponse } from "@/api/generated/model";

// UI Components
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
  currentUserId?: string;
  onDelete?: (commentId: string) => void;
  onReply?: (commentId: string, authorName: string) => void;
}

export function CommentItem({
  comment,
  currentUserId,
  onDelete,
  onReply,
}: CommentItemProps) {
  const [isLiked, setIsLiked] = useState(false);

  // 1. Safety Guard: Return null if essential data is missing
  if (!comment.author || !comment.id) return null;

  const { author } = comment;

  // 2. Name Construction: First + Last, fallback to Username
  const fullName =
    author.firstName && author.lastName
      ? `${author.firstName} ${author.lastName}`
      : author.username;

  // 3. Initials for Avatar Fallback
  const initials = (author.firstName?.[0] || "") + (author.lastName?.[0] || "");

  const isOwnComment = currentUserId === author.id;

  return (
    <div className="group flex gap-4 items-start">
      {/* Avatar */}
      <Avatar className="h-9 w-9 border border-border/50">
        <AvatarImage src={author.profileImageUrl || ""} alt={fullName} />
        <AvatarFallback className="text-xs font-medium uppercase">
          {initials || author.username.slice(0, 2)}
        </AvatarFallback>
      </Avatar>

      {/* Content Area */}
      <div className="flex-1 space-y-1">
        {/* Header: Name & Options */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-sm font-semibold text-foreground">
              {fullName}
            </span>
            {/* Note: 'createdAt' is currently missing from your generated CommentResponse type.
                If you update the backend model, you can add the date display here. */}
          </div>

          {/* Dropdown Menu (Three Dots) */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                variant="ghost"
                size="icon"
                className="h-6 w-6 opacity-0 group-hover:opacity-100 transition-opacity data-[state=open]:opacity-100"
              >
                <MoreHorizontal className="h-4 w-4 text-muted-foreground" />
                <span className="sr-only">More options</span>
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

        {/* Comment Body */}
        <p className="text-sm text-foreground leading-relaxed break-words whitespace-pre-wrap">
          {comment.content || ""}
        </p>

        {/* Action Buttons */}
        <div className="flex items-center gap-4 pt-1">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => setIsLiked(!isLiked)}
            className={`h-auto p-0 hover:bg-transparent hover:text-blue-600 ${
              isLiked ? "text-blue-600" : "text-muted-foreground"
            }`}
          >
            <ThumbsUp
              className={`mr-1.5 h-3.5 w-3.5 ${isLiked ? "fill-current" : ""}`}
            />
            <span className="text-xs font-medium">Like</span>
          </Button>

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

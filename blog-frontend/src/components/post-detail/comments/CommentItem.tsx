"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Flag, MoreHorizontal, Trash2 } from "lucide-react";
import { useState } from "react";

import { CommentResponse } from "@/api/generated/model";
import { useCommentLike } from "@/hooks/useCommentLike";
import { CommentActions } from "./CommentActions";
import { CommentReplyForm } from "./CommentReplyForm";

export interface CommentWithChildren extends CommentResponse {
  replies?: CommentWithChildren[];
}

export interface CommentItemProps {
  comment: CommentWithChildren;
  postId: string;
  currentUserId?: string;
  onDelete?: (commentId: string) => void;
  onReplySubmit?: (content: string, parentId: string) => Promise<any>;
}

export function CommentItem({
  comment,
  postId,
  currentUserId,
  onDelete,
  onReplySubmit,
}: CommentItemProps) {
  const [isReplying, setIsReplying] = useState(false);

  // Use the abstracted Like Logic
  const { isLiked, likesCount, handleLike } = useCommentLike({
    commentId: comment.id,
    postId,
    initialLiked: comment.meta?.likedByCurrentUser || false,
    initialCount: comment.meta?.likeCount || 0,
  });

  if (!comment.author || !comment.id) return null;

  const { author } = comment;
  const fullName = `${author.firstName} ${author.lastName}`;
  const initials = (author.firstName?.[0] || "") + (author.lastName?.[0] || "");
  const isOwnComment = currentUserId === author.id;

  return (
    <div className="flex flex-col gap-3">
      {/* 1. Main Comment Content */}
      <div className="group flex gap-3 items-start">
        <Avatar className="h-8 w-8 border border-border/50">
          <AvatarImage src={author.imageUrl || ""} alt={fullName} />
          <AvatarFallback className="text-[10px] font-medium uppercase">
            {initials}
          </AvatarFallback>
        </Avatar>

        <div className="flex-1 space-y-1">
          <div className="flex items-center justify-between">
            <span className="text-sm font-semibold text-foreground">
              {fullName}
            </span>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-6 w-6 opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  <MoreHorizontal className="h-4 w-4 text-muted-foreground" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {isOwnComment ? (
                  <DropdownMenuItem
                    onClick={() => comment.id && onDelete?.(comment.id)}
                    className="text-destructive cursor-pointer"
                  >
                    <Trash2 className="mr-2 h-4 w-4" /> Delete
                  </DropdownMenuItem>
                ) : (
                  <DropdownMenuItem className="cursor-pointer">
                    <Flag className="mr-2 h-4 w-4" /> Report
                  </DropdownMenuItem>
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          </div>

          <p className="text-sm text-foreground/90 whitespace-pre-wrap leading-relaxed">
            {comment.content || ""}
          </p>

          <CommentActions
            likesCount={likesCount}
            isLiked={isLiked}
            onLike={handleLike}
            onReplyClick={() => setIsReplying((prev) => !prev)}
          />
        </div>
      </div>

      {/* 2. Inline Reply Form */}
      {isReplying && (
        <CommentReplyForm
          replyToName={fullName}
          commentId={comment.id}
          onReplySubmit={onReplySubmit}
          onCancel={() => setIsReplying(false)}
        />
      )}

      {/* 3. Recursive Replies */}
      {comment.replies && comment.replies.length > 0 && (
        <div className="ml-4 pl-4 border-l-2 border-border/40 space-y-4 mt-1">
          {comment.replies.map((reply) => (
            <CommentItem
              key={reply.id}
              comment={reply as CommentWithChildren}
              postId={postId}
              currentUserId={currentUserId}
              onDelete={onDelete}
              onReplySubmit={onReplySubmit}
            />
          ))}
        </div>
      )}
    </div>
  );
}

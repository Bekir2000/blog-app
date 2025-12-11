"use client";

import { useToggleLike1 } from "@/api/generated/client/comment-controller/comment-controller";
import { CommentResponse } from "@/api/generated/model";
import { useState } from "react";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Textarea } from "@/components/ui/textarea";
import {
  AlertCircle,
  Flag,
  Loader2,
  MessageSquare,
  MoreHorizontal,
  SendHorizonal,
  ThumbsUp,
  Trash2,
} from "lucide-react";

interface CommentWithChildren extends CommentResponse {
  replies?: CommentWithChildren[];
}

interface CommentItemProps {
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
  const { mutate: toggleLike } = useToggleLike1();

  const [isLiked, setIsLiked] = useState(comment.likedByCurrentUser || false);
  const [likesCount, setLikesCount] = useState(comment.likesCount || 0);

  // Reply State
  const [isReplying, setIsReplying] = useState(false);
  const [replyContent, setReplyContent] = useState("");
  const [isSubmittingReply, setIsSubmittingReply] = useState(false);
  const [replyError, setReplyError] = useState<string | null>(null);

  if (!comment.author || !comment.id) return null;

  const { author } = comment;
  const fullName =
    author.firstName && author.lastName
      ? `${author.firstName} ${author.lastName}`
      : author.username;
  const initials = (author.firstName?.[0] || "") + (author.lastName?.[0] || "");
  const isOwnComment = currentUserId === author.id;

  // --- Logic ---
  const isValidLength =
    replyContent.length >= 10 && replyContent.length <= 2000;

  const handleLike = () => {
    if (!comment.id) return;
    const newIsLiked = !isLiked;
    setIsLiked(newIsLiked);
    setLikesCount((prev) => (newIsLiked ? prev + 1 : prev - 1));

    toggleLike(
      { postId, commentId: comment.id },
      {
        onError: () => {
          setIsLiked(!newIsLiked);
          setLikesCount((prev) => (!newIsLiked ? prev + 1 : prev - 1));
        },
      }
    );
  };

  const handleReplySubmit = async () => {
    // Client-side validation check
    if (!isValidLength || !comment.id || !onReplySubmit) return;

    setIsSubmittingReply(true);
    setReplyError(null);

    try {
      await onReplySubmit(replyContent, comment.id);
      setIsReplying(false);
      setReplyContent("");
    } catch (error: any) {
      console.error("Reply failed", error);
      const backendError = error.response?.data || error.body;
      const validationErrors =
        backendError?.errors || backendError?.fieldErrors;

      if (Array.isArray(validationErrors)) {
        const contentError = validationErrors.find(
          (err: any) => err.field === "content"
        );
        if (contentError) {
          setReplyError(contentError.message);
          setIsSubmittingReply(false);
          return;
        }
      }
      setReplyError(
        backendError?.detail || backendError?.message || "Something went wrong."
      );
    } finally {
      setIsSubmittingReply(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && (e.metaKey || e.ctrlKey)) {
      handleReplySubmit();
    }
  };

  return (
    <div className="flex flex-col gap-3">
      {/* 1. Main Comment Row */}
      <div className="group flex gap-3 items-start">
        <Avatar className="h-8 w-8 border border-border/50">
          <AvatarImage src={author.profileImageUrl || ""} alt={fullName} />
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

          <div className="flex items-center gap-4 pt-1">
            <Button
              variant="ghost"
              size="sm"
              onClick={handleLike}
              className={`h-6 px-1.5 text-xs hover:bg-transparent ${
                isLiked ? "text-blue-600" : "text-muted-foreground"
              }`}
            >
              <ThumbsUp
                className={`mr-1.5 h-3 w-3 ${isLiked ? "fill-current" : ""}`}
              />
              {likesCount > 0 ? likesCount : "Like"}
            </Button>

            <Button
              variant="ghost"
              size="sm"
              onClick={() => {
                setIsReplying(!isReplying);
                setReplyError(null);
              }}
              className="h-6 px-1.5 text-xs text-muted-foreground hover:bg-transparent hover:text-foreground"
            >
              <MessageSquare className="mr-1.5 h-3 w-3" />
              Reply
            </Button>
          </div>
        </div>
      </div>

      {/* 2. Inline Reply Form (MATCHING MAIN FORM STYLE) */}
      {isReplying && (
        <div className="ml-11 flex gap-3 items-start animate-in fade-in slide-in-from-top-2">
          <div className="flex-1 space-y-2">
            <Textarea
              value={replyContent}
              onChange={(e) => {
                setReplyContent(e.target.value);
                if (replyError) setReplyError(null);
              }}
              onKeyDown={handleKeyDown}
              placeholder={`Replying to ${fullName}...`}
              disabled={isSubmittingReply}
              // 👇 Dynamic Styling: Red border if error, generic focus ring otherwise
              className={`min-h-[80px] text-sm resize-none bg-background focus-visible:ring-1 ${
                replyError
                  ? "border-destructive focus-visible:ring-destructive"
                  : ""
              }`}
              autoFocus
            />

            {/* 👇 Status Bar (Error Left, Counter/Buttons Right) */}
            <div className="flex items-center justify-between">
              {/* Validation Message */}
              <div className="text-xs min-h-[20px]">
                {replyError ? (
                  <span className="flex items-center text-destructive font-medium animate-in fade-in slide-in-from-left-1">
                    <AlertCircle className="mr-1 h-3 w-3" />
                    {replyError}
                  </span>
                ) : (
                  // Character Counter
                  <span
                    className={`transition-colors ${
                      replyContent.length > 0 && replyContent.length < 10
                        ? "text-orange-500"
                        : "text-muted-foreground"
                    }`}
                  >
                    {replyContent.length}/2000
                  </span>
                )}
              </div>

              {/* Buttons */}
              <div className="flex gap-2">
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => setIsReplying(false)}
                  disabled={isSubmittingReply}
                  className="h-8 text-xs"
                >
                  Cancel
                </Button>
                <Button
                  size="sm"
                  className="h-8 text-xs"
                  disabled={!isValidLength || isSubmittingReply}
                  variant={replyError ? "destructive" : "default"} // Turns red on error retry
                  onClick={handleReplySubmit}
                >
                  {isSubmittingReply ? (
                    <Loader2 className="mr-2 h-3 w-3 animate-spin" />
                  ) : (
                    <SendHorizonal className="mr-2 h-3 w-3" />
                  )}
                  Reply
                </Button>
              </div>
            </div>
          </div>
        </div>
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

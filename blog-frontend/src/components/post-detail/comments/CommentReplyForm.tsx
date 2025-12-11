"use client";

import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { useReplyForm } from "@/hooks/useCommentLogic";
import { AlertCircle, Loader2, SendHorizonal } from "lucide-react";
interface CommentReplyFormProps {
  replyToName: string;
  commentId?: string;
  onReplySubmit?: (content: string, parentId: string) => Promise<any>;
  onCancel: () => void;
}

export function CommentReplyForm({
  replyToName,
  commentId,
  onReplySubmit,
  onCancel,
}: CommentReplyFormProps) {
  const {
    content,
    setContent,
    isSubmitting,
    error,
    setError,
    isValidLength,
    handleSubmit,
    handleKeyDown,
  } = useReplyForm({
    commentId,
    onReplySubmit,
    onSuccess: onCancel,
  });

  return (
    <div className="ml-11 flex gap-3 items-start animate-in fade-in slide-in-from-top-2">
      <div className="flex-1 space-y-2">
        <Textarea
          value={content}
          onChange={(e) => {
            setContent(e.target.value);
            if (error) setError(null);
          }}
          onKeyDown={handleKeyDown}
          placeholder={`Replying to ${replyToName}...`}
          disabled={isSubmitting}
          className={`min-h-[80px] text-sm resize-none bg-background focus-visible:ring-1 ${
            error ? "border-destructive focus-visible:ring-destructive" : ""
          }`}
          autoFocus
        />

        <div className="flex items-center justify-between">
          {/* Validation / Status Area */}
          <div className="text-xs min-h-[20px]">
            {error ? (
              <span className="flex items-center text-destructive font-medium animate-in fade-in slide-in-from-left-1">
                <AlertCircle className="mr-1 h-3 w-3" />
                {error}
              </span>
            ) : (
              <span
                className={`transition-colors ${
                  content.length > 0 && content.length < 10
                    ? "text-orange-500"
                    : "text-muted-foreground"
                }`}
              >
                {content.length}/2000
              </span>
            )}
          </div>

          {/* Action Buttons */}
          <div className="flex gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={onCancel}
              disabled={isSubmitting}
              className="h-8 text-xs"
            >
              Cancel
            </Button>
            <Button
              size="sm"
              className="h-8 text-xs"
              disabled={!isValidLength || isSubmitting}
              variant={error ? "destructive" : "default"}
              onClick={handleSubmit}
            >
              {isSubmitting ? (
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
  );
}

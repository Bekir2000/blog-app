"use client";

import {
  useLikeComment,
  useUnlikeComment,
} from "@/api/generated/client/comment-controller/comment-controller";
import { useState } from "react";

// --- 1. Updated Like Hook (Split into Like/Unlike) ---
export function useCommentLike({
  commentId,
  postId,
  initialLiked,
  initialCount,
}: {
  commentId?: string;
  postId: string;
  initialLiked: boolean;
  initialCount: number;
}) {
  const { mutate: likeComment } = useLikeComment();
  const { mutate: unlikeComment } = useUnlikeComment();

  const [isLiked, setIsLiked] = useState(initialLiked);
  const [likesCount, setLikesCount] = useState(initialCount);

  const handleLike = () => {
    if (!commentId) return;

    const previousLiked = isLiked;
    const newIsLiked = !isLiked;

    // Optimistic Update
    setIsLiked(newIsLiked);
    setLikesCount((prev) => (newIsLiked ? prev + 1 : prev - 1));

    if (newIsLiked) {
      // API: Like
      likeComment(
        { postId, commentId },
        {
          onError: () => {
            setIsLiked(previousLiked);
            setLikesCount((prev) => prev - 1);
          },
        }
      );
    } else {
      // API: Unlike
      unlikeComment(
        { postId, commentId },
        {
          onError: () => {
            setIsLiked(previousLiked);
            setLikesCount((prev) => prev + 1);
          },
        }
      );
    }
  };

  return { isLiked, likesCount, handleLike };
}

// --- 2. Reply Form Hook (Unchanged) ---
export function useReplyForm({
  commentId,
  onReplySubmit,
  onSuccess,
}: {
  commentId?: string;
  onReplySubmit?: (content: string, parentId: string) => Promise<any>;
  onSuccess: () => void;
}) {
  const [content, setContent] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isValidLength = content.length >= 10 && content.length <= 2000;

  const handleSubmit = async () => {
    if (!isValidLength || !commentId || !onReplySubmit) return;

    setIsSubmitting(true);
    setError(null);

    try {
      await onReplySubmit(content, commentId);
      setContent("");
      onSuccess();
    } catch (err: any) {
      console.error("Reply failed", err);
      const backendError = err.response?.data || err.body;
      const validationErrors =
        backendError?.errors || backendError?.fieldErrors;

      if (Array.isArray(validationErrors)) {
        const contentError = validationErrors.find(
          (e: any) => e.field === "content"
        );
        if (contentError) {
          setError(contentError.message);
          return;
        }
      }
      setError(
        backendError?.detail || backendError?.message || "Something went wrong."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && (e.metaKey || e.ctrlKey)) {
      handleSubmit();
    }
  };

  return {
    content,
    setContent,
    isSubmitting,
    error,
    setError,
    isValidLength,
    handleSubmit,
    handleKeyDown,
  };
}

"use client";

import {
  // 👇 1. Import the Key Helper from your generated file
  getGetCommentsInfiniteQueryKey,
  useLikeComment,
  useUnlikeComment,
} from "@/api/generated/client/comment-controller/comment-controller";
import { useQueryClient } from "@tanstack/react-query";
import { useState } from "react";

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
  const queryClient = useQueryClient();
  const { mutate: likeComment } = useLikeComment();
  const { mutate: unlikeComment } = useUnlikeComment();

  const [isLiked, setIsLiked] = useState(initialLiked);
  const [likesCount, setLikesCount] = useState(initialCount);

  // Helper to invalidate the specific Post's comments
  const invalidateComments = () => {
    // 👇 2. Generate the key for this post.
    // We don't pass 'params' so it matches ALL pages of comments for this post.
    const queryKey = getGetCommentsInfiniteQueryKey(postId);

    queryClient.invalidateQueries({ queryKey });
  };

  const handleLike = () => {
    if (!commentId) return;

    const previousLiked = isLiked;
    const newIsLiked = !isLiked;

    // Optimistic Update
    setIsLiked(newIsLiked);
    setLikesCount((prev) => (newIsLiked ? prev + 1 : prev - 1));

    if (newIsLiked) {
      likeComment(
        { postId, commentId },
        {
          onSuccess: () => {
            // 👇 3. Trigger invalidation
            invalidateComments();
          },
          onError: () => {
            setIsLiked(previousLiked);
            setLikesCount((prev) => prev - 1);
          },
        }
      );
    } else {
      unlikeComment(
        { postId, commentId },
        {
          onSuccess: () => {
            // 👇 3. Trigger invalidation
            invalidateComments();
          },
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

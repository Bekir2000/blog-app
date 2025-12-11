"use client";

import {
  getGetAllCommentsInfiniteQueryKey,
  useCreateComment,
  useGetAllCommentsInfinite,
} from "@/api/generated/client/comment-controller/comment-controller";
import { CreateCommentRequest } from "@/api/generated/model";
import { useQueryClient } from "@tanstack/react-query";

interface UsePostCommentsProps {
  postId: string;
}

export function usePostComments({ postId }: UsePostCommentsProps) {
  const queryClient = useQueryClient();

  // 1. Fetch Comments (Infinite Scroll for Root Level)
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isError,
  } = useGetAllCommentsInfinite(
    postId,
    {
      size: 10,
      sort: ["createdAt,desc"],
    },
    {
      query: {
        initialPageParam: 0,
        getNextPageParam: (lastPageResponse) => {
          const { isLast, page } = lastPageResponse.data;
          if (isLast) return undefined;
          return (page ?? 0) + 1;
        },
      },
    }
  );

  // 2. Create Comment Mutation
  const { mutateAsync: createCommentAsync, isPending: isCreating } =
    useCreateComment({
      mutation: {
        onSuccess: () => {
          // Refresh list to show new comment (root or nested)
          queryClient.invalidateQueries({
            queryKey: getGetAllCommentsInfiniteQueryKey(postId),
          });
        },
      },
    });

  // 👇 UPDATED: Accepts optional parentCommentId
  const addComment = async (content: string, parentCommentId?: string) => {
    const payload: CreateCommentRequest = {
      content,
      parentCommentId, // Sent to backend to link reply
    };
    return createCommentAsync({
      postId,
      data: payload,
    });
  };

  // 3. Flatten pages
  const comments =
    data?.pages.flatMap((pageResponse) => pageResponse.data.content || []) ||
    [];

  return {
    comments,
    isLoading,
    isError,
    isCreating,
    addComment,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  };
}

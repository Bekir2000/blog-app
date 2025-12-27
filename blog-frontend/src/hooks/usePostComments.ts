"use client";

import {
  getGetCommentsInfiniteQueryKey,
  useCreateComment,
  useDeleteComment,
  useGetCommentsInfinite,
  useReplyComment,
} from "@/api/generated/client/comment-controller/comment-controller";
import { useQueryClient } from "@tanstack/react-query";

interface UsePostCommentsProps {
  postId: string;
}

export function usePostComments({ postId }: UsePostCommentsProps) {
  const queryClient = useQueryClient();
  const queryKey = getGetCommentsInfiniteQueryKey(postId);

  // 1. Fetch Comments
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isError,
  } = useGetCommentsInfinite(
    postId,
    { size: 10 },
    {
      query: {
        initialPageParam: 0,
        getNextPageParam: (lastPage) => {
          const { isLast, page } = lastPage.data;
          return isLast ? undefined : (page ?? 0) + 1;
        },
      },
    }
  );

  // 2. Create Root Comment Mutation
  const { mutateAsync: createRootAsync, isPending: isCreatingRoot } =
    useCreateComment({
      mutation: {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey });
        },
      },
    });

  // 3. Reply Mutation
  const { mutateAsync: replyAsync, isPending: isReplying } = useReplyComment({
    mutation: {
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey });
      },
    },
  });

  // 4. Delete Mutation
  const { mutateAsync: deleteAsync, isPending: isDeleting } = useDeleteComment({
    mutation: {
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey });
      },
    },
  });

  // --- Helpers ---

  const addComment = async (content: string, parentCommentId?: string) => {
    if (parentCommentId) {
      return replyAsync({
        postId: postId,
        commentId: parentCommentId,
        data: { content },
      });
    } else {
      return createRootAsync({
        postId,
        data: { content },
      });
    }
  };

  const handleDelete = async (commentId: string) => {
    return deleteAsync({
      postId,
      commentId,
    });
  };

  const comments = data?.pages.flatMap((page) => page.data.content || []) || [];

  // 👇 NEW: Extract total count from the first page of the response
  // Adjust 'totalElements' if your API calls it 'total' or 'count'
  const totalCount = data?.pages[0]?.data?.totalElements;

  return {
    comments,
    totalCount, // 👈 Exporting the live count
    isLoading,
    isError,
    isCreating: isCreatingRoot || isReplying,
    isDeleting,
    addComment,
    handleDelete,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  };
}

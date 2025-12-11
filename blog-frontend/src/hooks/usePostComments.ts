"use client";

import {
  getGetAllCommentsInfiniteQueryKey, // The generated infinite hook
  useCreateComment,
  useGetAllCommentsInfinite, // The generated infinite hook
} from "@/api/generated/client/comment-controller/comment-controller";
import { CreateCommentRequest } from "@/api/generated/model";
import { useQueryClient } from "@tanstack/react-query";

interface UsePostCommentsProps {
  postId: string;
}

export function usePostComments({ postId }: UsePostCommentsProps) {
  const queryClient = useQueryClient();

  // 1. Use the generated Infinite Hook
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
    isError,
  } = useGetAllCommentsInfinite(
    // Arg 1: postId
    postId,
    // Arg 2: Params (Page is handled automatically by pageParam, just set size/sort)
    {
      size: 10,
      sort: ["createdAt,desc"],
    },
    // Arg 3: React Query Options
    {
      query: {
        initialPageParam: 0,
        // The 'lastPage' here is the full Axios response object ({ data, status, headers })
        getNextPageParam: (lastPageResponse) => {
          const { isLast, page } = lastPageResponse.data;

          if (isLast) return undefined;

          // Return the next page number
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
          // Invalidate the specific infinite query key to refresh the list
          queryClient.invalidateQueries({
            queryKey: getGetAllCommentsInfiniteQueryKey(postId),
          });
        },
      },
    });

  const addComment = async (content: string) => {
    const payload: CreateCommentRequest = { content };
    return createCommentAsync({
      postId,
      data: payload,
    });
  };

  // 3. Flatten the pages
  // Note: Orval wraps the response in an object containing { data, status, etc. }
  // So we access page.data.content
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

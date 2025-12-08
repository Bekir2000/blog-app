import {
  getGetAllCommentsQueryKey,
  useCreateComment,
  useGetAllComments,
} from "@/api/generated/client/comment-controller/comment-controller";
import { CommentResponse, CreateCommentRequest } from "@/api/generated/model";
import { useQueryClient } from "@tanstack/react-query";

interface UsePostCommentsProps {
  postId: string;
}

export function usePostComments({ postId }: UsePostCommentsProps) {
  const queryClient = useQueryClient();

  const { data: commentsData, isLoading, isError } = useGetAllComments(postId);

  // 1. Use mutateAsync to expose the Promise
  const { mutateAsync: createCommentAsync, isPending: isCreating } =
    useCreateComment({
      mutation: {
        onSuccess: () => {
          // Refetch comments immediately on success
          queryClient.invalidateQueries({
            queryKey: getGetAllCommentsQueryKey(postId),
          });
        },
      },
    });

  // 2. Return the Promise so the Form component can 'await' it and 'catch' errors
  const addComment = async (content: string) => {
    const payload: CreateCommentRequest = { content };
    return createCommentAsync({
      postId,
      data: payload,
    });
  };

  const comments: CommentResponse[] = commentsData?.data || [];

  return {
    comments,
    isLoading,
    isError,
    isCreating,
    addComment,
  };
}

import { useToggleLike } from "@/api/generated/client/post-controller/post-controller"; // <--- CHECK THIS NAME in your generated file
import { InfiniteData, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { toast } from "sonner";

interface UsePostLikeProps {
  postId: string;
  initialIsLiked: boolean;
  initialLikeCount: number;
}

export function usePostLike({
  postId,
  initialIsLiked,
  initialLikeCount,
}: UsePostLikeProps) {
  const queryClient = useQueryClient();

  // 1. Local State for Instant Feedback
  const [isLiked, setIsLiked] = useState(initialIsLiked);
  const [likeCount, setLikeCount] = useState(initialLikeCount);

  // Sync state if navigating between posts
  useEffect(() => {
    setIsLiked(initialIsLiked);
    setLikeCount(initialLikeCount);
  }, [initialIsLiked, initialLikeCount]);

  // 2. Orval Mutation
  // Assuming the generated hook is named 'useLikePost' based on 'PUT .../like'
  const { mutate: toggleLikeApi, isPending } = useToggleLike();

  const handleToggleLike = async () => {
    if (!postId) return;

    // A. Calculate New State
    const previousLiked = isLiked;
    const previousCount = likeCount;

    const newLiked = !isLiked;
    const newCount = newLiked ? likeCount + 1 : likeCount - 1;

    // B. Optimistic Update (Local)
    setIsLiked(newLiked);
    setLikeCount(newCount);

    // C. Global Cache Update (Sync Home Feed / Lists)
    updateAllCaches(newLiked, newCount);

    // D. API Call
    toggleLikeApi(
      { postId }, // Adjust based on your generated function signature
      {
        onError: () => {
          // Revert on failure
          setIsLiked(previousLiked);
          setLikeCount(previousCount);
          updateAllCaches(previousLiked, previousCount);
          toast.error("Failed to update like");
        },
      }
    );
  };

  const updateAllCaches = (liked: boolean, count: number) => {
    // Update Infinite Lists (Home Feed, Search, etc.)
    queryClient.setQueriesData<InfiniteData<any>>(
      { queryKey: ["posts"] }, // Fuzzy match for all post lists
      (oldData) => {
        if (!oldData) return oldData;
        return {
          ...oldData,
          pages: oldData.pages.map((page) => ({
            ...page,
            data: {
              ...page.data,
              content: page.data.content?.map((p: any) =>
                p.id === postId ? { ...p, isLiked: liked, likes: count } : p
              ),
            },
          })),
        };
      }
    );
  };

  return {
    isLiked,
    likeCount,
    isLikePending: isPending,
    handleToggleLike,
  };
}

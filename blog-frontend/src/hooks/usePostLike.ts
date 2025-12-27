import {
  useLikePost,
  useUnlikePost,
} from "@/api/generated/client/post-controller/post-controller";
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

  // 1. Initialize both mutations
  const { mutate: likePostApi } = useLikePost();
  const { mutate: unlikePostApi } = useUnlikePost();

  // 2. Local State
  const [isLiked, setIsLiked] = useState(initialIsLiked);
  const [likeCount, setLikeCount] = useState(initialLikeCount);

  // Sync state if initial props change (e.g. navigation)
  useEffect(() => {
    setIsLiked(initialIsLiked);
    setLikeCount(initialLikeCount);
  }, [initialIsLiked, initialLikeCount]);

  const handleToggleLike = async () => {
    if (!postId) return;

    // A. Capture Previous State (for Rollback)
    const previousLiked = isLiked;
    const previousCount = likeCount;

    // B. Calculate New State
    const newLiked = !isLiked;
    const newCount = newLiked ? likeCount + 1 : likeCount - 1;

    // C. Optimistic Update (Local + Global Cache)
    setIsLiked(newLiked);
    setLikeCount(newCount);
    updateAllCaches(newLiked, newCount);

    // D. API Call (Conditional)
    if (newLiked) {
      // User is LIKING
      likePostApi(
        { postId },
        {
          onError: () => {
            revertState(previousLiked, previousCount);
          },
        }
      );
    } else {
      // User is UNLIKING
      unlikePostApi(
        { postId },
        {
          onError: () => {
            revertState(previousLiked, previousCount);
          },
        }
      );
    }
  };

  const revertState = (prevLiked: boolean, prevCount: number) => {
    setIsLiked(prevLiked);
    setLikeCount(prevCount);
    updateAllCaches(prevLiked, prevCount);
    toast.error("Failed to update like");
  };

  const updateAllCaches = (liked: boolean, count: number) => {
    // Update Infinite Lists (Home Feed, Search results, Profile lists)
    queryClient.setQueriesData<InfiniteData<any>>(
      { queryKey: ["posts"] }, // Matches any key starting with "posts"
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
    handleToggleLike,
  };
}

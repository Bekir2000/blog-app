import {
  useFollowUser,
  useUnfollowUser,
} from "@/api/generated/client/me-controller/me-controller";
import { UserResponse } from "@/api/generated/model";
import { useState } from "react";
import { toast } from "sonner";

interface UsePostFollowProps {
  author: UserResponse | undefined;
  currentUser: UserResponse | null | undefined;
  initialIsFollowing?: boolean;
}

export function usePostFollow({
  author,
  currentUser,
  initialIsFollowing = false,
}: UsePostFollowProps) {
  const [isFollowing, setIsFollowing] = useState(initialIsFollowing);

  const { mutate: followUser, isPending: isFollowPending } = useFollowUser();
  const { mutate: unfollowUser, isPending: isUnfollowPending } =
    useUnfollowUser();

  const isLoading = isFollowPending || isUnfollowPending;
  const isOwnPost = currentUser?.id === author?.id;

  const toggleFollow = () => {
    if (!author?.id) return;

    if (isFollowing) {
      // Handle Unfollow
      setIsFollowing(false); // Optimistic
      unfollowUser(
        { targetUserId: author.id },
        {
          onError: () => {
            setIsFollowing(true); // Revert
            toast.error("Failed to unfollow");
          },
          onSuccess: () => toast.info(`Unfollowed ${author.firstName}`),
        }
      );
    } else {
      // Handle Follow
      setIsFollowing(true); // Optimistic
      followUser(
        { targetUserId: author.id },
        {
          onError: () => {
            setIsFollowing(false); // Revert
            toast.error("Failed to follow");
          },
          onSuccess: () => toast.success(`Following ${author.firstName}`),
        }
      );
    }
  };

  return {
    isFollowing,
    isLoading,
    isOwnPost,
    toggleFollow,
  };
}

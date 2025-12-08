import {
  useBookmarkPost,
  useUnbookmarkPost,
} from "@/api/generated/client/me-controller/me-controller";
import { InfiniteData, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { toast } from "sonner";

interface UsePostBookmarkProps {
  postId: string;
  initialIsBookmarked: boolean;
}

export function usePostBookmark({
  postId,
  initialIsBookmarked,
}: UsePostBookmarkProps) {
  const queryClient = useQueryClient();

  // 1. LOCAL STATE: This makes the UI react INSTANTLY, just like your Follow button
  const [isBookmarked, setIsBookmarked] = useState(initialIsBookmarked);

  // Sync state if you navigate to a different post
  useEffect(() => {
    setIsBookmarked(initialIsBookmarked);
  }, [initialIsBookmarked]);

  const { mutate: bookmark, isPending: isBookmarkPending } = useBookmarkPost();
  const { mutate: unbookmark, isPending: isUnbookmarkPending } =
    useUnbookmarkPost();

  const isBookmarkLoading = isBookmarkPending || isUnbookmarkPending;

  const toggleBookmark = async () => {
    // Debugging: Check if function is called
    console.log("🖱️ Bookmark Toggle Clicked for ID:", postId);

    if (!postId) {
      toast.error("Error: No Post ID found");
      return;
    }

    const nextState = !isBookmarked;

    // 2. OPTIMISTIC UPDATE (Local) - Immediate visual change
    setIsBookmarked(nextState);

    // 3. GLOBAL CACHE UPDATE - Syncs Home Feed / History in background
    updateAllCaches(nextState);

    // 4. API CALL
    if (isBookmarked) {
      // Was true, now unbookmarking
      unbookmark(
        { postId },
        {
          onError: (err) => {
            console.error("❌ Unbookmark Failed", err);
            setIsBookmarked(true); // Revert local
            updateAllCaches(true); // Revert global
            toast.error("Failed to remove bookmark");
          },
        }
      );
    } else {
      // Was false, now bookmarking
      bookmark(
        { data: { postId } },
        {
          onError: (err) => {
            console.error("❌ Bookmark Failed", err);
            setIsBookmarked(false); // Revert local
            updateAllCaches(false); // Revert global
            toast.error("Failed to save bookmark");
          },
        }
      );
    }
  };

  const updateAllCaches = (newState: boolean) => {
    const listKeys = [["posts"], ["me", "bookmarks"]];
    listKeys.forEach((key) => {
      queryClient.setQueriesData<InfiniteData<any>>(
        { queryKey: key },
        (oldData) => {
          if (!oldData) return oldData;
          return {
            ...oldData,
            pages: oldData.pages.map((page) => ({
              ...page,
              data: {
                ...page.data,
                content: page.data.content?.map((p: any) =>
                  p.id === postId ? { ...p, isBookmarked: newState } : p
                ),
              },
            })),
          };
        }
      );
    });
  };

  return {
    isBookmarked, // Return local state
    isBookmarkLoading,
    toggleBookmark,
  };
}

"use client";

import { useGetAllPostsInfinite } from "@/api/generated/client/post-controller/post-controller";
import { PostWithBookmarkResponse, UserResponse } from "@/api/generated/model";
import { useEffect } from "react";
import { useInView } from "react-intersection-observer";
import { PostCard } from "./PostCard";
import { PostSkeleton } from "./PostSkeleton";

interface PostsGridProps {
  initialPosts: PostWithBookmarkResponse[] | null;
  currentUser: UserResponse | null;
}

export function PostsGrid({ initialPosts, currentUser }: PostsGridProps) {
  const { ref, inView } = useInView({
    threshold: 0,
    rootMargin: "1200px", // Prefetch when 2 screens away from bottom
  });

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useGetAllPostsInfinite(
      // 1. API Params: Must match the size used in FeedTabs (5)
      { size: 5 },
      // 2. React Query Options
      {
        query: {
          queryKey: ["posts"],

          initialData: {
            pages: [
              {
                data: initialPosts || [],
                status: 200,
                headers: {} as any,
              },
            ],
            pageParams: [0],
          },

          // Logic to calculate next page index
          getNextPageParam: (lastPage, allPages) => {
            // Access .data because lastPage is the wrapper object
            const posts = lastPage?.data;

            // If no posts, or fewer than page size, we are done
            if (!posts || posts.length === 0 || posts.length < 5) {
              return undefined;
            }

            // Otherwise, fetch next page
            return allPages.length;
          },
        },
      }
    );

  useEffect(() => {
    // Fetch next page if in view and not currently loading
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  // Flatten pages into a single list
  const allPosts = data?.pages.flatMap((page) => page.data) || [];

  return (
    <div className="gap-6 flex flex-col pb-10">
      {allPosts.map((postWithBookMark) => {
        if (!postWithBookMark?.post) return null;

        const id = postWithBookMark.post.id;

        return (
          <PostCard
            key={id}
            postWithBookMark={postWithBookMark}
            currentUser={currentUser}
          />
        );
      })}

      {/* Skeletons Loading State */}
      {hasNextPage && (
        <div ref={ref} className="flex flex-col gap-6">
          {(isFetchingNextPage || hasNextPage) && (
            <>
              {/* Render 5 skeletons to match page size */}
              {Array.from({ length: 5 }).map((_, i) => (
                <PostSkeleton key={i} />
              ))}
            </>
          )}
        </div>
      )}

      {/* End of Feed Message */}
      {!hasNextPage && allPosts.length > 0 && (
        <div className="text-center py-6 text-gray-400 text-sm">
          You've reached the end.
        </div>
      )}
    </div>
  );
}

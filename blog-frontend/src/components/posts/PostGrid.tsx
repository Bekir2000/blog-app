"use client";

import { PostWithBookmarkResponse, UserResponse } from "@/api/generated/model";
import { fetchPostsPage } from "@/lib/actions/posts/actions";
import { useEffect, useState } from "react";
import { useInView } from "react-intersection-observer";
import { PostCard } from "./PostCard";
import { PostSkeleton } from "./PostSkeleton";

interface PostsGridProps {
  initialPosts: PostWithBookmarkResponse[] | null;
  currentUser: UserResponse | null;
}

export function PostsGrid({ initialPosts, currentUser }: PostsGridProps) {
  const [posts, setPosts] = useState<PostWithBookmarkResponse[]>(
    initialPosts || []
  );
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  const { ref, inView } = useInView({
    threshold: 0,
    // Keep the large rootMargin for prefetching
    rootMargin: "1200px",
  });

  const loadMorePosts = async () => {
    if (isLoading || !hasMore) return;

    setIsLoading(true);

    try {
      const newPosts = await fetchPostsPage(page);

      if (newPosts && newPosts.length > 0) {
        setPosts((prev) => [...prev, ...newPosts]);
        setPage((prev) => prev + 1);
      } else {
        setHasMore(false);
      }
    } catch (error) {
      console.error("Error loading posts:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (inView && hasMore && !isLoading) {
      loadMorePosts();
    }
  }, [inView, hasMore, isLoading]);

  return (
    <div className="gap-6 flex flex-col pb-10">
      {posts.map((postWithBookMark, index) => {
        if (!postWithBookMark?.post) return null;
        const key = postWithBookMark.post.id || index;
        return (
          <PostCard
            key={key}
            postWithBookMark={postWithBookMark}
            currentUser={currentUser}
          />
        );
      })}

      {hasMore && (
        <div ref={ref} className="flex flex-col gap-6">
          {isLoading && (
            <>
              {/* UX FIX: Render 5 skeletons to match the Page Size (5).
                  This creates enough height so the user can keep scrolling 
                  and doesn't feel "stuck" at the bottom. 
              */}
              {Array.from({ length: 5 }).map((_, i) => (
                <PostSkeleton key={i} />
              ))}
            </>
          )}
        </div>
      )}

      {!hasMore && posts.length > 0 && (
        <div className="text-center py-6 text-gray-400 text-sm">
          You've reached the end.
        </div>
      )}
    </div>
  );
}

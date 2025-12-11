"use client";

import { UserResponse } from "@/api/generated/model";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { usePostComments } from "@/hooks/usePostComments";
import { Loader2 } from "lucide-react";
import { useEffect } from "react";
import { useInView } from "react-intersection-observer"; // 👈 Import this
import { CommentForm } from "./CommentForm";
import { CommentItem } from "./CommentItem";

interface PostCommentsProps {
  postId: string;
  currentUser?: UserResponse | null;
  commentsCount?: number;
}

export function PostComments({
  postId,
  currentUser,
  commentsCount = 0,
}: PostCommentsProps) {
  const {
    comments,
    isLoading,
    isCreating,
    addComment,
    // 👇 Destructure the new infinite scroll props
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = usePostComments({
    postId,
  });

  // 👇 Setup the "sensor"
  // When 'ref' appears on screen, 'inView' becomes true
  const { ref, inView } = useInView({
    threshold: 0,
    rootMargin: "100px", // Load when user is 100px away from bottom
  });

  // 👇 Trigger fetch automatically
  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  // Use the real length if comments are loaded, otherwise fallback to prop
  const displayCount = comments.length > 0 ? comments.length : commentsCount;

  return (
    <section className="mt-12 space-y-8" id="comments">
      <Separator />

      <h3 className="text-lg font-semibold tracking-tight">
        Comments ({displayCount})
      </h3>

      <CommentForm
        currentUser={currentUser}
        onSubmit={addComment}
        isSubmitting={isCreating}
      />

      <div className="space-y-6">
        {isLoading ? (
          // Loading Skeletons
          Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="flex gap-4">
              <Skeleton className="h-9 w-9 rounded-full" />
              <div className="space-y-2 flex-1">
                <Skeleton className="h-4 w-[150px]" />
                <Skeleton className="h-4 w-full" />
              </div>
            </div>
          ))
        ) : comments.length > 0 ? (
          <>
            {/* Render List */}
            {comments.map((comment) => (
              <CommentItem
                key={comment.id}
                postId={postId} // 👈 Pass postId down needed for Likes
                comment={comment}
                currentUserId={currentUser?.id}
              />
            ))}

            {/* 👇 The Infinite Scroll Sensor / Loader */}
            <div ref={ref} className="flex justify-center py-4 min-h-[50px]">
              {isFetchingNextPage && (
                <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
              )}
            </div>
          </>
        ) : (
          <p className="text-center text-sm text-muted-foreground py-8">
            No comments yet. Be the first to share your thoughts!
          </p>
        )}
      </div>
    </section>
  );
}

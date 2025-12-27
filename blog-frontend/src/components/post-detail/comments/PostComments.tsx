"use client";

import { CommentResponse, UserResponse } from "@/api/generated/model";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { Loader2 } from "lucide-react";
import { useEffect } from "react"; // 👈 Don't forget this
import { useInView } from "react-intersection-observer";

import { usePostComments } from "@/hooks/usePostComments";
import { CommentForm } from "./CommentForm";
import { CommentItem } from "./CommentItem";

interface PostCommentsProps {
  postId: string;
  currentUser?: UserResponse | null;
  initialCount?: number; // Renamed for clarity, logic remains similar
  onCountChange?: (count: number) => void; // 👈 NEW PROP
}

export interface CommentWithChildren extends CommentResponse {
  replies?: CommentWithChildren[];
}

export function PostComments({
  postId,
  currentUser,
  initialCount = 0,
  onCountChange, // 👈 Destructure new prop
}: PostCommentsProps) {
  const {
    comments,
    totalCount, // This comes from usePostComments (React Query)
    isLoading,
    isCreating,
    addComment,
    handleDelete,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = usePostComments({
    postId,
  });

  const { ref, inView } = useInView({
    threshold: 0,
    rootMargin: "100px",
  });

  // 1. Pagination Trigger
  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  // 2. Sync Count to Parent (PostDetail) 👇 NEW LOGIC
  useEffect(() => {
    // Only update parent if totalCount is a valid number (fetched successfully)
    if (typeof totalCount === "number" && onCountChange) {
      onCountChange(totalCount);
    }
  }, [totalCount, onCountChange]);

  // Determine what to show locally in the header
  const displayCount = totalCount !== undefined ? totalCount : initialCount;

  return (
    <section className="mt-12 space-y-8" id="comments">
      <Separator />

      <h3 className="text-lg font-semibold tracking-tight">
        Comments ({displayCount})
      </h3>

      <CommentForm
        currentUser={currentUser}
        onSubmit={(content) => addComment(content)}
        isSubmitting={isCreating}
      />

      {/* ... Rest of the render logic is identical to previous version ... */}
      <div className="space-y-6">
        {isLoading ? (
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
            {comments.map((comment) => (
              <CommentItem
                key={comment.id}
                postId={postId}
                comment={comment as CommentWithChildren}
                currentUserId={currentUser?.id}
                onReplySubmit={(content, parentId) =>
                  addComment(content, parentId)
                }
                onDelete={handleDelete}
              />
            ))}

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

"use client";

import { UserResponse } from "@/api/generated/model";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { Loader2 } from "lucide-react";
import { useEffect } from "react";
import { useInView } from "react-intersection-observer";

import { usePostComments } from "@/hooks/usePostComments";
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

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  const displayCount = comments.length > 0 ? comments.length : commentsCount;

  return (
    <section className="mt-12 space-y-8" id="comments">
      <Separator />

      <h3 className="text-lg font-semibold tracking-tight">
        Comments ({displayCount})
      </h3>

      {/* Main Form: Submits ROOT comments (parentId undefined) */}
      <CommentForm
        currentUser={currentUser}
        onSubmit={(content) => addComment(content)}
        isSubmitting={isCreating}
      />

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
                comment={comment}
                currentUserId={currentUser?.id}
                // 👇 Handle Nested Replies here
                onReplySubmit={(content, parentId) =>
                  addComment(content, parentId)
                }
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

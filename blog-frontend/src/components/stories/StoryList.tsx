"use client";

import {
  useAddNewDraft,
  useDeleteDraft,
  useDeletePost,
} from "@/api/generated/client/post-controller/post-controller";
import {
  DraftCardResponse,
  PagedResponsePostCardResponse,
  PostCardResponse,
} from "@/api/generated/model";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { InfiniteData, UseInfiniteQueryResult } from "@tanstack/react-query";
import { format } from "date-fns";
import { Edit2, Eye, MoreHorizontal, Trash2 } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useMemo } from "react";
import { useInView } from "react-intersection-observer";
import { toast } from "sonner";

type ApiPageResponse = {
  data: PagedResponsePostCardResponse;
};

interface StoryListProps {
  initialPosts: PostCardResponse[];
  queryResult: UseInfiniteQueryResult<InfiniteData<ApiPageResponse>, unknown>;
  type: "DRAFT" | "PUBLISHED";
}

export function StoryList({ initialPosts, queryResult, type }: StoryListProps) {
  const router = useRouter();
  const { ref, inView } = useInView({ threshold: 0, rootMargin: "400px" });

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, refetch } =
    queryResult;

  // --- 1. Delete Post (For Published Stories) ---
  const { mutate: deletePost } = useDeletePost({
    mutation: {
      onSuccess: () => {
        toast.success("Story deleted");
        refetch();
      },
      onError: () => toast.error("Could not delete story"),
    },
  });

  // --- 2. Delete Draft (For Drafts/Revisions) ---
  const { mutate: deleteDraft } = useDeleteDraft({
    mutation: {
      onSuccess: () => {
        toast.success("Draft discarded");
        refetch();
      },
      onError: () => toast.error("Could not delete draft"),
    },
  });

  // --- 3. Create Revision (Fork Logic) ---
  const { mutate: addDraft, isPending: isAddingDraft } = useAddNewDraft({
    mutation: {
      onSuccess: (response) => {
        toast.success("Revision created");
        // Navigate to the editor with the NEW draft ID
        router.push(
          `/posts/${response.data.postId}/drafts/${response.data.draftId}/edit`
        );
      },
      onError: () => toast.error("Could not create revision draft"),
    },
  });

  // --- Handlers ---

  const handleDelete = (post: PostCardResponse | DraftCardResponse) => {
    if (!confirm("Are you sure you want to delete this?")) return;

    if (type === "PUBLISHED") {
      deletePost({ postId: post.id! });
    } else {
      const draft = post as DraftCardResponse;
      // Safety check for parent ID
      const parentId = draft.postId || draft.id!;

      deleteDraft({
        postId: parentId,
        draftId: draft.id!,
      });
    }
  };

  const handleEdit = (post: PostCardResponse) => {
    if (type === "DRAFT") {
      // Direct Edit: Navigate to existing draft
      // @ts-ignore - Assuming API structure provides postId or logic handles it
      const parentId = post.postId || post.id!;
      router.push(`/posts/${parentId}/drafts/${post.id}/edit`);
    } else {
      // Published: Create a new Draft (Fork)
      addDraft({
        postId: post.id!,
      });
    }
  };

  // --- Infinite Scroll ---
  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  // --- Data Merging ---
  const allPosts = useMemo(() => {
    const fetchedPosts =
      data?.pages.flatMap((page) => page.data.content ?? []) || [];
    const combined = fetchedPosts.length > 0 ? fetchedPosts : initialPosts;

    const seen = new Set<string>();
    return combined.filter((post) => {
      if (!post.id || seen.has(post.id)) return false;
      seen.add(post.id);
      return true;
    });
  }, [data, initialPosts]);

  // --- Empty State ---
  if (allPosts.length === 0 && !isFetchingNextPage) {
    return (
      <div className="flex flex-col items-center justify-center py-20 text-gray-500">
        <p className="text-lg font-serif mb-2">
          {type === "DRAFT" ? "No drafts yet." : "You haven't published yet."}
        </p>
        {type === "DRAFT" && (
          <Button variant="link" asChild>
            <Link href="/new-story">Write your first story</Link>
          </Button>
        )}
      </div>
    );
  }

  return (
    <div className="space-y-6 pb-20">
      {allPosts.map((post) => {
        const displayDate =
          type === "DRAFT" ? post.updatedAt ?? post.createdAt : post.createdAt;
        return (
          <div
            key={post.id}
            className="group border-b border-gray-100 pb-6 transition-all"
          >
            <div className="flex items-start justify-between gap-4">
              <div className="flex-1">
                <div
                  onClick={() => handleEdit(post)}
                  className="cursor-pointer block group-hover:opacity-75 transition-opacity"
                >
                  <h3 className="font-bold text-lg mb-2 font-serif text-gray-900 leading-tight">
                    {post.title || "Untitled Story"}
                  </h3>
                  <p className="text-gray-500 text-sm line-clamp-2 font-serif">
                    {post.description || "No description available..."}
                  </p>
                </div>

                <div className="flex items-center gap-2 mt-3 text-xs text-gray-400 font-sans">
                  <span>
                    {type === "DRAFT" ? "Last edited" : "Published"}{" "}
                    {displayDate
                      ? format(new Date(displayDate), "MMM d, yyyy")
                      : "Unknown date"}
                  </span>
                </div>
              </div>

              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8 -mr-2 text-gray-400 hover:text-black"
                  >
                    <MoreHorizontal className="w-4 h-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-40">
                  {type === "PUBLISHED" && (
                    <DropdownMenuItem asChild>
                      <Link
                        href={`/posts/${post.id}`}
                        className="cursor-pointer"
                      >
                        <Eye className="w-4 h-4 mr-2" /> View Story
                      </Link>
                    </DropdownMenuItem>
                  )}

                  <DropdownMenuItem
                    onClick={() => handleEdit(post)}
                    disabled={isAddingDraft}
                    className="cursor-pointer"
                  >
                    <Edit2 className="w-4 h-4 mr-2" />
                    {type === "PUBLISHED" ? "Edit Revision" : "Edit Draft"}
                  </DropdownMenuItem>

                  <DropdownMenuItem
                    onClick={() => handleDelete(post)}
                    className="text-red-600 focus:text-red-600 focus:bg-red-50 cursor-pointer"
                  >
                    <Trash2 className="w-4 h-4 mr-2" /> Delete
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>
        );
      })}

      {/* Loading Skeleton */}
      {(isFetchingNextPage || hasNextPage) && (
        <div ref={ref} className="space-y-8 pt-4">
          {[1, 2, 3].map((i) => (
            <div key={i} className="animate-pulse flex flex-col gap-2">
              <div className="h-6 bg-gray-100 rounded w-3/4" />
              <div className="h-4 bg-gray-50 rounded w-full" />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

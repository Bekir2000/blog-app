"use client";

import { useDeletePost } from "@/api/generated/client/post-controller/post-controller";
import {
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
import { Edit2, MoreHorizontal, Trash2 } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useMemo } from "react";
import { useInView } from "react-intersection-observer";
import { toast } from "sonner";

// Define the shape of one "page" of data from your API client
type ApiPageResponse = {
  data: PagedResponsePostCardResponse;
  status: number;
};

interface StoryListProps {
  initialPosts: PostCardResponse[];
  // Strictly typed query result
  queryResult: UseInfiniteQueryResult<InfiniteData<ApiPageResponse>, unknown>;
  type: "DRAFT" | "PUBLISHED";
}

export function StoryList({ initialPosts, queryResult, type }: StoryListProps) {
  const router = useRouter();
  const { ref, inView } = useInView({ threshold: 0, rootMargin: "600px" });

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage, refetch } =
    queryResult;

  const { mutate: deletePost } = useDeletePost({
    mutation: {
      onSuccess: () => {
        toast.success("Story deleted");
        refetch();
      },
      onError: () => toast.error("Could not delete story"),
    },
  });

  const handleDelete = (id: string) => {
    if (confirm("Are you sure you want to delete this story?")) {
      deletePost({ postId: id });
    }
  };

  useEffect(() => {
    if (inView && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [inView, hasNextPage, isFetchingNextPage, fetchNextPage]);

  // Combine SSR data + Client Infinite Data
  const allPosts = useMemo(() => {
    // If we have fetched data on the client, use it (it effectively replaces initialPosts)
    if (data && data.pages.length > 0) {
      // flatMap over the pages. Each page has a .data property which has .content
      const rawPosts =
        data.pages.flatMap((page) => page.data.content ?? []) || [];

      const seen = new Set<string>();
      return rawPosts.filter((post) => {
        if (!post?.id || seen.has(post.id)) return false;
        seen.add(post.id);
        return true;
      });
    }
    // Fallback to SSR initial data
    return initialPosts || [];
  }, [data, initialPosts]);

  if (allPosts.length === 0 && !isFetchingNextPage) {
    return (
      <div className="text-center py-20 text-gray-500">
        You have no {type === "DRAFT" ? "drafts" : "published stories"}.
      </div>
    );
  }

  return (
    <div className="space-y-6 pb-10">
      {allPosts.map((post) => (
        <div key={post.id} className="group border-b border-gray-100 pb-6">
          <div className="flex items-start justify-between">
            <div>
              <Link
                href={`/posts/${post.id}/edit`}
                className="block group-hover:opacity-75 transition-opacity"
              >
                <h3 className="font-bold text-lg mb-1">{post.title}</h3>
                <p className="text-gray-500 text-sm line-clamp-1">
                  {post.description || "No description"}
                </p>
              </Link>
              <div className="flex items-center gap-2 mt-2 text-xs text-gray-400">
                <span>
                  {type === "DRAFT" ? "Last edited" : "Published"}{" "}
                  {post.createdAt
                    ? format(new Date(post.createdAt), "MMM d, yyyy")
                    : "recently"}
                </span>
              </div>
            </div>

            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon">
                  <MoreHorizontal className="w-4 h-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem
                  onClick={() => router.push(`/posts/${post.id}/edit`)}
                >
                  <Edit2 className="w-4 h-4 mr-2" /> Edit
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={() => handleDelete(post.id!)}
                  className="text-red-600 focus:text-red-600"
                >
                  <Trash2 className="w-4 h-4 mr-2" /> Delete
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      ))}

      {(isFetchingNextPage || hasNextPage) && (
        <div ref={ref} className="space-y-6">
          {[1, 2].map((i) => (
            <div key={i} className="h-24 bg-gray-50 animate-pulse rounded-md" />
          ))}
        </div>
      )}
    </div>
  );
}

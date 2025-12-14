"use client";

import {
  useDeletePost,
  useGetDrafts,
} from "@/api/generated/client/post-controller/post-controller";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { format } from "date-fns";
import { ChevronLeft, Edit2, MoreHorizontal, Trash2 } from "lucide-react"; // 1. Added ChevronLeft
import Link from "next/link";
import { useRouter } from "next/navigation";
import { toast } from "sonner";

export default function StoriesPage() {
  const router = useRouter();

  // 1. Fetch Drafts
  const { data: drafts, isLoading, refetch } = useGetDrafts();

  // 2. Delete Hook
  const { mutate: deletePost } = useDeletePost({
    mutation: {
      onSuccess: () => {
        toast.success("Story deleted");
        refetch(); // Refresh list
      },
      onError: () => toast.error("Could not delete story"),
    },
  });

  const handleDelete = (id: string) => {
    if (confirm("Are you sure you want to delete this story?")) {
      deletePost({ postId: id });
    }
  };

  return (
    <div className="container max-w-4xl mx-auto py-10 px-6">
      {/* --- HEADER WITH BACK BUTTON --- */}
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-2">
          {/* 2. The Back Button */}
          <Button
            variant="ghost"
            size="icon"
            onClick={() => router.back()}
            className="-ml-3 text-gray-500 hover:text-black"
          >
            <ChevronLeft className="w-6 h-6" />
          </Button>
          <h1 className="text-3xl font-serif font-bold">Your Stories</h1>
        </div>

        <Button
          asChild
          className="rounded-full bg-green-600 hover:bg-green-700"
        >
          <Link href="/new-story">Write a story</Link>
        </Button>
      </div>

      <Tabs defaultValue="drafts" className="w-full">
        <TabsList className="mb-6 border-b w-full justify-start rounded-none h-auto p-0 bg-transparent">
          <TabsTrigger
            value="drafts"
            className="data-[state=active]:border-black data-[state=active]:shadow-none border-b-2 border-transparent rounded-none pb-3 px-1 mr-6"
          >
            Drafts {drafts?.data?.length ? `(${drafts.data.length})` : ""}
          </TabsTrigger>
          <TabsTrigger
            value="published"
            className="data-[state=active]:border-black data-[state=active]:shadow-none border-b-2 border-transparent rounded-none pb-3 px-1"
          >
            Published
          </TabsTrigger>
        </TabsList>

        {/* --- DRAFTS TAB --- */}
        <TabsContent value="drafts">
          {isLoading ? (
            <p className="text-gray-500 py-8">Loading drafts...</p>
          ) : drafts?.data?.length === 0 ? (
            <div className="text-center py-20 text-gray-500">
              You have no drafts.
            </div>
          ) : (
            <div className="space-y-6">
              {drafts?.data?.map((post) => (
                <div
                  key={post.id}
                  className="group border-b border-gray-100 pb-6"
                >
                  <div className="flex items-start justify-between">
                    <div>
                      <Link
                        href={`/p/${post.id}/edit`}
                        className="block group-hover:opacity-75 transition-opacity"
                      >
                        <h3 className="font-bold text-lg mb-1">{post.title}</h3>
                        <p className="text-gray-500 text-sm line-clamp-1">
                          {post.description}
                        </p>
                      </Link>
                      <div className="flex items-center gap-2 mt-2 text-xs text-gray-400">
                        <span>
                          Last edited{" "}
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
                          onClick={() => router.push(`/p/${post.id}/edit`)}
                        >
                          <Edit2 className="w-4 h-4 mr-2" /> Edit Draft
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
            </div>
          )}
        </TabsContent>

        {/* --- PUBLISHED TAB --- */}
        <TabsContent value="published">
          <div className="text-center py-20 text-gray-500">
            Published posts list works the same way as drafts.
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}

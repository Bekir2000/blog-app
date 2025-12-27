"use client";

import { useGetDraftById } from "@/api/generated/client/post-controller/post-controller";
import { PostEditor } from "@/components/editor/PostEditor";
import { Loader2 } from "lucide-react";
import { notFound, useParams } from "next/navigation";

export default function EditStoryPage() {
  const params = useParams();
  const postId = params.id as string;
  const draftId = params.draftId as string;

  // Fetch the existing post
  const { data: post, isLoading, error } = useGetDraftById(postId, draftId);

  if (isLoading) {
    return (
      <div className="h-screen flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-gray-300" />
      </div>
    );
  }

  if (error || !post) {
    return notFound();
  }

  // Pass existing data to the editor
  return <PostEditor draftToEdit={post.data} />;
}

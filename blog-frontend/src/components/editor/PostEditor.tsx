"use client";

import {
  useCreatePost,
  useUpdatePost,
} from "@/api/generated/client/post-controller/post-controller";
import {
  PostDetailResponse,
  PostRequest,
  PostRequestStatus,
  UserResponse,
} from "@/api/generated/model";
import { EditorNavbar } from "@/components/navbar/EditorNavbar";
import { Button } from "@/components/ui/button";
import { useAutoResizeTextArea } from "@/hooks/useAutoResizeTextArea";
import { Hash, Search, X } from "lucide-react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { UnsplashModal } from "./UnsplashModal";

interface PostEditorProps {
  currentUser: UserResponse | null;
  postToEdit?: PostDetailResponse;
}

export function PostEditor({ currentUser, postToEdit }: PostEditorProps) {
  const router = useRouter();
  const isEditing = !!postToEdit;

  // --- State ---
  const [title, setTitle] = useState(postToEdit?.title || "");
  const [content, setContent] = useState(postToEdit?.content || "");
  const [imageUrl, setImageUrl] = useState<string>(postToEdit?.imageUrl || "");
  const [tags, setTags] = useState<string[]>(
    postToEdit?.tags?.map((t) => t.name || "") || []
  );
  const [tagInput, setTagInput] = useState("");
  // Default category to "General" if missing (common for drafts)
  const [categoryName, setCategoryName] = useState(
    postToEdit?.category?.name || "General"
  );
  const [unsplashOpen, setUnsplashOpen] = useState(false);

  // Refs for auto-growing textareas
  const titleRef = useAutoResizeTextArea(title);
  const contentRef = useAutoResizeTextArea(content);

  // --- API Hooks ---
  // ✅ Removed useCreatePostDraft (endpoint deleted)
  const createMutation = useCreatePost();
  const updateMutation = useUpdatePost();

  const isPending = createMutation.isPending || updateMutation.isPending;

  // --- Submit Handler ---
  const handleSubmit = (targetStatus: PostRequestStatus) => {
    // 1. Basic Validation (Required for EVERYTHING)
    if (!title.trim()) {
      toast.error("Please add a title.");
      return;
    }

    // 2. Strict Validation (Required ONLY for Publishing)
    if (targetStatus === PostRequestStatus.PUBLISHED) {
      if (!content.trim()) {
        toast.error("You cannot publish an empty story.");
        return;
      }
      if (!imageUrl) {
        toast.error("Please add a cover image before publishing.");
        return;
      }
    }

    // Generate a default description if none exists
    // (Note: Backend handles optionality for drafts)
    const cleanContent = content.replace(/<[^>]*>?/gm, "");
    const description = cleanContent.slice(0, 140).trim() || "";

    // ✅ Construct Unified Payload
    const payload: PostRequest = {
      title,
      content,
      description: description || undefined, // Send undefined if empty so backend ignores it for draft
      imageUrl: imageUrl || undefined,
      status: targetStatus,
      tags: tags.length > 0 ? tags.map((tag) => ({ name: tag })) : undefined,
      category: categoryName ? { name: categoryName } : undefined,
    };

    const mutationOptions = {
      onSuccess: (data: any) => {
        toast.success(
          targetStatus === "DRAFT" ? "Draft saved" : "Story published!"
        );
        // If it was a draft creation, maybe redirect to edit page or dashboard
        router.push("/me/stories");
        router.refresh();
      },
      onError: (err: any) => {
        // Backend validation errors (e.g. 400 Bad Request)
        const msg = err?.response?.data?.detail || "Something went wrong.";
        toast.error(msg);
      },
    };

    // --- Logic Branching ---
    if (isEditing && postToEdit?.id) {
      // UPDATE (PUT) - Handles both saving draft updates AND publishing drafts
      updateMutation.mutate(
        { postId: postToEdit.id, data: payload },
        mutationOptions
      );
    } else {
      // CREATE (POST) - Handles creating new Drafts AND new Published posts
      createMutation.mutate({ data: payload }, mutationOptions);
    }
  };

  const handleUnsplashSelect = (url: string) => {
    setImageUrl(url);
    setUnsplashOpen(false);
  };

  const handleTagKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" || e.key === ",") {
      e.preventDefault();
      const newTag = tagInput.trim();
      if (newTag && !tags.includes(newTag) && tags.length < 5) {
        setTags([...tags, newTag]);
      }
      setTagInput("");
    }
  };

  return (
    <div className="min-h-screen bg-white dark:bg-zinc-950 pb-20">
      <EditorNavbar
        currentUser={currentUser}
        isPending={isPending}
        isEditing={isEditing}
        onSaveDraft={() => handleSubmit(PostRequestStatus.DRAFT)}
        onPublish={() => handleSubmit(PostRequestStatus.PUBLISHED)}
      />

      <UnsplashModal
        open={unsplashOpen}
        onOpenChange={setUnsplashOpen}
        onSelect={handleUnsplashSelect}
      />

      <div className="mx-auto max-w-[720px] px-6 py-10 md:py-14">
        {/* Cover Image Section */}
        <div className="group relative mb-8 transition-all">
          {imageUrl ? (
            <div className="relative aspect-video w-full overflow-hidden rounded-lg border border-gray-100 shadow-sm bg-gray-50">
              <Image
                src={imageUrl}
                alt="Cover"
                fill
                className="object-cover"
                onError={() => setImageUrl("")}
              />
              <Button
                variant="secondary"
                size="icon"
                className="absolute top-2 right-2 rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                onClick={() => setImageUrl("")}
              >
                <X className="w-4 h-4" />
              </Button>
            </div>
          ) : (
            <div className="rounded-lg border-2 border-dashed border-gray-200 p-8 text-center hover:bg-gray-50 transition-colors">
              <Button
                variant="ghost"
                onClick={() => setUnsplashOpen(true)}
                className="text-gray-500 hover:text-black"
              >
                <Search className="mr-2 w-4 h-4" /> Add Cover Image
              </Button>
            </div>
          )}
        </div>

        {/* Title */}
        <textarea
          ref={titleRef}
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Title"
          className="w-full resize-none overflow-hidden bg-transparent font-serif text-4xl font-extrabold leading-tight placeholder:text-gray-300 focus:outline-none dark:text-gray-50 dark:placeholder:text-gray-700 md:text-5xl mb-6"
          rows={1}
          spellCheck={false}
        />

        {/* Tags */}
        <div className="mb-8 flex flex-wrap items-center gap-2">
          {tags.map((tag) => (
            <span
              key={tag}
              className="flex items-center gap-1 rounded-full bg-gray-100 px-3 py-1 text-sm text-gray-600"
            >
              <Hash className="w-3 h-3 opacity-50" /> {tag}
              <X
                className="w-3 h-3 cursor-pointer hover:text-red-500"
                onClick={() => setTags(tags.filter((t) => t !== tag))}
              />
            </span>
          ))}
          <input
            value={tagInput}
            onChange={(e) => setTagInput(e.target.value)}
            onKeyDown={handleTagKeyDown}
            placeholder={tags.length === 0 ? "Add a tag..." : "Add another..."}
            className="bg-transparent text-sm focus:outline-none min-w-[100px] text-gray-500 placeholder:text-gray-300"
          />
        </div>

        {/* Content */}
        <textarea
          ref={contentRef}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Tell your story..."
          className="w-full resize-none overflow-hidden bg-transparent font-serif text-xl leading-relaxed text-gray-800 placeholder:text-gray-300 focus:outline-none min-h-[300px]"
          spellCheck={false}
        />
      </div>
    </div>
  );
}

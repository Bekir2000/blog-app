"use client";

import { useCreatePost } from "@/api/generated/client/post-controller/post-controller";
import { PostRequestStatus, UserResponse } from "@/api/generated/model";
import { Button } from "@/components/ui/button";
import { useAutoResizeTextArea } from "@/hooks/useAutoResizeTextArea";
import { Folder, Hash, Search, X } from "lucide-react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { toast } from "sonner";
import { EditorNavbar } from "./EditorNavbar";
import { UnsplashModal } from "./UnsplashModal";

interface PostEditorProps {
  currentUser: UserResponse | null;
}

export function PostEditor({ currentUser }: PostEditorProps) {
  const router = useRouter();

  // --- State ---
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [imageUrl, setImageUrl] = useState<string>("");
  const [imageCredit, setImageCredit] = useState(""); // Optional credit text

  // Tags & Category
  const [tags, setTags] = useState<string[]>([]);
  const [tagInput, setTagInput] = useState("");
  const [categoryName, setCategoryName] = useState("");
  const [showCategoryInput, setShowCategoryInput] = useState(false);

  // Unsplash UI State
  const [unsplashOpen, setUnsplashOpen] = useState(false);

  // --- Refs for auto-resize ---
  const titleRef = useAutoResizeTextArea(title);
  const contentRef = useAutoResizeTextArea(content);

  // --- API Mutation ---
  const { mutate: createPost, isPending } = useCreatePost({
    mutation: {
      onSuccess: (response) => {
        toast.success("Story published successfully!");
        if (response.data?.id) {
          router.push(`/posts/${response.data.id}`);
        } else {
          router.push("/");
        }
      },
      onError: (error: any) => {
        console.error("Publish Error:", error);
        toast.error("Failed to publish. Please check your inputs.");
      },
    },
  });

  // --- Image Handler: Unsplash Selection Only ---
  const handleUnsplashSelect = (url: string, photographerName: string) => {
    setImageUrl(url);
    setImageCredit(`Photo by ${photographerName} on Unsplash`);
    setUnsplashOpen(false);
  };

  // --- Tag Handlers ---
  const handleTagKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" || e.key === ",") {
      e.preventDefault();
      const newTag = tagInput.trim();
      // Backend Validation: Max 10 tags
      if (newTag && !tags.includes(newTag) && tags.length < 10) {
        setTags([...tags, newTag]);
      }
      setTagInput("");
    }
  };

  const removeTag = (tagToRemove: string) => {
    setTags(tags.filter((tag) => tag !== tagToRemove));
  };

  // --- Publish Logic ---
  const handlePublish = () => {
    const cleanContent = content.replace(/<[^>]*>?/gm, "");
    const description =
      cleanContent.slice(0, 140).trim() || "Read this story on Blogium.";

    createPost({
      data: {
        title,
        content,
        description,
        imageUrl,
        status: PostRequestStatus.PUBLISHED,
        tags: tags.map((tag) => ({ name: tag })) as any,
        category: { name: categoryName || "General" } as any,
      },
    });
  };

  // --- Validation Rules ---
  const canPublish =
    title.length >= 3 &&
    title.length <= 100 &&
    content.length >= 10 &&
    imageUrl.length > 0 &&
    tags.length > 0;

  return (
    <div className="min-h-screen bg-white dark:bg-zinc-950 pb-20">
      <EditorNavbar
        user={currentUser}
        isPublishing={isPending}
        onPublish={handlePublish}
        canPublish={canPublish}
      />

      {/* Unsplash Dialog */}
      <UnsplashModal
        open={unsplashOpen}
        onOpenChange={setUnsplashOpen}
        onSelect={handleUnsplashSelect}
      />

      <div className="mx-auto max-w-[720px] px-6 py-10 md:py-14">
        {/* --- Image Selection Area (Unsplash Only) --- */}
        <div className="group relative mb-8 transition-all">
          {imageUrl ? (
            <div className="relative">
              <div className="relative aspect-video w-full overflow-hidden rounded-lg border border-gray-100 shadow-sm">
                <Image
                  src={imageUrl}
                  alt="Cover"
                  fill
                  className="object-cover"
                />
                <Button
                  variant="secondary"
                  size="icon"
                  className="absolute top-2 right-2 rounded-full opacity-0 group-hover:opacity-100 transition-opacity"
                  onClick={() => {
                    setImageUrl("");
                    setImageCredit("");
                  }}
                >
                  <X className="w-4 h-4" />
                </Button>
              </div>
              {imageCredit && (
                <p className="text-center text-xs text-gray-400 mt-2 italic">
                  {imageCredit}
                </p>
              )}
            </div>
          ) : (
            <div className="rounded-lg border-2 border-dashed border-gray-200 p-8 text-center hover:bg-gray-50 transition-colors">
              <div className="flex flex-col items-center gap-4">
                <p className="text-sm text-gray-500 font-medium">
                  Add a cover image
                </p>

                {/* Single Button for Unsplash */}
                <Button
                  variant="outline"
                  onClick={() => setUnsplashOpen(true)}
                  className="text-gray-600"
                >
                  <Search className="mr-2 w-4 h-4" /> Search Unsplash
                </Button>

                <p className="text-xs text-gray-400 mt-2">
                  Required for publication{" "}
                  <span className="text-red-500">*</span>
                </p>
              </div>
            </div>
          )}
        </div>

        {/* --- Title Input --- */}
        <textarea
          ref={titleRef}
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Title"
          className="w-full resize-none overflow-hidden bg-transparent font-serif text-4xl font-extrabold leading-tight placeholder:text-gray-300 focus:outline-none dark:text-gray-50 dark:placeholder:text-gray-700 md:text-5xl mb-6"
          rows={1}
          maxLength={100}
        />

        {/* --- Meta Bar (Tags & Category) --- */}
        <div className="mb-8 flex flex-wrap items-center gap-4">
          {/* Category */}
          <div className="relative flex items-center">
            {showCategoryInput || categoryName ? (
              <div className="flex items-center gap-1 rounded-full bg-blue-50 px-3 py-1 text-sm text-blue-600 dark:bg-blue-900/30 dark:text-blue-300">
                <Folder className="w-3 h-3" />
                <input
                  value={categoryName}
                  onChange={(e) => setCategoryName(e.target.value)}
                  placeholder="Category"
                  className="bg-transparent focus:outline-none min-w-[60px] max-w-[100px]"
                  autoFocus
                />
              </div>
            ) : (
              <button
                onClick={() => setShowCategoryInput(true)}
                className="flex items-center gap-1 text-sm text-gray-400 hover:text-gray-600 transition-colors"
              >
                <Folder className="w-4 h-4" />
                <span>Add Category</span>
              </button>
            )}
          </div>

          <div className="h-4 w-[1px] bg-gray-200 dark:bg-gray-800"></div>

          {/* Tags */}
          <div className="flex flex-wrap items-center gap-2">
            {tags.map((tag) => (
              <span
                key={tag}
                className="flex items-center gap-1 rounded-full bg-gray-100 px-3 py-1 text-sm text-gray-600 dark:bg-gray-800 dark:text-gray-300"
              >
                <Hash className="w-3 h-3 opacity-50" />
                {tag}
                <X
                  className="w-3 h-3 cursor-pointer hover:text-red-500 ml-1"
                  onClick={() => removeTag(tag)}
                />
              </span>
            ))}

            {tags.length < 10 && (
              <input
                value={tagInput}
                onChange={(e) => setTagInput(e.target.value)}
                onKeyDown={handleTagKeyDown}
                placeholder={
                  tags.length === 0
                    ? "Add tags (Required)..."
                    : "Add another..."
                }
                className="bg-transparent text-sm focus:outline-none placeholder:text-gray-300 min-w-[120px] text-gray-600"
              />
            )}
            {tags.length === 0 && (
              <span className="text-xs text-red-400">*</span>
            )}
          </div>
        </div>

        {/* --- Main Content --- */}
        <textarea
          ref={contentRef}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Tell your story..."
          className="w-full resize-none overflow-hidden bg-transparent font-serif text-xl leading-relaxed text-gray-800 placeholder:text-gray-300 focus:outline-none dark:text-gray-300 dark:placeholder:text-gray-700 min-h-[300px]"
          minLength={10}
        />
      </div>
    </div>
  );
}

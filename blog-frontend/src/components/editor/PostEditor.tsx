"use client";

import { useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, ImagePlus, MoreHorizontal, X } from "lucide-react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import React, { useEffect, useRef, useState } from "react";

// Shadcn Imports
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

// API & Models
import {
  getGetDraftByIdQueryKey,
  useAddNewDraft,
  useCreateFirstDraft,
  usePublishPost,
  useSafeDraft,
} from "@/api/generated/client/post-controller/post-controller"; // Adjust path
import type {
  DraftDetailResponse,
  PostDraftRequest,
  PostDraftRequestCategory,
} from "@/api/generated/model"; // Adjust path

// Your Modal
import { UnsplashModal } from "@/components/editor/UnsplashModal";
import { toast } from "sonner";

// --- CONSTANTS ---
const CATEGORIES: PostDraftRequestCategory[] = [
  "TECHNOLOGY",
  "LIFESTYLE",
  "TUTORIAL",
  "NEWS",
  "PERSONAL",
  "CODING",
] as any;

interface PostEditorProps {
  draftToEdit?: DraftDetailResponse;
}

export const PostEditor: React.FC<PostEditorProps> = ({ draftToEdit }) => {
  const router = useRouter();
  const queryClient = useQueryClient();

  // Refs for auto-resizing
  const titleRef = useRef<HTMLTextAreaElement>(null);
  const contentRef = useRef<HTMLTextAreaElement>(null);

  // --- 1. LOCAL STATE ---
  const [title, setTitle] = useState(draftToEdit?.title || "");
  const [content, setContent] = useState(draftToEdit?.content || "");
  const [imageUrl, setImageUrl] = useState(draftToEdit?.imageUrl || "");

  const [category, setCategory] = useState<
    PostDraftRequestCategory | undefined
  >(draftToEdit?.category as PostDraftRequestCategory | undefined);
  const [tags, setTags] = useState<string[]>(draftToEdit?.tags || []);
  const [tagInput, setTagInput] = useState("");

  // UI State
  const [isUnsplashOpen, setIsUnsplashOpen] = useState(false);
  const [showSettings, setShowSettings] = useState(false);

  // ID State
  const [postId, setPostId] = useState<string | undefined>(draftToEdit?.postId);
  const [draftId, setDraftId] = useState<string | undefined>(draftToEdit?.id);

  // Sync Data
  useEffect(() => {
    const data = draftToEdit;
    if (data) {
      setTitle(data.title || "");
      setContent(data.content || "");
      setImageUrl(data.imageUrl || "");
      setCategory(data.category as PostDraftRequestCategory | undefined);
      setTags(data.tags || []);
    }
  }, [draftToEdit]);

  // Auto-resize logic
  const autoResize = (elem: HTMLTextAreaElement | null) => {
    if (elem) {
      elem.style.height = "auto";
      elem.style.height = elem.scrollHeight + "px";
    }
  };

  useEffect(() => {
    autoResize(titleRef.current);
  }, [title]);
  useEffect(() => {
    autoResize(contentRef.current);
  }, [content]);

  // --- 3. MUTATIONS ---
  const createFirstDraftMutation = useCreateFirstDraft();
  const addNewDraftMutation = useAddNewDraft();
  const saveDraftMutation = useSafeDraft();
  const publishMutation = usePublishPost({
    mutation: {
      onSuccess: () => {
        toast.success("Post published successfully!");
        // Navigate to the editor with the NEW draft ID
        // Assuming API returns: { postId: "...", draftId: "..." }
        router.push(`/post-feed/${postId}`);
      },
      onError: () => toast.error("Could not create revision draft"),
    },
  });

  const isSaving =
    createFirstDraftMutation.isPending ||
    addNewDraftMutation.isPending ||
    saveDraftMutation.isPending;

  // --- 4. HANDLERS ---
  const handleAddTag = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault();
      const trimmed = tagInput.trim();
      if (trimmed && !tags.includes(trimmed)) {
        if (tags.length >= 10) return; // Silent fail or toast
        setTags([...tags, trimmed]);
        setTagInput("");
      }
    }
  };

  const removeTag = (tagToRemove: string) => {
    setTags(tags.filter((t) => t !== tagToRemove));
  };

  const handleSave = async (silent = false) => {
    if (!silent && title.length < 3) return alert("Title too short"); // Use Toast in real app

    const payload: PostDraftRequest = {
      title,
      content,
      imageUrl: imageUrl || undefined,
      category,
      tags,
    };

    try {
      if (!postId) {
        const res = await createFirstDraftMutation.mutateAsync({
          data: payload,
        });
        setPostId(res.data.postId);
        setDraftId(res.data.draftId);
      } else if (postId && draftId) {
        await saveDraftMutation.mutateAsync({ postId, draftId, data: payload });
      }
      if (postId && draftId) {
        await queryClient.invalidateQueries({
          // This generates the correct key automatically:
          queryKey: getGetDraftByIdQueryKey(postId, draftId),
        });
      }
      if (!silent) console.log("Saved");
      router.back();
    } catch (error) {
      console.error("Save failed", error);
    }
  };

  const handlePublish = async () => {
    if (!postId || !draftId) return alert("Save draft first");

    // Final save before publish
    await saveDraftMutation.mutateAsync({
      postId,
      draftId,
      data: { title, content, imageUrl, category, tags },
    });

    try {
      await publishMutation.mutateAsync({ postId, draftId });
      setDraftId(undefined);
      queryClient.invalidateQueries({ queryKey: [`/api/v1/posts/${postId}`] });
      router.push(`/post-feed/${postId}`);
    } catch (error) {
      console.error("Publish failed", error);
    }
  };

  return (
    <div className="min-h-screen bg-background text-foreground pb-40">
      {/* --- HEADER --- */}
      <nav className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container flex h-14 max-w-5xl items-center justify-between px-4 md:px-6">
          <div className="flex items-center gap-4">
            {/* Back Button (Optional) */}
            <Button
              onClick={() => router.back()}
              variant="ghost"
              size="icon"
              className="text-muted-foreground"
            >
              <ArrowLeft className="h-4 w-4" />
            </Button>

            <div className="flex flex-col">
              <span className="text-sm font-semibold tracking-tight">
                {draftId ? "Draft" : postId ? "Editing Live" : "New Story"}
              </span>
              <span className="text-[10px] text-muted-foreground">
                {isSaving ? "Saving..." : "Saved locally"}
              </span>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleSave(false)}
              disabled={isSaving}
              className="hidden sm:flex"
            >
              Save Draft
            </Button>

            <Button
              size="sm"
              onClick={handlePublish}
              disabled={isSaving || !postId}
              className="bg-green-600 hover:bg-green-700 text-white"
            >
              Publish
            </Button>

            <Button
              variant="ghost"
              size="icon"
              onClick={() => setShowSettings(!showSettings)}
              className={showSettings ? "bg-accent text-accent-foreground" : ""}
            >
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </nav>

      {/* --- EDITOR CONTENT --- */}
      <main className="container max-w-3xl mx-auto px-4 md:px-6 mt-12 animate-in fade-in slide-in-from-bottom-4 duration-700">
        {/* Cover Image */}
        <div className="group relative mb-8">
          {!imageUrl ? (
            <Button
              variant="outline"
              className="h-auto py-8 w-full border-dashed text-muted-foreground hover:text-foreground hover:bg-accent/50"
              onClick={() => setIsUnsplashOpen(true)}
            >
              <ImagePlus className="mr-2 h-4 w-4" />
              Add a cover image
            </Button>
          ) : (
            <div className="relative w-full aspect-video md:aspect-[2/1] rounded-lg overflow-hidden bg-muted shadow-sm">
              <Image src={imageUrl} alt="Cover" fill className="object-cover" />
              {/* Hover Overlay */}
              <div className="absolute inset-0 bg-black/0 group-hover:bg-black/10 transition-colors flex items-center justify-center opacity-0 group-hover:opacity-100 gap-2">
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => setIsUnsplashOpen(true)}
                >
                  Change
                </Button>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => setImageUrl("")}
                >
                  Remove
                </Button>
              </div>
            </div>
          )}
        </div>

        {/* Title */}
        <textarea
          ref={titleRef}
          placeholder="Title"
          className="w-full resize-none overflow-hidden bg-transparent text-4xl md:text-5xl font-bold font-serif leading-tight placeholder:text-muted-foreground/40 focus:outline-none mb-4"
          rows={1}
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              contentRef.current?.focus();
            }
          }}
        />

        {/* Content */}
        <textarea
          ref={contentRef}
          placeholder="Tell your story..."
          className="w-full resize-none overflow-hidden bg-transparent text-lg md:text-xl font-serif leading-relaxed text-foreground placeholder:text-muted-foreground/40 focus:outline-none min-h-[50vh]"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
      </main>

      {/* --- SETTINGS SECTION (Slide Down) --- */}
      {showSettings && (
        <div className="fixed bottom-0 left-0 right-0 border-t bg-background/95 backdrop-blur p-6 z-40 shadow-2xl animate-in slide-in-from-bottom-10">
          <div className="max-w-3xl mx-auto space-y-6">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-medium uppercase tracking-wider text-muted-foreground">
                Publishing Details
              </h3>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setShowSettings(false)}
                className="h-6 w-6"
              >
                <X className="h-4 w-4" />
              </Button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
              {/* Category Select */}
              <div className="space-y-2">
                <label className="text-sm font-medium">Category</label>
                <Select
                  value={category || ""}
                  onValueChange={(val) =>
                    setCategory(val as PostDraftRequestCategory)
                  }
                >
                  <SelectTrigger className="w-full">
                    <SelectValue placeholder="Select a topic..." />
                  </SelectTrigger>
                  <SelectContent>
                    {CATEGORIES.map((cat) => (
                      <SelectItem key={cat} value={cat}>
                        {cat}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              {/* Tags Input */}
              <div className="space-y-2">
                <label className="text-sm font-medium flex justify-between">
                  Tags{" "}
                  <span className="text-xs text-muted-foreground font-normal">
                    Max 10
                  </span>
                </label>
                <div className="min-h-[2.5rem] p-2 border rounded-md bg-background flex flex-wrap gap-2 focus-within:ring-2 focus-within:ring-ring focus-within:ring-offset-2">
                  {tags.map((tag) => (
                    <Badge
                      key={tag}
                      variant="secondary"
                      className="hover:bg-destructive hover:text-destructive-foreground cursor-pointer"
                      onClick={() => removeTag(tag)}
                    >
                      {tag} &times;
                    </Badge>
                  ))}
                  {tags.length < 10 && (
                    <input
                      type="text"
                      className="flex-1 bg-transparent outline-none text-sm min-w-[80px] placeholder:text-muted-foreground"
                      placeholder={tags.length === 0 ? "Add tags..." : ""}
                      value={tagInput}
                      onChange={(e) => setTagInput(e.target.value)}
                      onKeyDown={handleAddTag}
                    />
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* --- MODAL --- */}
      <UnsplashModal
        open={isUnsplashOpen}
        onOpenChange={setIsUnsplashOpen}
        onSelect={(url) => {
          setImageUrl(url);
          setIsUnsplashOpen(false);
        }}
      />
    </div>
  );
};

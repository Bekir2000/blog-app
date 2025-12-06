"use client";

import { PostDetailResponse, UserResponse } from "@/api/generated/model";
// 👇 IMPORT YOUR GENERATED HOOKS HERE (Adjust path if needed)
import {
  useFollowUser,
  useUnfollowUser,
} from "@/api/generated/client/me-controller/me-controller";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { format } from "date-fns";
import {
  Bookmark,
  MessageCircle,
  MoreHorizontal,
  PlayCircle,
  Share2,
  ThumbsUp,
} from "lucide-react";
import Image from "next/image";
import { useEffect, useState } from "react";
import { toast } from "sonner"; // Assuming you use Sonner for toasts

interface PostDetailProps {
  post: PostDetailResponse | null;
  currentUser?: UserResponse | null;
}

export function PostDetail({
  post: initialPost,
  currentUser,
}: PostDetailProps) {
  // 1. Local State for Optimistic UI (Instant feedback)
  const [isFollowing, setIsFollowing] = useState(
    initialPost?.followingAuthor ?? false
  );

  // 2. Sync local state if the prop changes (e.g. navigation)
  useEffect(() => {
    if (initialPost) {
      setIsFollowing(initialPost.followingAuthor ?? false);
    }
  }, [initialPost]);

  // 3. Orval Generated Hooks
  // We rename 'mutate' to 'followUser'/'unfollowUser' for clarity
  const { mutate: followUser, isPending: isFollowPending } = useFollowUser();
  const { mutate: unfollowUser, isPending: isUnfollowPending } =
    useUnfollowUser();

  const isLoading = isFollowPending || isUnfollowPending;

  if (!initialPost) return null;

  // 4. Handlers
  const handleFollow = () => {
    if (!initialPost.author?.id) return;

    // Optimistic Update: Switch UI to "Following" immediately
    setIsFollowing(true);

    followUser(
      { targetUserId: initialPost.author.id },
      {
        onError: () => {
          // Revert on failure
          setIsFollowing(false);
          toast.error("Failed to follow user");
        },
        onSuccess: () => {
          // Optional: toast.success("Followed!");
        },
      }
    );
  };

  const handleUnfollow = () => {
    if (!initialPost.author?.id) return;

    // Optimistic Update: Switch UI to "Not Following" immediately
    setIsFollowing(false);

    unfollowUser(
      { targetUserId: initialPost.author.id },
      {
        onError: () => {
          // Revert on failure
          setIsFollowing(true);
          toast.error("Failed to unfollow user");
        },
      }
    );
  };

  return (
    <article className="min-h-screen bg-white dark:bg-zinc-950">
      <div className="mx-auto max-w-[680px] px-6 py-10 md:py-14">
        {/* --- 1. HEADER: Title & Description --- */}
        <header className="mb-8">
          <h1 className="mb-4 font-serif text-3xl font-extrabold leading-tight tracking-tight text-gray-900 dark:text-gray-50 md:text-5xl">
            {initialPost.title}
          </h1>
          {initialPost.description && (
            <h2 className="text-xl font-medium text-gray-500 dark:text-gray-400 font-sans">
              {initialPost.description}
            </h2>
          )}
        </header>

        {/* --- 2. AUTHOR META ROW (UPDATED) --- */}
        <div className="mb-8 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Avatar className="h-10 w-10 cursor-pointer">
              <AvatarImage src={initialPost.author?.profileImageUrl} />
              <AvatarFallback>
                {initialPost.author?.username?.[0]}
              </AvatarFallback>
            </Avatar>
            <div className="flex flex-col text-sm">
              <div className="flex items-center gap-2">
                <span className="font-medium text-gray-900 dark:text-gray-100">
                  {initialPost.author?.firstName} {initialPost.author?.lastName}
                </span>

                {/* --- FOLLOW/UNFOLLOW LOGIC START --- */}
                {currentUser?.id !== initialPost.author?.id && (
                  <>
                    <span className="text-gray-400">·</span>
                    {isFollowing ? (
                      <button
                        type="button"
                        onClick={handleUnfollow}
                        disabled={isLoading}
                        className="font-medium text-red-500 hover:text-red-600 hover:underline disabled:opacity-50 transition-colors"
                      >
                        Unfollow
                      </button>
                    ) : (
                      <button
                        type="button"
                        onClick={handleFollow}
                        disabled={isLoading}
                        className="font-medium text-green-600 hover:text-green-700 hover:underline disabled:opacity-50 transition-colors"
                      >
                        Follow
                      </button>
                    )}
                  </>
                )}
                {/* --- FOLLOW/UNFOLLOW LOGIC END --- */}
              </div>
              <div className="flex items-center gap-1 text-gray-500">
                <span>{initialPost.readingTime} min read</span>
                <span>·</span>
                <span>
                  {initialPost.createdAt
                    ? format(new Date(initialPost.createdAt), "MMM d, yyyy")
                    : ""}
                </span>
                {/* Optional Play Icon for "Listen" feature */}
                <span className="ml-2 cursor-pointer hover:text-gray-800">
                  <PlayCircle className="w-4 h-4" />
                </span>
              </div>
            </div>
          </div>

          <div className="flex gap-2">
            <Button variant="ghost" size="icon" className="text-gray-500">
              <Share2 className="w-5 h-5" />
            </Button>
            <Button variant="ghost" size="icon" className="text-gray-500">
              <MoreHorizontal className="w-5 h-5" />
            </Button>
          </div>
        </div>

        {/* --- 3. TOP ACTION BAR (Like Medium) --- */}
        <div className="flex items-center justify-between border-y border-gray-100 py-3 dark:border-gray-800 mb-8">
          <div className="flex items-center gap-6">
            <Button
              variant="ghost"
              className="flex items-center gap-2 px-0 hover:bg-transparent hover:text-black"
            >
              <ThumbsUp className="w-5 h-5 text-gray-500" />
              <span className="text-sm text-gray-500">
                {initialPost.likes || 0}
              </span>
            </Button>
            <Button
              variant="ghost"
              className="flex items-center gap-2 px-0 hover:bg-transparent hover:text-black"
            >
              <MessageCircle className="w-5 h-5 text-gray-500" />
              <span className="text-sm text-gray-500">
                {initialPost.commentsCount || 0}
              </span>
            </Button>
          </div>
          <div>
            <Button
              variant="ghost"
              size="icon"
              className="text-gray-500 hover:text-black"
            >
              <Bookmark className="w-5 h-5" />
            </Button>
          </div>
        </div>

        {/* --- 4. HERO IMAGE --- */}
        {initialPost.imageUrl && (
          <figure className="mb-10">
            <div className="relative aspect-video w-full overflow-hidden rounded-lg">
              <Image
                src={initialPost.imageUrl}
                alt={initialPost.title || "Post cover"}
                fill
                className="object-cover"
                priority
              />
            </div>
            <figcaption className="mt-3 text-center text-sm text-gray-500">
              Image source or caption can go here
            </figcaption>
          </figure>
        )}

        {/* --- 5. MAIN CONTENT (The "Prose" part) --- */}
        <div
          className="
                prose prose-lg prose-slate dark:prose-invert 
                max-w-none 
                font-serif 
                prose-headings:font-sans prose-headings:font-bold
                prose-a:text-green-600 prose-img:rounded-md
            "
        >
          {initialPost.content ? (
            <div dangerouslySetInnerHTML={{ __html: initialPost.content }} />
          ) : (
            <p className="text-gray-500 italic">No content available.</p>
          )}
        </div>

        {/* --- 6. TAGS --- */}
        {initialPost.tags && initialPost.tags.length > 0 && (
          <div className="mt-14 mb-10 flex flex-wrap gap-2">
            {initialPost.tags.map((tag) => (
              <span
                key={tag.id}
                className="rounded-full bg-gray-100 px-4 py-2 text-sm text-gray-600 hover:bg-gray-200 cursor-pointer transition-colors dark:bg-gray-800 dark:text-gray-300"
              >
                {tag.name}
              </span>
            ))}
          </div>
        )}

        {/* --- 7. FOOTER ACTIONS --- */}
        <div className="bg-gray-50 dark:bg-gray-900 -mx-6 px-6 py-10 mt-10 rounded-lg">
          <div className="flex items-center justify-between mb-6">
            <h3 className="font-bold text-lg">
              More from {initialPost.author?.firstName}
            </h3>
            <Button variant="outline" className="rounded-full">
              See all
            </Button>
          </div>
          <div className="text-gray-500 text-sm">
            Short bio about the author could appear here to encourage following.
          </div>
        </div>
      </div>
    </article>
  );
}

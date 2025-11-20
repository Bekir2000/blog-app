// components/PostCard.tsx
import { PostWithBookmarkResponse, UserResponse } from "@/api/generated/model";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Card,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Calendar, MessageCircle, ThumbsUp } from "lucide-react";
import Image from "next/image";
import { PostActions } from "./PostActions";

export function PostCard({
  postWithBookMark,
  currentUser,
}: {
  postWithBookMark: PostWithBookmarkResponse;
  currentUser: UserResponse | null;
}) {
  // 1. Safely access the post object
  const post = postWithBookMark?.post;

  // 2. GUARD CLAUSE: If post is null or undefined, don't render anything
  if (!post) {
    return null;
    // Alternatively, return a placeholder:
    // return <div className="p-4">Post unavailable</div>;
  }

  const isBookmarked = postWithBookMark.isBookmarked;

  return (
    <Card className="max-w-3xl min-w-10 shadow-md">
      <CardHeader>
        <div className="text-sm flex flex-row items-center gap-2 mb-2">
          <Avatar>
            {/* Added optional chaining (?.) just in case author is missing */}
            <AvatarImage
              src={post.author?.profileImageUrl ?? undefined}
              alt={post.author?.firstName}
            />
            <AvatarFallback>{post.author?.username?.[0] ?? "?"}</AvatarFallback>
          </Avatar>
          <div>
            {post.author?.firstName} {post.author?.lastName}
          </div>
        </div>
        <div className="flex flex-row items-start justify-between gap-4">
          <div className="flex-1">
            <CardTitle className="text-xl font-bold leading-snug">
              {post.title}
            </CardTitle>
            <CardDescription className="mt-2">
              {post.description}
            </CardDescription>
          </div>

          <div className="w-[160px] h-[120px] shrink-0">
            {post.imageUrl && (
              <Image
                src={post.imageUrl}
                alt={post.title ?? ""}
                width={160}
                height={120}
                className="h-full w-full rounded-md object-cover"
              />
            )}
          </div>
        </div>
      </CardHeader>

      <CardFooter className="flex justify-between items-center text-sm text-gray-600">
        <div className="flex items-center gap-6">
          <span className="flex items-center gap-1 ">
            {/* Added safety check for createdAt */}
            <Calendar className="w-4 h-4" />{" "}
            {post.createdAt ? post.createdAt.split("T")[0] : ""}
          </span>
          <span className="flex items-center gap-1">
            <ThumbsUp className="w-4 h-4" /> {post.likes || 0}
          </span>
          <span className="flex items-center gap-1">
            <MessageCircle className="w-4 h-4" /> {post.commentsCount || 0}
          </span>
        </div>

        {post.id ? (
          <PostActions
            postId={post.id}
            currentUser={currentUser}
            isBookmarked={isBookmarked}
          />
        ) : null}
      </CardFooter>
    </Card>
  );
}

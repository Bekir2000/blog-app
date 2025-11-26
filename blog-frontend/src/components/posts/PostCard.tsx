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
import Link from "next/link"; // <--- 1. Import Link
import { PostActions } from "./PostActions";

export function PostCard({
  postWithBookMark,
  currentUser,
}: {
  postWithBookMark: PostWithBookmarkResponse;
  currentUser: UserResponse | null;
}) {
  const post = postWithBookMark?.post;

  if (!post) {
    return null;
  }

  const isBookmarked = postWithBookMark.isBookmarked;
  const postUrl = `/posts/${post.id}`; // <--- 2. Define the URL

  return (
    <Card className="max-w-3xl min-w-10 shadow-md transition-shadow hover:shadow-lg">
      <CardHeader>
        {/* User Info (Not part of the main click to keep profile accessible separately) */}
        <div className="text-sm flex flex-row items-center gap-2 mb-3">
          <Avatar className="h-6 w-6">
            <AvatarImage
              src={post.author?.profileImageUrl ?? undefined}
              alt={post.author?.firstName}
            />
            <AvatarFallback className="text-[10px]">
              {post.author?.username?.[0] ?? "?"}
            </AvatarFallback>
          </Avatar>
          <div className="text-xs font-medium text-gray-700">
            {post.author?.firstName} {post.author?.lastName}
          </div>
        </div>

        {/* 3. Wrap the Main Content in a Link */}
        <Link
          href={postUrl}
          className="flex flex-row items-start justify-between gap-4 group cursor-pointer"
        >
          <div className="flex-1">
            <CardTitle className="text-xl font-bold leading-snug group-hover:text-gray-700 transition-colors">
              {post.title}
            </CardTitle>
            <CardDescription className="mt-2 line-clamp-2">
              {" "}
              {/* line-clamp limits text to 2 lines */}
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
        </Link>
      </CardHeader>

      <CardFooter className="flex justify-between items-center text-sm text-gray-600 mt-2">
        {/* Footer info links to post as well, or stays static */}
        <div className="flex items-center gap-6">
          <span className="flex items-center gap-1 text-xs">
            <Calendar className="w-3.5 h-3.5" />{" "}
            {post.createdAt ? post.createdAt.split("T")[0] : ""}
          </span>
          <span className="flex items-center gap-1 text-xs">
            <ThumbsUp className="w-3.5 h-3.5" /> {post.likes || 0}
          </span>
          <span className="flex items-center gap-1 text-xs">
            <MessageCircle className="w-3.5 h-3.5" /> {post.commentsCount || 0}
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

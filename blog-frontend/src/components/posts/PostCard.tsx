import { PostCardResponse, UserResponse } from "@/api/generated/model";
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
import Link from "next/link";
import { PostActions } from "./PostActions";

export function PostCard({
  postCard,
  currentUser,
}: {
  postCard: PostCardResponse;
  currentUser: UserResponse | null;
}) {
  if (!postCard) {
    return null;
  }

  const isBookmarked = postCard.isBookmarked;
  const postUrl = `/posts/${postCard.id}`;

  return (
    <Card className="w-full shadow-md transition-shadow hover:shadow-lg">
      <CardHeader>
        {/* User Info */}
        <div className="mb-3 flex flex-row items-center gap-2 text-sm">
          <Avatar className="h-6 w-6">
            <AvatarImage
              src={postCard.author?.profileImageUrl ?? undefined}
              alt={postCard.author?.firstName}
            />
            <AvatarFallback className="text-[10px]">
              {postCard.author?.username?.[0] ?? "?"}
            </AvatarFallback>
          </Avatar>
          <div className="text-xs font-medium text-gray-700">
            {postCard.author?.firstName} {postCard.author?.lastName}
          </div>
        </div>

        {/* Main Content Link */}
        <Link
          href={postUrl}
          className="group flex flex-row items-start justify-between gap-4 cursor-pointer"
        >
          <div className="flex-1">
            <CardTitle className="text-base font-bold leading-snug transition-colors group-hover:text-gray-700 sm:text-xl">
              {postCard.title}
            </CardTitle>
            <CardDescription className="mt-2 line-clamp-2 text-sm sm:text-base">
              {postCard.description}
            </CardDescription>
          </div>

          {/* Updated: Responsive Image Size (Smaller on mobile) */}
          <div className="h-[75px] w-[100px] shrink-0 sm:h-[120px] sm:w-[160px]">
            {postCard.imageUrl && (
              <Image
                src={postCard.imageUrl}
                alt={postCard.title ?? ""}
                width={160}
                height={120}
                className="h-full w-full rounded-md object-cover"
              />
            )}
          </div>
        </Link>
      </CardHeader>

      <CardFooter className="mt-2 flex items-center justify-between text-sm text-gray-600">
        <div className="flex items-center gap-4 sm:gap-6">
          <span className="flex items-center gap-1 text-xs">
            <Calendar className="h-3.5 w-3.5" />{" "}
            {postCard.createdAt ? postCard.createdAt.split("T")[0] : ""}
          </span>
          <span className="flex items-center gap-1 text-xs">
            <ThumbsUp className="h-3.5 w-3.5" /> {postCard.likes || 0}
          </span>
          <span className="flex items-center gap-1 text-xs">
            <MessageCircle className="h-3.5 w-3.5" />{" "}
            {postCard.commentsCount || 0}
          </span>
        </div>

        {postCard.id ? (
          <PostActions
            postId={postCard.id}
            currentUser={currentUser}
            isBookmarked={isBookmarked}
          />
        ) : null}
      </CardFooter>
    </Card>
  );
}

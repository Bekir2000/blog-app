// components/PostCard.tsx
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Card,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Post } from "@/data/posts";
import {
  BookmarkPlus,
  Calendar,
  CircleMinus,
  Ellipsis,
  MessageCircle,
  ThumbsUp,
} from "lucide-react";
import Image from "next/image";

export function PostCard({ post }: { post: Post }) {
  return (
    <Card className="max-w-3xl min-w-10 shadow-md">
      {/* Header: title, description, author, image */}
      <CardHeader>
        <div className="text-sm flex flex-row items-center gap-2 mb-2">
          <Avatar>
            <AvatarImage src={post.author.avatarUrl} alt={post.author.name} />
            <AvatarFallback>{post.author.name[0]}</AvatarFallback>
          </Avatar>
          <div>{post.author.name}</div>
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

          {/* Fixed image size */}
          <div className="w-[160px] h-[120px] shrink-0">
            <Image
              src={post.imageUrl}
              alt={post.title}
              width={160}
              height={120}
              className="h-full w-full rounded-md object-cover"
            />
          </div>
        </div>
      </CardHeader>

      {/* Footer: meta info + actions */}
      <CardFooter className="flex justify-between items-center text-sm text-gray-600">
        <div className="flex items-center gap-6">
          <span className="flex items-center gap-1 ">
            <Calendar className="w-4 h-4" /> {post.date}
          </span>
          <span className="flex items-center gap-1">
            <ThumbsUp className="w-4 h-4" /> {post.reads}
          </span>
          <span className="flex items-center gap-1">
            <MessageCircle className="w-4 h-4" /> {post.comments}
          </span>
        </div>

        <div className="flex gap-4 mr-50">
          <CircleMinus className="cursor-pointer" />
          <BookmarkPlus className="cursor-pointer" />
          <Ellipsis className="cursor-pointer" />
        </div>
      </CardFooter>
    </Card>
  );
}

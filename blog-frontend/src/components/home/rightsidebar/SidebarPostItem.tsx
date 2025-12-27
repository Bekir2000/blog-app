"use client";

import { PostCardResponse } from "@/api/generated/model";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { format } from "date-fns";
import Link from "next/link";

export function SidebarPostItem({ post }: { post: PostCardResponse }) {
  return (
    <div className="flex flex-col gap-1 mb-4 last:mb-0">
      <div className="flex items-center gap-2 mb-1">
        <Avatar className="h-5 w-5">
          <AvatarImage src={post.author?.imageUrl} />
          <AvatarFallback className="text-[10px]">
            {post.author?.firstName?.[0]}
          </AvatarFallback>
        </Avatar>
        <span className="text-xs font-medium text-gray-700 truncate">
          {post.author?.firstName} {post.author?.lastName}
        </span>
      </div>

      <Link href={`/posts/${post.id}`} className="group">
        <h4 className="text-sm font-bold text-gray-900 group-hover:underline line-clamp-2 leading-snug">
          {post.title}
        </h4>
      </Link>

      <div className="text-xs text-gray-500 mt-1">
        {post.createdAt && format(new Date(post.createdAt), "MMM d")}
        {" · "}
        {post.meta?.readingTimeMinutes ?? 1} min read
      </div>
    </div>
  );
}

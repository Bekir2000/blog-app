import { PostDetailResponse } from "@/api/generated/model";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { format } from "date-fns";
import { MoreHorizontal, PlayCircle, Share2 } from "lucide-react";

interface PostAuthorMetaProps {
  author: PostDetailResponse["author"];
  createdAt?: string;
  readingTime?: number;
  isFollowing: boolean;
  isLoading: boolean;
  isOwnPost: boolean;
  onToggleFollow: () => void;
}

export function PostAuthorMeta({
  author,
  createdAt,
  readingTime,
  isFollowing,
  isLoading,
  isOwnPost,
  onToggleFollow,
}: PostAuthorMetaProps) {
  return (
    <div className="mb-8 flex items-center justify-between">
      <div className="flex items-center gap-3">
        <Avatar className="h-10 w-10 cursor-pointer">
          <AvatarImage src={author?.profileImageUrl} />
          <AvatarFallback>{author?.username?.[0]}</AvatarFallback>
        </Avatar>
        <div className="flex flex-col text-sm">
          <div className="flex items-center gap-2">
            <span className="font-medium text-gray-900 dark:text-gray-100">
              {author?.firstName} {author?.lastName}
            </span>

            {/* Follow Button Logic */}
            {!isOwnPost && (
              <>
                <span className="text-gray-400">·</span>
                <button
                  type="button"
                  onClick={onToggleFollow}
                  disabled={isLoading}
                  className={`font-medium hover:underline disabled:opacity-50 transition-colors ${
                    isFollowing
                      ? "text-red-500 hover:text-red-600"
                      : "text-green-600 hover:text-green-700"
                  }`}
                >
                  {isFollowing ? "Unfollow" : "Follow"}
                </button>
              </>
            )}
          </div>

          <div className="flex items-center gap-1 text-gray-500">
            <span>{readingTime} min read</span>
            <span>·</span>
            <span>
              {createdAt ? format(new Date(createdAt), "MMM d, yyyy") : ""}
            </span>
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
  );
}

import { Button } from "@/components/ui/button";
import { Bookmark, MessageCircle, ThumbsUp } from "lucide-react";

interface PostActionBarProps {
  likes: number;
  comments: number;
  isBookmarked: boolean;
  isLoading: boolean;
  onToggleBookmark: () => void;
}

export function PostActionBar({
  likes,
  comments,
  isBookmarked,
  isLoading,
  onToggleBookmark,
}: PostActionBarProps) {
  return (
    <div className="flex items-center justify-between border-y border-gray-100 py-3 dark:border-gray-800 mb-8">
      <div className="flex items-center gap-6">
        <Button
          variant="ghost"
          className="flex items-center gap-2 px-0 hover:bg-transparent hover:text-black dark:hover:text-white"
        >
          <ThumbsUp className="w-5 h-5 text-gray-500" />
          <span className="text-sm text-gray-500">{likes}</span>
        </Button>
        <Button
          variant="ghost"
          className="flex items-center gap-2 px-0 hover:bg-transparent hover:text-black dark:hover:text-white"
        >
          <MessageCircle className="w-5 h-5 text-gray-500" />
          <span className="text-sm text-gray-500">{comments}</span>
        </Button>
      </div>

      <div>
        <Button
          variant="ghost"
          size="icon"
          onClick={onToggleBookmark}
          disabled={isLoading}
          className="text-gray-500 hover:text-black dark:hover:text-white disabled:opacity-50"
        >
          {/* Fill the icon if bookmarked */}
          <Bookmark
            className={`w-5 h-5 transition-colors ${
              isBookmarked
                ? "fill-black text-black dark:fill-white dark:text-white"
                : ""
            }`}
          />
        </Button>
      </div>
    </div>
  );
}

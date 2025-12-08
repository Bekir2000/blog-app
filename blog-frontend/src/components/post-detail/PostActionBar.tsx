import { Button } from "@/components/ui/button";
import { Bookmark, MessageCircle, ThumbsUp } from "lucide-react";

interface PostActionBarProps {
  likes: number;
  comments: number;
  isBookmarked: boolean;
  isLiked: boolean; // <--- New Prop
  isLoading: boolean;
  onToggleBookmark: () => void;
  onToggleLike: () => void; // <--- New Prop
}

export function PostActionBar({
  likes,
  comments,
  isBookmarked,
  isLiked,
  isLoading,
  onToggleBookmark,
  onToggleLike,
}: PostActionBarProps) {
  return (
    <div className="flex items-center justify-between border-y border-gray-100 py-3 dark:border-gray-800 mb-8">
      <div className="flex items-center gap-6">
        <Button
          variant="ghost"
          onClick={onToggleLike}
          disabled={isLoading} // Optional: Disable while request flying if desired
          className={`flex items-center gap-2 px-0 hover:bg-transparent transition-colors ${
            isLiked
              ? "text-blue-600 dark:text-blue-500 hover:text-blue-700"
              : "text-gray-500 hover:text-black dark:hover:text-white"
          }`}
        >
          {/* Fill icon if liked */}
          <ThumbsUp className={`w-5 h-5 ${isLiked ? "fill-current" : ""}`} />
          <span className="text-sm">{likes}</span>
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

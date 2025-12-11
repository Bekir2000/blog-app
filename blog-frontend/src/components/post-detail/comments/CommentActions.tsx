import { Button } from "@/components/ui/button";
import { MessageSquare, ThumbsUp } from "lucide-react";

interface CommentActionsProps {
  likesCount: number;
  isLiked: boolean;
  onLike: () => void;
  onReplyClick: () => void;
}

export function CommentActions({
  likesCount,
  isLiked,
  onLike,
  onReplyClick,
}: CommentActionsProps) {
  return (
    <div className="flex items-center gap-4 pt-1">
      <Button
        variant="ghost"
        size="sm"
        onClick={onLike}
        className={`h-6 px-1.5 text-xs hover:bg-transparent ${
          isLiked ? "text-blue-600" : "text-muted-foreground"
        }`}
      >
        <ThumbsUp
          className={`mr-1.5 h-3 w-3 ${isLiked ? "fill-current" : ""}`}
        />
        {likesCount > 0 ? likesCount : "Like"}
      </Button>

      <Button
        variant="ghost"
        size="sm"
        onClick={onReplyClick}
        className="h-6 px-1.5 text-xs text-muted-foreground hover:bg-transparent hover:text-foreground"
      >
        <MessageSquare className="mr-1.5 h-3 w-3" />
        Reply
      </Button>
    </div>
  );
}

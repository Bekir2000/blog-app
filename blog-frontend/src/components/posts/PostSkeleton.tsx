import { Card, CardFooter, CardHeader } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

export function PostSkeleton() {
  return (
    <Card className="max-w-3xl min-w-10 shadow-md">
      <CardHeader>
        {/* Header: Avatar + Name */}
        <div className="flex flex-row items-center gap-2 mb-2">
          <Skeleton className="h-10 w-10 rounded-full" />
          <div className="space-y-1">
            <Skeleton className="h-4 w-32" />
          </div>
        </div>

        {/* Body: Title/Desc + Image */}
        <div className="flex flex-row items-start justify-between gap-4">
          <div className="flex-1 space-y-3">
            <Skeleton className="h-6 w-3/4" /> {/* Title */}
            <Skeleton className="h-4 w-full" /> {/* Desc Line 1 */}
            <Skeleton className="h-4 w-5/6" /> {/* Desc Line 2 */}
          </div>

          {/* Image Placeholder */}
          <Skeleton className="w-[160px] h-[120px] rounded-md shrink-0" />
        </div>
      </CardHeader>

      <CardFooter className="flex justify-between items-center mt-4">
        {/* Footer Icons */}
        <div className="flex items-center gap-6">
          <Skeleton className="h-4 w-20" /> {/* Date */}
          <Skeleton className="h-4 w-12" /> {/* Likes */}
          <Skeleton className="h-4 w-12" /> {/* Comments */}
        </div>

        {/* Bookmark Icon */}
        <Skeleton className="h-5 w-5 rounded-sm" />
      </CardFooter>
    </Card>
  );
}

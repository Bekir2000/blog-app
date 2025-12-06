import { PostDetailResponse } from "@/api/generated/model";
import Image from "next/image";

export function PostContent({ post }: { post: PostDetailResponse }) {
  return (
    <>
      {/* Hero Image */}
      {post.imageUrl && (
        <figure className="mb-10">
          <div className="relative aspect-video w-full overflow-hidden rounded-lg">
            <Image
              src={post.imageUrl}
              alt={post.title || "Post cover"}
              fill
              className="object-cover"
              priority
            />
          </div>
        </figure>
      )}

      {/* Main Prose */}
      <div className="prose prose-lg prose-slate dark:prose-invert max-w-none font-serif prose-headings:font-sans prose-headings:font-bold prose-a:text-green-600 prose-img:rounded-md">
        {post.content ? (
          <div dangerouslySetInnerHTML={{ __html: post.content }} />
        ) : (
          <p className="text-gray-500 italic">No content available.</p>
        )}
      </div>
    </>
  );
}

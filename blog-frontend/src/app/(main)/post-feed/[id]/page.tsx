import { getPostById } from "@/api/generated/server/post-controller/post-controller";
import { PostDetail } from "@/components/post-detail/PostDetail";
import { getUser } from "@/lib/auth";
import { Metadata } from "next";

interface PageProps {
  params: { id: string };
}

// 1. Add this function to generate the Preview Card
export async function generateMetadata({
  params,
}: PageProps): Promise<Metadata> {
  // Await params (required in Next.js 15+)
  const postId = (await params).id;

  try {
    const post = await getPostById(postId);

    if (!post) {
      return {
        title: "Post Not Found",
      };
    }

    // Construct the absolute URL for the image
    // LinkedIn requires absolute URLs (e.g., https://myapp.com/image.png)
    const imageUrl = post.imageUrl || "/default-og-image.png";

    return {
      title: post.title,
      description: post.description || "Read this article on our platform.",
      openGraph: {
        title: post.title,
        description: post.description || "Read this article on our platform.",
        // The URL of the specific post
        // url: `https://your-domain.com/posts/${postId}`,
        images: [
          {
            url: imageUrl,
            width: 1200,
            height: 630,
            alt: post.title,
          },
        ],
        type: "article",
        // article: {
        //   publishedTime: post.createdAt,
        //   authors: [post.author?.username || ""],
        // }
      },
      twitter: {
        card: "summary_large_image",
        title: post.title,
        description: post.description || "",
        images: [imageUrl],
      },
    };
  } catch (error) {
    return {
      title: "Error loading post",
    };
  }
}

// 2. Your existing Page component
export default async function Page({ params }: PageProps) {
  const postId = (await params).id;

  // Next.js automatically dedupes this request if it uses fetch()
  // So calling getPostById twice (once in metadata, once here) is fine.
  const post = await getPostById(postId);
  const user = await getUser();

  return <PostDetail post={post} currentUser={user} />;
}

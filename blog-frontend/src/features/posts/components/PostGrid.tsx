import { PostCard } from "./PostCard";

export function PostsGrid({ posts }: { posts: any[] }) {
  return (
    <div className="w-4xl gap-6 flex flex-col items-center ">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
}

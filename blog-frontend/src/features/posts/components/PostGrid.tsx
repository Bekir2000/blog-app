import { PostCard } from "./PostCard";

export function PostsGrid({ posts }: { posts: any[] }) {
  return (
    <div className=" gap-6 flex flex-col">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
}

import ApiClient from "@/lib/api-client";
import { Post } from "./type";

const client = new ApiClient();

export const PostApi = {
  getPosts: () => client.get<Post[]>("/posts"),
  createPost: (post: Partial<Post>) => client.post<Post>("/posts", post),
};

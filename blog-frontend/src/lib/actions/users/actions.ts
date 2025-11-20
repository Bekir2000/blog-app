"use server";

import {
  bookmarkPost,
  unbookmarkPost,
} from "@/api/generated/me-controller/me-controller";
import { getUser } from "@/lib/auth";
import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
// Import your PostApi logic or database SDK

export async function createBookmarkAction(postId: string) {
  // 1. Get the cookies in the server context
  const user = await getUser();

  if (!user) {
    redirect("/");
  }

  await bookmarkPost({ postId });

  // 4. (Optional) Revalidate any relevant data paths
  revalidatePath("/posts");
  revalidatePath("/bookmarks");
}

export async function deleteBookmarkAction(postId: string) {
  // 1. Get the cookies in the server context
  const user = await getUser();

  if (!user) {
    redirect("/");
  }

  await unbookmarkPost(postId);

  // 4. (Optional) Revalidate any relevant data paths
  revalidatePath("/posts");
  revalidatePath("/bookmarks");
}

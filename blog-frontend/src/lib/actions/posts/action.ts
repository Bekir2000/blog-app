"use server";

// 1. Import the generated function
import { getAllPosts } from "@/api/generated/post-controller/post-controller";

export async function fetchPostsPage(page: number) {
  const SIZE = 5;

  // --- 🕒 ARTIFICIAL DELAY (2 Seconds) ---
  // This pauses execution here for 2000ms before fetching data
  await new Promise((resolve) => setTimeout(resolve, 2000));

  try {
    // 2. Call the Orval function
    const data = await getAllPosts(
      // Query Params (Mapped to ?page=1&size=5)
      {
        page: page,
        size: SIZE,
      }
    );

    return data;
  } catch (error) {
    console.error("Error fetching page:", page, error);
    return [];
  }
}

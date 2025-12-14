// src/app/actions/unsplash.ts
"use server";

export async function searchUnsplash(query: string) {
  if (!query) return [];

  const ACCESS_KEY = process.env.UNSPLASH_ACCESS_KEY;

  if (!ACCESS_KEY) {
    throw new Error("Unsplash API key is missing");
  }

  try {
    const res = await fetch(
      `https://api.unsplash.com/search/photos?page=1&query=${encodeURIComponent(
        query
      )}&client_id=${ACCESS_KEY}&per_page=9&orientation=landscape`
    );

    if (!res.ok) {
      throw new Error("Unsplash API error");
    }

    const data = await res.json();
    return data.results || [];
  } catch (error) {
    console.error("Unsplash Error:", error);
    return []; // Return empty array on error to prevent crashing UI
  }
}

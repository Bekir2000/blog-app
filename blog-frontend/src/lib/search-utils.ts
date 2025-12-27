import { GetAllPostCardsCategory } from "@/api/generated/model"; // Adjust path to where your Orval types are

// We need a runtime list to validate the URL parameter because
// GetAllPostCardsCategory is just a Type, not a runtime object.
const VALID_CATEGORIES: GetAllPostCardsCategory[] = [
  "TECHNOLOGY",
  "LIFESTYLE",
  "TUTORIAL",
  "NEWS",
  "PERSONAL",
  "CODING",
];

function parseCategory(
  value: string | undefined
): GetAllPostCardsCategory | undefined {
  if (!value) return undefined;

  const upperValue = value.toUpperCase();

  // Type predicate/guard to ensure safety
  if (VALID_CATEGORIES.includes(upperValue as GetAllPostCardsCategory)) {
    return upperValue as GetAllPostCardsCategory;
  }
  return undefined;
}

type PostFilters = {
  title?: string;
  authorName?: string;
  tag?: string;
  category?: GetAllPostCardsCategory;
};

export function getPostFiltersFromParams(searchParams: {
  [key: string]: string | string[] | undefined;
}): PostFilters {
  // Helper to extract a single string from potential arrays
  const getString = (key: string): string | undefined => {
    const value = searchParams[key];
    return typeof value === "string" ? value : undefined;
  };

  // Helper to parse numbers (for page/size if you ever add them to URL)
  const getNumber = (key: string): number | undefined => {
    const val = getString(key);
    return val ? parseInt(val, 10) : undefined;
  };

  return {
    title: getString("title"),
    authorName: getString("authorName"),
    tag: getString("tag"),
    category: parseCategory(getString("category")),
  };
}

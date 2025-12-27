import { PostDetailResponseCategory } from "@/api/generated/model";
import { BackButton } from "@/components/ui/back-button";

interface PostHeaderProps {
  title?: string;
  description?: string;
  category?: PostDetailResponseCategory | string;
}

export function PostHeader({ title, description, category }: PostHeaderProps) {
  const formattedCategory = category
    ? category.toString().charAt(0) + category.toString().slice(1).toLowerCase()
    : "";

  return (
    <header className="mb-8">
      {/* Category Badge */}
      {category && (
        <div className="mb-4">
          <span className="text-sm font-bold uppercase tracking-wider text-blue-600 dark:text-blue-400">
            {formattedCategory}
          </span>
        </div>
      )}

      {/* FIX: 
         1. Moved 'mb-4' from <h1> to this parent <div>.
         2. Added 'gap-4' to create space between the arrow and title.
         3. 'items-center' will now perfectly align the arrow with the text height.
      */}
      <div className="mb-4 flex items-center gap-4">
        <BackButton />
        <h1 className="font-serif text-3xl font-extrabold leading-tight tracking-tight text-gray-900 dark:text-gray-50 md:text-5xl">
          {title}
        </h1>
      </div>

      {/* {description && (
        <h2 className="text-xl font-medium text-gray-500 dark:text-gray-400 font-sans">
          {description}
        </h2>
      )} */}
    </header>
  );
}

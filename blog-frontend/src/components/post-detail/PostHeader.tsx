export function PostHeader({
  title,
  description,
}: {
  title?: string;
  description?: string;
}) {
  return (
    <header className="mb-8">
      <h1 className="mb-4 font-serif text-3xl font-extrabold leading-tight tracking-tight text-gray-900 dark:text-gray-50 md:text-5xl">
        {title}
      </h1>
      {description && (
        <h2 className="text-xl font-medium text-gray-500 dark:text-gray-400 font-sans">
          {description}
        </h2>
      )}
    </header>
  );
}

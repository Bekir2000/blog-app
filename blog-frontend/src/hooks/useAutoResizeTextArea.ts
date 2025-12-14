import { useEffect, useRef } from "react";

export function useAutoResizeTextArea(value: string | undefined) {
  const ref = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    const element = ref.current;
    if (!element) return;

    // Reset height to allow shrinking
    element.style.height = "auto";
    // Set height to scrollHeight
    element.style.height = `${element.scrollHeight}px`;
  }, [value]);

  return ref;
}

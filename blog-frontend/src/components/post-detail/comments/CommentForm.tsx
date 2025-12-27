"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import * as z from "zod";

import { UserResponse } from "@/api/generated/model";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { AlertCircle, Loader2, SendHorizonal } from "lucide-react";
// 👇 CHANGE THIS IMPORT
import { useRouter } from "next/navigation";

// 1. Zod Schema
const commentSchema = z.object({
  content: z
    .string()
    .min(10, "Comment must be at least 10 characters.")
    .max(2000, "Comment cannot exceed 2000 characters."),
});

type CommentFormValues = z.infer<typeof commentSchema>;

interface CommentFormProps {
  currentUser?: UserResponse | null;
  onSubmit: (content: string) => Promise<any>;
  isSubmitting: boolean;
}

export function CommentForm({
  currentUser,
  onSubmit,
  isSubmitting,
}: CommentFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    watch,
    setError,
    formState: { errors, isValid },
  } = useForm<CommentFormValues>({
    resolver: zodResolver(commentSchema),
    defaultValues: { content: "" },
    mode: "onChange",
  });

  const router = useRouter();

  const fullName = currentUser
    ? `${currentUser.firstName} ${currentUser.lastName}`.trim()
    : "";
  const initials = currentUser
    ? (currentUser.firstName?.[0] || "") + (currentUser.lastName?.[0] || "")
    : "";

  const currentContent = watch("content") || "";

  const onFormSubmit = async (data: CommentFormValues) => {
    try {
      await onSubmit(data.content);
      reset();
    } catch (error: any) {
      console.log("🔥 Comment Submission Error:", error);

      const backendError = error.response?.data;
      const validationErrors =
        backendError?.errors || backendError?.fieldErrors;

      if (Array.isArray(validationErrors)) {
        const contentError = validationErrors.find(
          (err: any) => err.field === "content"
        );

        if (contentError) {
          setError("content", {
            type: "server",
            message: contentError.message,
          });
          return;
        }
      }

      setError("content", {
        type: "server",
        message:
          backendError?.detail ||
          backendError?.message ||
          "Something went wrong. Please try again.",
      });
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && (e.metaKey || e.ctrlKey)) {
      handleSubmit(onFormSubmit)();
    }
  };

  if (!currentUser) {
    return (
      <div className="flex flex-col items-center justify-center gap-2 rounded-xl bg-muted/50 p-8 text-center border border-dashed border-border">
        <p className="text-sm text-muted-foreground">
          Log in to join the conversation
        </p>
        <Button
          // You can also pass a return URL here if you want:
          // onClick={() => router.push(`/login?next=${window.location.pathname}`)}
          onClick={() => router.push("/login")}
          variant="outline"
          size="sm"
        >
          Log In
        </Button>
      </div>
    );
  }

  return (
    <div className="flex gap-4">
      <Avatar className="h-9 w-9 border border-border/50">
        <AvatarImage src={currentUser.profileImageUrl || ""} alt={fullName} />
        <AvatarFallback className="text-xs font-medium uppercase">
          {initials || "ME"}
        </AvatarFallback>
      </Avatar>

      <form onSubmit={handleSubmit(onFormSubmit)} className="flex-1 space-y-2">
        <Textarea
          {...register("content")}
          placeholder="What are your thoughts?"
          disabled={isSubmitting}
          onKeyDown={handleKeyDown}
          className={`min-h-[100px] resize-none bg-background focus-visible:ring-1 text-sm ${
            errors.content
              ? "border-destructive focus-visible:ring-destructive"
              : ""
          }`}
        />

        <div className="flex items-center justify-between">
          <div className="text-xs min-h-[20px]">
            {errors.content ? (
              <span className="flex items-center text-destructive font-medium animate-in fade-in slide-in-from-left-1">
                <AlertCircle className="mr-1 h-3 w-3" />
                {errors.content.message}
              </span>
            ) : (
              <span
                className={`transition-colors ${
                  currentContent.length > 0 && currentContent.length < 10
                    ? "text-orange-500"
                    : "text-muted-foreground"
                }`}
              >
                {currentContent.length}/2000
              </span>
            )}
          </div>

          <Button
            type="submit"
            disabled={!isValid || isSubmitting}
            size="sm"
            variant={errors.content ? "destructive" : "default"}
          >
            {isSubmitting ? (
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            ) : (
              <SendHorizonal className="mr-2 h-4 w-4" />
            )}
            Post Comment
          </Button>
        </div>
      </form>
    </div>
  );
}

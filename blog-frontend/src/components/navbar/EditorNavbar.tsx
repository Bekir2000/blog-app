"use client";

import { UserResponse } from "@/api/generated/model";
import { BackButton } from "@/components/ui/back-button";
import { BrandLogo } from "@/components/ui/brand-logo";
import { Button } from "@/components/ui/button";
import { Loader2 } from "lucide-react";
import { BaseNavbar } from "../navbar/BaseNavbar";

interface EditorNavbarProps {
  currentUser: UserResponse | null;
  isPending: boolean;
  isEditing: boolean;
  onSaveDraft: () => void;
  onPublish: () => void;
}

export function EditorNavbar({
  currentUser,
  isPending,
  isEditing,
  onSaveDraft,
  onPublish,
}: EditorNavbarProps) {
  return (
    <BaseNavbar
      user={currentUser} // ✅ Pass user here
      left={
        <div className="flex items-center gap-4">
          <BackButton />
          <div className="flex items-center gap-2">
            <BrandLogo />
            <span className="hidden sm:block h-6 w-px bg-gray-200 dark:bg-gray-700 mx-2"></span>
            <span className="text-sm text-gray-500 font-medium">
              {isEditing ? "Editing Draft" : "New Story"}
            </span>
          </div>
        </div>
      }
      center={null}
      // ✅ Only pass the specific buttons for the editor
      actions={
        <>
          <Button
            variant="ghost"
            size="sm"
            disabled={isPending}
            onClick={onSaveDraft}
            className="text-gray-500 hover:text-black hidden sm:flex"
          >
            Save Draft
          </Button>

          <Button
            size="sm"
            disabled={isPending}
            onClick={onPublish}
            className="rounded-full bg-green-600 hover:bg-green-700 text-white px-6 font-medium mr-2"
          >
            {isPending && <Loader2 className="w-3 h-3 mr-2 animate-spin" />}
            Publish
          </Button>
        </>
      }
    />
  );
}

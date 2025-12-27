"use client";

import {
  AuthorSummary,
  PostCardResponse,
  UserResponse,
} from "@/api/generated/model";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Plus } from "lucide-react";
import Link from "next/link";
import { Suspense } from "react";
import { CategoryList } from "./CategoryList";
import { SidebarPostItem } from "./SidebarPostItem";
import { UserSuggestionItem } from "./UserSuggestionItem";

interface RightSidebarProps {
  suggestions: AuthorSummary[];
  currentUser?: UserResponse | null;
  bookmarks: PostCardResponse[];
}

export default function RightSidebar({
  suggestions,
  currentUser,
  bookmarks,
}: RightSidebarProps) {
  return (
    <aside className="hidden lg:block w-[350px] sticky top-24 h-fit space-y-6 pb-10 mr-6">
      {/* Topics - Visible to Everyone */}
      <Card className="border-none shadow-none bg-gray-50/50">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-bold text-gray-900">
            Recommended topics
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Suspense fallback={<div className="h-10 bg-gray-100" />}>
            <CategoryList />
          </Suspense>
        </CardContent>
      </Card>

      {/* Who to follow - HIDDEN if not logged in */}
      {currentUser && (
        <Card className="border-none shadow-none">
          <CardHeader className="pb-3">
            <CardTitle className="text-base font-bold text-gray-900">
              Who to follow
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-5">
            {suggestions.length > 0 ? (
              suggestions.map((author) => (
                <UserSuggestionItem
                  key={author.id}
                  author={author}
                  currentUser={currentUser}
                />
              ))
            ) : (
              <p className="text-sm text-muted-foreground">
                No suggestions available.
              </p>
            )}
          </CardContent>
        </Card>
      )}

      {/* Reading List - HIDDEN if not logged in (since guests don't have bookmarks) */}
      {currentUser && (
        <Card className="border-none shadow-none">
          <CardHeader className="pb-3">
            <CardTitle className="text-base font-bold text-gray-900">
              Reading list
            </CardTitle>
          </CardHeader>
          <CardContent>
            {bookmarks.length > 0 ? (
              <div className="flex flex-col gap-4">
                {bookmarks.map((post) => (
                  <SidebarPostItem key={post.id} post={post} />
                ))}
                <Link
                  href="/bookmarks"
                  className="text-xs text-green-600 hover:text-green-700 mt-2 font-medium"
                >
                  See all bookmarks
                </Link>
              </div>
            ) : (
              <p className="text-sm text-gray-500">
                Click the <Plus className="w-3 h-3 inline" /> on any story to
                add it here.
              </p>
            )}
          </CardContent>
        </Card>
      )}
    </aside>
  );
}

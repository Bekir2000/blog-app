"use client";

import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { DraftsFeed, PublishedFeed } from "@/components/stories/StoryFeeds";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ChevronLeft } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";

interface UserStoriesTabsProps {
  currentUser: UserResponse | null;
  initialDrafts: PostCardResponse[];
  initialPublished: PostCardResponse[];
}

export function UserStoriesTabs({
  currentUser,
  initialDrafts,
  initialPublished,
}: UserStoriesTabsProps) {
  const router = useRouter();

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => router.back()}
            className="-ml-3 text-gray-500 hover:text-black"
          >
            <ChevronLeft className="w-6 h-6" />
          </Button>
          <h1 className="text-3xl font-serif font-bold">Your Stories</h1>
        </div>
        <Button
          asChild
          className="rounded-full bg-green-600 hover:bg-green-700"
        >
          <Link href="/new-story">Write a story</Link>
        </Button>
      </div>

      <Tabs defaultValue="drafts" className="w-full">
        <TabsList className="mb-6 border-b w-full justify-start rounded-none h-auto p-0 bg-transparent">
          <TabsTrigger
            value="drafts"
            className="data-[state=active]:border-black data-[state=active]:shadow-none border-b-2 border-transparent rounded-none pb-3 px-1 mr-6"
          >
            Drafts{" "}
            {initialDrafts.length > 0 ? `(${initialDrafts.length}+)` : ""}
          </TabsTrigger>
          <TabsTrigger
            value="published"
            className="data-[state=active]:border-black data-[state=active]:shadow-none border-b-2 border-transparent rounded-none pb-3 px-1"
          >
            Published
          </TabsTrigger>
        </TabsList>

        <TabsContent value="drafts">
          <DraftsFeed initialPosts={initialDrafts} currentUser={currentUser} />
        </TabsContent>

        <TabsContent value="published">
          <PublishedFeed
            initialPosts={initialPublished}
            currentUser={currentUser}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
}

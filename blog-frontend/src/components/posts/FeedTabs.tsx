import { PostCardResponse, UserResponse } from "@/api/generated/model";
import { InfoTooltip } from "@/components/InfoTooltip";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { HomeFeed } from "./HomeFeed";

interface FeedTabsProps {
  postCards: PostCardResponse[];
  currentUser?: UserResponse | undefined;
}

export function FeedTabs({ postCards, currentUser }: FeedTabsProps) {
  return (
    <Tabs defaultValue="foryou" className="w-full">
      <TabsList>
        <InfoTooltip message="Recommended stories based on your reading history">
          <TabsTrigger value="foryou">For you</TabsTrigger>
        </InfoTooltip>
        <InfoTooltip message="Featured stories from publication you follow">
          <TabsTrigger value="featured">Featured</TabsTrigger>
        </InfoTooltip>
      </TabsList>

      <TabsContent value="foryou" className="pt-4">
        {/* Pass the pre-fetched posts down */}
        <HomeFeed initialPosts={postCards} currentUser={currentUser} />
      </TabsContent>

      <TabsContent value="featured" className="pt-4">
        {/* You can add a different filtered list here later */}
        <div className="py-8 text-center text-muted-foreground">
          No featured posts yet.
        </div>
      </TabsContent>
    </Tabs>
  );
}

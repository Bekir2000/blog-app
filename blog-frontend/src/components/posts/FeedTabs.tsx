import { getAllPostCards } from "@/api/generated/server/post-controller/post-controller";
import { InfoTooltip } from "@/components/InfoTooltip";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { getUser } from "@/lib/auth";
import { HomeFeed } from "./HomeFeed";

export async function FeedTabs({ searchQuery }: { searchQuery: string }) {
  const postsPage = await getAllPostCards({
    query: searchQuery,
    page: 0,
    size: 5,
  });

  const postCards = postsPage.content ?? [];
  const currentUser = await getUser();

  return (
    // Updated: Changed w-3xl to w-full
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
        <HomeFeed initialPosts={postCards} currentUser={currentUser} />
      </TabsContent>
    </Tabs>
  );
}

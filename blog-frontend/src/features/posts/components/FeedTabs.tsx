import { InfoTooltip } from "@/components/InfoTooltip";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { mockposts } from "@/data/posts";
import { PostsGrid } from "@/features/posts/components/PostGrid";

export function FeedTabs() {
  return (
    <Tabs defaultValue="foryou" className="w-5xl">
      {/* thin baseline like the original */}
      <TabsList>
        <InfoTooltip message="Recommended stories based on your reading history">
          <TabsTrigger value="foryou">For you</TabsTrigger>
        </InfoTooltip>

        <InfoTooltip message="Featured stories from publication you follow">
          <TabsTrigger value="featured">Featured</TabsTrigger>
        </InfoTooltip>
      </TabsList>

      <TabsContent value="foryou" className="pt-4">
        <PostsGrid posts={mockposts} />
      </TabsContent>

      <TabsContent value="featured" className="pt-4">
        <div className="w-4xl text-center text-gray-500">
          Featured posts will go here.
        </div>
      </TabsContent>
    </Tabs>
  );
}

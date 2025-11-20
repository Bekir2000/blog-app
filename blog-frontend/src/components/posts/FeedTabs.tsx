import { getAllPosts } from "@/api/generated/post-controller/post-controller";
import { InfoTooltip } from "@/components/InfoTooltip";
import { PostsGrid } from "@/components/posts/PostGrid";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

export async function FeedTabs() {
  const postWithBookMarks = await getAllPosts();

  //console.log("postWithBookMarks", postWithBookMarks);
  return (
    <Tabs defaultValue="foryou" className="mx-auto w-3xl">
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
        <PostsGrid postWithBookMarks={postWithBookMarks} />
      </TabsContent>

      <TabsContent value="featured" className="pt-4">
        <div className="w-4xl text-center text-gray-500">
          Featured posts will go here.
        </div>
      </TabsContent>
    </Tabs>
  );
}

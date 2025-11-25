import { getAllPosts } from "@/api/generated/post-controller/post-controller";
import { InfoTooltip } from "@/components/InfoTooltip";
import { PostsGrid } from "@/components/posts/PostGrid";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { getUser } from "@/lib/auth";

export async function FeedTabs() {
  // FIX: Explicitly pass page 0 and size 5 to match your Server Action logic
  const postWithBookMarks = await getAllPosts({
    page: 0,
    size: 5,
  });

  const currentUser = await getUser();

  return (
    <Tabs defaultValue="foryou" className="mx-auto w-3xl">
      <TabsList>
        <InfoTooltip message="Recommended stories based on your reading history">
          <TabsTrigger value="foryou">For you</TabsTrigger>
        </InfoTooltip>

        <InfoTooltip message="Featured stories from publication you follow">
          <TabsTrigger value="featured">Featured</TabsTrigger>
        </InfoTooltip>
      </TabsList>

      <TabsContent value="foryou" className="pt-4">
        <PostsGrid initialPosts={postWithBookMarks} currentUser={currentUser} />
      </TabsContent>

      <TabsContent value="featured" className="pt-4">
        <div className="w-4xl text-center text-gray-500">
          Featured posts will go here.
        </div>
      </TabsContent>
    </Tabs>
  );
}

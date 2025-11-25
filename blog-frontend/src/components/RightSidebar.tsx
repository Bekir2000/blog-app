"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Plus } from "lucide-react";

export default function RightSidebar() {
  const topics = [
    "Technology",
    "Self Improvement",
    "Writing",
    "Productivity",
    "AI",
    "Java",
    "Spring Boot",
  ];

  const suggestions = [
    {
      name: "Kuriko Iwai",
      desc: "ML Engineer | Building Agentic AI Framework",
      avatar: "/kuriko.jpg",
    },
    {
      name: "AI Advances",
      desc: "Democratizing access to artificial intelligence",
      avatar: "/ai.png",
    },
  ];

  return (
    // Sticky Sidebar Logic:
    // 1. sticky top-24: Sticks 24px from top (below navbar)
    // 2. h-fit: Required for sticky to calculate height correctly
    // 3. hidden lg:block: Only show on large screens
    <aside className="hidden lg:block w-[350px] sticky top-24 h-fit space-y-6 pb-10 mr-6">
      {/* Recommended Topics */}
      <Card className="border-none shadow-none bg-gray-50/50">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-bold text-gray-900">
            Recommended topics
          </CardTitle>
        </CardHeader>
        <CardContent className="flex flex-wrap gap-2">
          {topics.map((topic) => (
            <Badge
              key={topic}
              variant="secondary"
              className="rounded-full px-4 py-1.5 text-sm font-normal bg-gray-200 hover:bg-gray-300 transition-colors cursor-pointer text-gray-700"
            >
              {topic}
            </Badge>
          ))}
          <button className="text-sm text-green-600 hover:text-green-700 mt-2 font-medium">
            See more topics
          </button>
        </CardContent>
      </Card>

      {/* Who to follow */}
      <Card className="border-none shadow-none">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-bold text-gray-900">
            Who to follow
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-5">
          {suggestions.map((s) => (
            <div
              key={s.name}
              className="flex items-start justify-between gap-3"
            >
              <div className="flex items-center gap-3">
                <Avatar className="h-9 w-9">
                  <AvatarImage src={s.avatar} />
                  <AvatarFallback>{s.name[0]}</AvatarFallback>
                </Avatar>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-semibold text-gray-900 truncate">
                    {s.name}
                  </p>
                  <p className="text-xs text-gray-500 line-clamp-1 break-all">
                    {s.desc}
                  </p>
                </div>
              </div>
              <Button
                variant="outline"
                size="sm"
                className="rounded-full h-8 px-4 border-gray-400 hover:border-black hover:bg-transparent transition-colors"
              >
                Follow
              </Button>
            </div>
          ))}
          <button className="text-sm text-green-600 hover:text-green-700 font-medium">
            See more suggestions
          </button>
        </CardContent>
      </Card>

      {/* Reading List */}
      <Card className="border-none shadow-none">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-bold text-gray-900">
            Reading list
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex flex-col gap-4 text-sm text-gray-500">
            <p>
              Click the{" "}
              <span className="inline-flex align-middle">
                <Plus className="w-3 h-3" />
              </span>{" "}
              on any story to easily add it to your reading list or a custom
              list that you can share.
            </p>
          </div>
        </CardContent>
      </Card>
    </aside>
  );
}

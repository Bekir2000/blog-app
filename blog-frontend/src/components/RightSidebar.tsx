// components/RightSidebar.tsx
"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function RightSidebar() {
  const topics = [
    "Self Improvement",
    "Cryptocurrency",
    "Writing",
    "Relationships",
    "Politics",
    "Productivity",
    "Money",
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
    {
      name: "Aj",
      desc: "Ethical Hacker and Cybersecurity Expert",
      avatar: "/aj.jpg",
    },
  ];

  const readingList = [
    {
      author: "Abhinav",
      title: "Docker Is Dead—And It’s About Time",
      date: "Jun 9",
      avatar: "/abhinav.jpg",
    },
    {
      author: "Analyst Uttam",
      title: "SQL Functions That Solve 80% of Data Problems",
      date: "Aug 9",
      avatar: "/uttam.jpg",
    },
  ];

  return (
    <aside className="max-w-140">
      {/* Recommended Topics */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Recommended topics</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-wrap gap-2">
          {topics.map((topic) => (
            <Badge
              key={topic}
              variant="secondary"
              className="rounded-full px-4 py-1 text-sm"
            >
              {topic}
            </Badge>
          ))}
          <p className="mt-3 text-sm text-gray-600 cursor-pointer hover:underline">
            See more topics
          </p>
        </CardContent>
      </Card>

      {/* Who to follow */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Who to follow</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {suggestions.map((s) => (
            <div key={s.name} className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Avatar>
                  <AvatarImage src={s.avatar} />
                  <AvatarFallback>{s.name[0]}</AvatarFallback>
                </Avatar>
                <div>
                  <p className="font-medium">{s.name}</p>
                  <p className="text-sm text-gray-600 line-clamp-1">{s.desc}</p>
                </div>
              </div>
              <Button
                variant="outline"
                className="rounded-full text-sm px-4 py-1"
              >
                Follow
              </Button>
            </div>
          ))}
          <p className="mt-2 text-sm text-gray-600 cursor-pointer hover:underline">
            See more suggestions
          </p>
        </CardContent>
      </Card>

      {/* Reading List */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Your Reading list</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {readingList.map((r) => (
            <div key={r.title} className="space-y-1">
              <div className="flex items-center gap-2">
                <Avatar className="w-6 h-6">
                  <AvatarImage src={r.avatar} />
                  <AvatarFallback>{r.author[0]}</AvatarFallback>
                </Avatar>
                <span className="text-sm">{r.author}</span>
              </div>
              <p className="font-medium">{r.title}</p>
              <span className="text-xs text-gray-500">⭐ {r.date}</span>
            </div>
          ))}
        </CardContent>
      </Card>
    </aside>
  );
}

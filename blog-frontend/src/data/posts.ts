export type Post = {
  id: string;
  author: {
    name: string;
    avatarUrl: string;
    org?: string; // optional, like "In CodeElevation"
  };
  title: string;
  description: string;
  date: string; // ISO format
  reads: number;
  comments: number;
  isStarred: boolean;
  imageUrl: string;
};

export const mockposts: Post[] = [
  {
    id: "1",
    author: {
      name: "Joe Njenga",
      avatarUrl: "/avatars/joe-njenga.jpg",
    },
    title: "9 Books Every AI Engineer Should Read (To Go Fully Professional)",
    description:
      "Picking an AI engineering book and reading it from start to finish is tough!",
    date: "2025-07-23",
    reads: 620,
    comments: 13,
    isStarred: true,
    imageUrl:
      "https://miro.medium.com/v2/resize:fit:1400/format:webp/1*SdvICPEkDR4UQrThkXGKvQ.png",
  },
  {
    id: "2",
    author: {
      name: "Devrim Ozcay",
      avatarUrl: "/avatars/devrim-ozcay.jpg",
      org: "CodeElevation",
    },
    title: "Python is Dying and Nobody Wants to Admit It",
    description:
      "You won’t hear this at PyCon. You won’t read it in the official Python blog. But after 2 years of Python development and...",
    date: "2025-09-02", // ~6 days ago
    reads: 708,
    comments: 152,
    isStarred: false,
    imageUrl:
      "https://miro.medium.com/v2/resize:fit:1400/format:webp/0*KzVJGUUA2UFdSmqQ",
  },
  {
    id: "3",
    author: {
      name: "Abdur Rahman",
      avatarUrl:
        "https://miro.medium.com/v2/resize:fill:64:64/1*L6qxuEdgGIfD_4Jbg_1U9g.jpeg",
      org: "Codrift",
    },
    title:
      "7 Python Automation Projects You Can Build in Less Than 2 Hours Each",
    description: "Small Builds. Big Impact.",
    date: "2025-08-01",
    reads: 352,
    comments: 6,
    isStarred: true,
    imageUrl:
      "https://miro.medium.com/v2/resize:fit:2000/format:webp/0*jNm75DMytWXufzDP",
  },
];

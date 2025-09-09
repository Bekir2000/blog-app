import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */

  // To Do: delete after setting up api
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "miro.medium.com",
      },
    ],
  },
};

export default nextConfig;

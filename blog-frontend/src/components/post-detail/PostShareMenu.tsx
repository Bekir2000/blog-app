"use client";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Link as LinkIcon, Linkedin, Share2, Twitter } from "lucide-react";
import { ComponentProps } from "react";
import { toast } from "sonner";

// 1. NEW: A stroke-based SVG to match Lucide style perfectly
const WhatsAppOutlineIcon = ({
  className,
  ...props
}: ComponentProps<"svg">) => {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
      {...props}
    >
      <path d="M3 21l1.65-3.8a9 9 0 1 1 3.4 2.9L3 21" />
      <path d="M9 10a.5.5 0 0 0 1 0V9a.5.5 0 0 0-1 0v1a5 5 0 0 0 5 5h1a.5.5 0 0 0 0-1h-1a.5.5 0 0 0 0 1" />
    </svg>
  );
};

interface PostShareMenuProps {
  postTitle?: string;
}

export function PostShareMenu({
  postTitle = "Check out this post",
}: PostShareMenuProps) {
  const getCurrentUrl = () => window.location.href;

  const handleCopyLink = () => {
    navigator.clipboard.writeText(getCurrentUrl());
    toast.success("Link copied to clipboard");
  };

  const handleShareTwitter = () => {
    const text = encodeURIComponent(postTitle);
    const url = encodeURIComponent(getCurrentUrl());
    window.open(
      `https://twitter.com/intent/tweet?text=${text}&url=${url}`,
      "_blank"
    );
  };

  const handleShareLinkedIn = () => {
    const url = encodeURIComponent(getCurrentUrl());
    window.open(
      `https://www.linkedin.com/sharing/share-offsite/?url=${url}`,
      "_blank"
    );
  };

  const handleShareWhatsApp = () => {
    const text = encodeURIComponent(postTitle);
    const url = encodeURIComponent(getCurrentUrl());
    window.open(`https://wa.me/?text=${text}%20${url}`, "_blank");
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="text-muted-foreground hover:text-foreground hover:bg-muted"
        >
          <Share2 className="w-5 h-5" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-56">
        <DropdownMenuItem onClick={handleCopyLink} className="cursor-pointer">
          <LinkIcon className="mr-2 h-4 w-4" /> Copy link
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={handleShareTwitter}
          className="cursor-pointer"
        >
          <Twitter className="mr-2 h-4 w-4" /> Share on Twitter
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={handleShareLinkedIn}
          className="cursor-pointer"
        >
          <Linkedin className="mr-2 h-4 w-4" /> Share on LinkedIn
        </DropdownMenuItem>

        <DropdownMenuItem
          onClick={handleShareWhatsApp}
          className="cursor-pointer"
        >
          {/* 2. Use new outline icon with official WhatsApp green color */}
          <WhatsAppOutlineIcon className="mr-2 h-4 w-4 text-[#25D366]" />
          Share on WhatsApp
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

"use client";

// 1. Import the Server Action
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { searchUnsplash } from "@/lib/actions/unsplash";
import { Camera, Loader2, Search } from "lucide-react";
import Image from "next/image";
import { useState } from "react";

// Types for Unsplash API response
interface UnsplashPhoto {
  id: string;
  urls: { regular: string; small: string };
  user: { name: string; username: string };
  links: { html: string };
}

interface UnsplashModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSelect: (url: string, photographerName: string) => void;
}

export function UnsplashModal({
  open,
  onOpenChange,
  onSelect,
}: UnsplashModalProps) {
  const [query, setQuery] = useState("");
  const [photos, setPhotos] = useState<UnsplashPhoto[]>([]);
  const [loading, setLoading] = useState(false);

  // 2. Updated Search Function using Server Action
  const handleSearch = async () => {
    if (!query) return;
    setLoading(true);

    try {
      // Call the Server Action directly!
      // No "fetch", no "API Key" needed here.
      const results = await searchUnsplash(query);
      setPhotos(results as UnsplashPhoto[]);
    } catch (error) {
      console.error("Unsplash Error:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleSearch();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[650px] p-0 gap-0 overflow-hidden bg-white dark:bg-zinc-900 border-gray-200 dark:border-gray-800">
        {/* Header Section */}
        <div className="p-6 border-b border-gray-100 dark:border-gray-800">
          <DialogHeader className="mb-4">
            <DialogTitle className="flex items-center gap-2 text-xl font-serif">
              <Camera className="w-5 h-5 text-gray-500" />
              Search Unsplash
            </DialogTitle>
          </DialogHeader>

          <div className="flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-3 h-4 w-4 text-gray-400" />
              <Input
                placeholder="Search for 'technology', 'coffee', 'minimal'..."
                className="pl-9 bg-gray-50 border-gray-200 focus-visible:ring-black dark:bg-zinc-800 dark:border-zinc-700"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={handleKeyDown}
                autoFocus
              />
            </div>
            <Button
              onClick={handleSearch}
              disabled={loading}
              className="bg-black hover:bg-gray-800 text-white dark:bg-white dark:text-black"
            >
              {loading ? (
                <Loader2 className="animate-spin w-4 h-4" />
              ) : (
                "Search"
              )}
            </Button>
          </div>
        </div>

        {/* Results Grid */}
        <div className="p-6 bg-gray-50/50 dark:bg-zinc-900/50 min-h-[400px]">
          {photos.length > 0 ? (
            <div className="grid grid-cols-3 gap-3">
              {photos.map((photo) => (
                <div
                  key={photo.id}
                  className="group relative aspect-[4/3] cursor-pointer overflow-hidden rounded-md bg-gray-200 shadow-sm hover:shadow-md transition-all border border-transparent hover:border-black/10"
                  // 3. IMPORTANT: Use photo.urls.regular (Image URL), not photo.links.html (Website Link)
                  onClick={() => onSelect(photo.urls.regular, photo.user.name)}
                >
                  <Image
                    src={photo.urls.small}
                    alt={photo.user.name}
                    fill
                    className="object-cover transition-transform duration-500 group-hover:scale-110"
                  />
                  {/* Photographer Credit Overlay */}
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 transition-opacity group-hover:opacity-100 flex items-end p-2">
                    <p className="text-[10px] text-white font-medium truncate w-full">
                      by {photo.user.name}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            // Empty State
            <div className="flex flex-col items-center justify-center h-[300px] text-gray-400">
              <Search className="w-12 h-12 mb-2 opacity-20" />
              <p className="text-sm">Search for high-resolution photos</p>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}

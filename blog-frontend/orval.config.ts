import { defineConfig } from "orval";

export default defineConfig({
  // 1. SERVER: Generates plain functions for Server Components (SSR)
  blogServer: {
    input: "http://localhost:8080/v3/api-docs",
    output: {
      target: "src/api/generated/server", // 👈 Distinct file
      mode: "tags-split",
      schemas: "src/api/generated/model",
      // No 'client' field = defaults to plain functions
      override: {
        mutator: {
          path: "./src/lib/api-client.ts",
          name: "serverFetch",
        },
      },
    },
  },

  // 2. CLIENT: Generates React Query Hooks for Client Components
  blogClient: {
    input: {
      target: "http://localhost:8080/v3/api-docs",
      filters: {
        tags: ["post-controller"], // 👈 Only posts for client
      },
    },
    output: {
      target: "src/api/generated/client", // 👈 Distinct file
      mode: "tags-split",
      schemas: "src/api/generated/model",
      client: "react-query", // 👈 Generates hooks
      override: {
        mutator: {
          path: "./src/lib/api-client.ts",
          name: "clientFetch",
        },
        query: {
          useInfinite: true, // Enable infinite scroll hooks
          useInfiniteQueryParam: "page", // Your backend expects 'page'
        },
      },
    },
  },
});

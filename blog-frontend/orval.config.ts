import { defineConfig } from "orval";

export default defineConfig({
  springBackend: {
    input: "http://localhost:8080/v3/api-docs",
    output: {
      // 1. TARGET (Required): Where to write the generated API functions
      target: "src/api/generated/endpoints.ts",

      // 2. MODE: 'tags-split' creates one file per Spring Controller
      mode: "tags-split",

      // 3. SCHEMAS: Where to put the Java DTO interfaces
      schemas: "src/api/generated/model",

      // 4. OVERRIDE: Connects your custom Server Action fetcher
      override: {
        mutator: {
          path: "./src/lib/api-client.ts",
          name: "serverFetch",
        },
      },
    },
  },
});

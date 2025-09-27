// src/lib/ApiError.ts
export class ApiError extends Error {
  constructor(public status: number, public body: string, message?: string) {
    super(message ?? `HTTP ${status}`);
    this.name = "ApiError";
  }
}

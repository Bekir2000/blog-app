import { z } from "zod";

export const loginSchema = z.object({
  email: z.string(),
  password: z.string(),
});
export type LoginRequest = z.infer<typeof loginSchema>;

export const registerSchema = z.object({
  username: z.string(),
  firstName: z.string(),
  lastName: z.string(),
  email: z.string(),
  password: z.string(),
  profileImageUrl: z.string().optional(),
});
export type RegisterRequest = z.infer<typeof registerSchema>;

export type UserResponse = {
  id: string;
  username: string;
  firstname: string;
  lastname: string;
  profileImageUrl?: string;
};

export type AuthResponse = {
  token: string;
  expiresIn: number;
};

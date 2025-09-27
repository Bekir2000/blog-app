// lib/ApiClients.ts
import { AuthApiClient } from "./AuthApiClient";
import { BaseApiClient } from "./BaseApiClient";

export const ApiClients = {
  public: new BaseApiClient(process.env.NEXT_PUBLIC_API_URL!),
  private: new AuthApiClient(process.env.NEXT_PUBLIC_API_URL!),
};

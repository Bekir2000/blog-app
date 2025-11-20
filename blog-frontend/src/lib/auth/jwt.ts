// lib/auth/jwt.ts

/**
 * Decode the `exp` claim (seconds since epoch) from a JWT payload.
 */
export function decodeJwtExp(token: string): number | null {
  try {
    const parts = token.split(".");
    if (parts.length < 2) return null;
    const payloadBase64 = parts[1];
    const payloadJson = Buffer.from(payloadBase64, "base64").toString();
    const payload = JSON.parse(payloadJson);
    return typeof payload.exp === "number" ? payload.exp : null;
  } catch {
    return null;
  }
}

/** * Return expiration time in milliseconds, or null if invalid
 */
export function expiresAtMs(token: string): number | null {
  const exp = decodeJwtExp(token);
  return exp === null ? null : exp * 1000;
}

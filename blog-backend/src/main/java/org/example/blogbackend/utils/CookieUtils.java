package org.example.blogbackend.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;

@NoArgsConstructor
public class CookieUtils {


    public static void addHttpOnlyCookie(HttpServletResponse res, String name, String value, long maxAgeSeconds,
                                         boolean secure, String sameSite, String path, String domain) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(maxAgeSeconds)
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
    }

    public static void clearCookie(HttpServletResponse res, String name, boolean secure,
                                   String sameSite, String path, String domain) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
               // .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(0)
                .build();
        res.addHeader("Set-Cookie", cookie.toString());
    }

    public static String getCookieValue(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}


package org.isaqb.onlineexam.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CookieHelper {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Base64Handler base64;



    public void setCookie(String key, String value) {
        Cookie cookie = new Cookie(key, base64.encode(value));
        response.addCookie(cookie);
    }

    public void setCookie(String key, String value, boolean httpOnly, int maxAgeSeconds, boolean secure) {
        Cookie cookie = new Cookie(key, base64.encode(value));
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setSecure(secure);
        response.addCookie(cookie);
    }

    public String cookieNames() {
        return request == null ? "" : Stream.of(request.getCookies()).map(Cookie::getName).collect(Collectors.joining(", "));
    }



    public Optional<String> readCookie(String key) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Stream.of(request.getCookies())
            .filter( c -> c.getName().equals(key) )
            .map(Cookie::getValue)
            .map(base64::decode)
            .findFirst();
    }



    public void deleteAllCookies() {
        if (request.getCookies() != null) {
            Stream.of(request.getCookies())
                .forEach(this::deleteCookie);
        }
    }

    public void deleteCookie(String... names) {
        if (request.getCookies() != null) {
            var nameList = Arrays.asList(names);
            Stream.of(request.getCookies())
                .filter( c -> nameList.contains(c.getName()))
                .forEach(this::deleteCookie);
        }
    }

    private void deleteCookie(Cookie cookie) {
        // Delete the cookie.
        cookie.setMaxAge(0);
        // Reduce size for just returning to client.
        cookie.setValue("");
        response.addCookie(cookie);
    }

}

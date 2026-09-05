package gr.grodov.grsso.infrastructure.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Component
public class DeviceIdFilter extends OncePerRequestFilter {
    private static final String DEVICE_COOKIE_NAME = "device_id";

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String deviceId = WebUtils.getCookie(request, DEVICE_COOKIE_NAME) != null
            ? Objects.requireNonNull(WebUtils.getCookie(request, DEVICE_COOKIE_NAME)).getValue()
            : null;

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();

            ResponseCookie cookie = ResponseCookie.from(DEVICE_COOKIE_NAME, deviceId)
                .httpOnly(true)
                .secure(true)
                .sameSite(Cookie.SameSite.LAX.attributeValue())
                .path("/")
                .maxAge(Duration.ofDays(400))
            .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        request.setAttribute(DEVICE_COOKIE_NAME, deviceId);

        filterChain.doFilter(request, response);
    }
}

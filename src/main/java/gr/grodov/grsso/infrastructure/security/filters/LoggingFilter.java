package gr.grodov.grsso.infrastructure.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_LENGTH = 2000;
    private static final Set<String> SKIP_LOGGING_CONTENT_TYPES = Set.of(
        "multipart/form-data", "application/octet-stream", "image/", "video/"
    );

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_BODY_LENGTH);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                MDC.put("sessionId", "SESSION_ID:%s".formatted(session.getId()));
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                MDC.put("userId", "USER_ID:%s".formatted(authentication.getName()));
            }

            logRequest(wrappedRequest);

            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logResponse(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();

            MDC.remove("sessionId");
            MDC.remove("userId");
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String query = request.getQueryString() != null ? "?%s".formatted(request.getQueryString()) : "";
        log.info("===> {} {}", request.getMethod(), "%s%s".formatted(request.getRequestURI(), query));
    }

    private void logResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        String requestBody = extractBody(
            request.getContentAsByteArray(), request.getContentType(), request.getCharacterEncoding()
        );
        String responseBody = extractBody(
            response.getContentAsByteArray(), response.getContentType(), response.getCharacterEncoding()
        );

        log.info("<=== {} {} [{}] request_body={} response_body={}",
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus(),
            LoggingBodyMasker.mask(requestBody),
            LoggingBodyMasker.mask(responseBody)
        );
    }

    private String extractBody(byte[] content, String contentType, String encoding) {
        if (content.length == 0) {
            return "";
        } else if (contentType != null && SKIP_LOGGING_CONTENT_TYPES.stream().anyMatch(contentType::startsWith)) {
            return "[skipped: " + contentType + "]";
        }

        try {
            Charset charset = encoding != null ? Charset.forName(encoding) : StandardCharsets.UTF_8;
            String body = new String(content, charset);
            return body.length() > MAX_BODY_LENGTH
                ? body.substring(0, MAX_BODY_LENGTH) + "...[truncated]"
                : body;
        } catch (Exception ex) {
            return "[unreadable body]";
        }
    }
}
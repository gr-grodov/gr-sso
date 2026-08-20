package gr.grodov.grsso.integration.flow_components;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImitationCookie {
    private final Map<String, String> cookies = new HashMap<>();
    private static final Pattern COOKIE_ITEM_PATTERN = Pattern.compile("^([^=]+)=([^;]*)");

    public void addNewCookieValues(ResponseEntity<?> response) {
        List<String> responseCookie = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (responseCookie == null || responseCookie.isEmpty()) {
            return;
        }

        for (String header: responseCookie) {
            Matcher matcher = COOKIE_ITEM_PATTERN.matcher(header);
            if (matcher.find()) {
                cookies.put(matcher.group(1).trim(), matcher.group(2).trim());
            }
        }
    }

    public HttpHeaders headers() {
        String headerCookie = cookies.entrySet().stream()
            .map(val -> "%s=%s".formatted(val.getKey(), val.getValue()))
            .reduce("%s;%s"::formatted)
            .orElse("");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, headerCookie);
        return headers;
    }

    public HttpHeaders jsonHeaders() {
        HttpHeaders headers = headers();
        headers.set("X-XSRF-TOKEN", cookies.get("XSRF-TOKEN"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public void addHeader(String headerName, String value) {
        cookies.put(headerName, value);
    }

    /**
     * {@link org.springframework.security.web.csrf.CookieCsrfTokenRepository CookieCsrfTokenRepository}
     * Default cookie name get from CookieCsrfTokenRepository
     */
    public void addCSRFHeader(CsrfToken csrfToken) {
        cookies.put("XSRF-TOKEN", csrfToken.getToken());
    }
}

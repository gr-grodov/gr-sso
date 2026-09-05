package gr.grodov.grsso.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class DeviceResolveUtils {

    public static final String DEVICE_COOKIE_NAME = "device_id";
    private final UserAgentAnalyzer userAgentAnalyzer;

    public DeviceResolveUtils() {
        this.userAgentAnalyzer = UserAgentAnalyzer
            .newBuilder()
            .withCache(1000)
            .withField(UserAgent.DEVICE_CLASS)
            .withField(UserAgent.OPERATING_SYSTEM_NAME)
            .withField(UserAgent.AGENT_CLASS)
            .withField(UserAgent.AGENT_NAME)
        .build();
    }

    public String deviceIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public String deviceUserAgent(HttpServletRequest request) {
        UserAgent userAgent = userAgentAnalyzer.parse(request.getHeader(HttpHeaders.USER_AGENT));
        return "%s: %s (%s, %s)".formatted(
            userAgent.get(UserAgent.DEVICE_CLASS).getValue(),
            userAgent.get(UserAgent.OPERATING_SYSTEM_NAME).getValue(),
            userAgent.get(UserAgent.AGENT_CLASS).getValue(),
            userAgent.get(UserAgent.AGENT_NAME).getValue()
        );
    }

    public String deviceId(HttpServletRequest request) {
        if (request.getAttribute(DEVICE_COOKIE_NAME) instanceof String deviceId) {
            return deviceId;
        }

        Cookie cookie = WebUtils.getCookie(request, DEVICE_COOKIE_NAME);
        if (cookie != null) {
            return cookie.getValue();
        }
        throw new IllegalStateException("device_id is not available — DeviceIdFilter must run before this call");
    }

    public DeviceContext deviceContext(HttpServletRequest request) {
        return new DeviceContext(deviceId(request), deviceIpAddress(request), deviceUserAgent(request));
    }
}

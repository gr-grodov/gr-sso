package gr.grodov.grsso.session_sso.service;

import gr.grodov.grsso.session_sso.service.dto.DeviceInfo;
import gr.grodov.grsso.session_sso.service.dto.DeviceContext;
import gr.grodov.grsso.session_sso.domain.entity.DeviceType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import nl.basjes.parse.useragent.classify.DeviceClass;
import nl.basjes.parse.useragent.classify.UserAgentClassifier;
import org.springframework.http.HttpHeaders;
import org.springframework.modulith.NamedInterface;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Set;

@NamedInterface("service")
@Service
public class DeviceResolveService {

    public static final String DEVICE_COOKIE_NAME = "device_id";

    private final UserAgentAnalyzer userAgentAnalyzer;

    public DeviceResolveService() {
        this.userAgentAnalyzer = UserAgentAnalyzer
            .newBuilder()
            .withCache(1_000)
            .withField(UserAgent.DEVICE_CLASS)
            .withField(UserAgent.OPERATING_SYSTEM_NAME)
            .withField(UserAgent.AGENT_CLASS)
            .withField(UserAgent.AGENT_NAME_VERSION)
        .build();
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

    public String deviceIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public String deviceUserAgent(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    public DeviceType deviceType(HttpServletRequest request) {
        UserAgent userAgent = userAgentAnalyzer.parse(request.getHeader(HttpHeaders.USER_AGENT));
        return resolveDeviceType(userAgent);
    }

    public DeviceContext deviceContext(HttpServletRequest request) {
        return new DeviceContext(deviceId(request), deviceIpAddress(request), deviceUserAgent(request), deviceType(request));
    }

    public DeviceInfo deviceInfo(String deviceUserAgent) {
        UserAgent userAgent = userAgentAnalyzer.parse(deviceUserAgent);
        return new DeviceInfo(
            UserAgentClassifier.getDeviceClass(userAgent),
            userAgent.get(UserAgent.OPERATING_SYSTEM_NAME).getValue(),
            userAgent.get(UserAgent.AGENT_CLASS).getValue(),
            userAgent.get(UserAgent.AGENT_NAME_VERSION).getValue()
        );
    }


    private DeviceType resolveDeviceType(UserAgent userAgent) {
        DeviceClass deviceClass = UserAgentClassifier.getDeviceClass(userAgent);

        if (DeviceClass.DESKTOP.equals(deviceClass)) {
            return DeviceType.DESKTOP;
        } else if (Set.of(DeviceClass.TV, DeviceClass.SET_TOP_BOX, DeviceClass.SMART_DISPLAY).contains(deviceClass)) {
            return DeviceType.TV;
        } else if (Set.of(DeviceClass.UNCLASSIFIED, DeviceClass.UNKNOWN).contains(deviceClass)) {
            return DeviceType.UNKNOWN;
        } else if (Set.of(
            DeviceClass.CAR,
            DeviceClass.VOICE,
            DeviceClass.HOME_APPLIANCE,
            DeviceClass.HANDHELD_GAME_CONSOLE,
            DeviceClass.GAME_CONSOLE).contains(deviceClass)
        ) {
            return DeviceType.DEVICE;
        } else if (UserAgentClassifier.isMobile(userAgent)) {
            return DeviceType.MOBILE;
        } else if (!UserAgentClassifier.isHuman(userAgent)) {
            return DeviceType.BOT;
        } else {
            return DeviceType.UNKNOWN;
        }
    }
}

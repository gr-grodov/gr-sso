package gr.grodov.grsso.session_sso.service;

import gr.grodov.grsso.session_sso.domain.entity.DeviceType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.classify.DeviceClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceResolveServiceTest {

    private static final String DEVICE_COOKIE_NAME = "device_id";

    @Mock
    private HttpServletRequest httpRequest;
    private final DeviceResolveService deviceResolveService = new DeviceResolveService();

    @Test
    void deviceId_withAttribute_returnDeviceId() {
        when(httpRequest.getAttribute(DEVICE_COOKIE_NAME)).thenReturn("device-id");

        var result = deviceResolveService.deviceId(httpRequest);

        assertThat(result).isEqualTo("device-id");
    }

    @Test
    void deviceId_withCookie_returnDeviceId() {
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{new Cookie(DEVICE_COOKIE_NAME, "device-id")});

        var result = deviceResolveService.deviceId(httpRequest);

        assertThat(result).isEqualTo("device-id");
    }

    @Test
    void deviceId_withoutAttributeAndCookie() {
        assertThatThrownBy(() -> deviceResolveService.deviceId(httpRequest))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deviceIpAddress_withProxy_returnIpAddress() {
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn("127.0.0.1");

        var result = deviceResolveService.deviceIpAddress(httpRequest);

        assertThat(result).isEqualTo("127.0.0.1");
    }

    @Test
    void deviceIpAddress_withNoProxy_returnIpAddress() {
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var result = deviceResolveService.deviceIpAddress(httpRequest);

        assertThat(result).isEqualTo("127.0.0.1");
    }

    @Test
    void deviceUserAgent_withUserAgent_returnDeviceUserAgent() {
        when(httpRequest.getHeader(HttpHeaders.USER_AGENT)).thenReturn(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36"
        );

        var result = deviceResolveService.deviceUserAgent(httpRequest);

        assertThat(result).isEqualTo("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");
    }

    @Test
    void deviceType_withUserAgent_returnDeviceType() {
        when(httpRequest.getHeader(HttpHeaders.USER_AGENT)).thenReturn(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36"
        );

        var result = deviceResolveService.deviceType(httpRequest);

        assertThat(result).isEqualTo(DeviceType.DESKTOP);
    }

    @Test
    void deviceContext_withCorrectData_returnContext() {
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn("127.0.0.1");
        when(httpRequest.getHeader(HttpHeaders.USER_AGENT)).thenReturn(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36"
        );
        when(httpRequest.getAttribute(DEVICE_COOKIE_NAME)).thenReturn("device-id");

        var result = deviceResolveService.deviceContext(httpRequest);

        assertThat(result.deviceId()).isEqualTo("device-id");
        assertThat(result.deviceIpAddress()).isEqualTo("127.0.0.1");
        assertThat(result.deviceUserAgent()).isEqualTo("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");
        assertThat(result.deviceType()).isEqualTo(DeviceType.DESKTOP);
    }

    @Test
    void deviceInfo_withDesktopUserAgent_returnDeviceInfo() {
        var result = deviceResolveService.deviceInfo("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36");

        assertThat(result.device()).isEqualTo(DeviceClass.DESKTOP);
        assertThat(result.operationSystem()).isEqualTo("Linux");
        assertThat(result.agent()).isEqualTo("Browser");
        assertThat(result.agentNameVersion()).isEqualTo("Chrome 150");
    }
}
package gr.grodov.grsso.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import static gr.grodov.grsso.common.utils.DeviceResolveUtils.DEVICE_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceResolveUtilsTest {

    @Mock
    private HttpServletRequest httpRequest;

    private DeviceResolveUtils deviceResolveUtils;

    @BeforeEach
    void setUp() {
        this.deviceResolveUtils = new DeviceResolveUtils();
    }

    @Test
    void deviceIpAddress_withProxy_returnIpAddress() {
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn("127.0.0.1");

        var result = deviceResolveUtils.deviceIpAddress(httpRequest);

        assertThat(result).isEqualTo("127.0.0.1");
    }

    @Test
    void deviceIpAddress_withNoProxy_returnIpAddress() {
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var result = deviceResolveUtils.deviceIpAddress(httpRequest);

        assertThat(result).isEqualTo("127.0.0.1");
    }

    @Test
    void deviceUserAgent_withUserAgent_returnDeviceUserAgent() {
        when(httpRequest.getHeader(HttpHeaders.USER_AGENT)).thenReturn(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36"
        );

        var result = deviceResolveUtils.deviceUserAgent(httpRequest);

        assertThat(result).isEqualTo("Desktop: Linux (Browser, Chrome)");
    }

    @Test
    void deviceId_withAttribute_returnDeviceId() {
        when(httpRequest.getAttribute(DEVICE_COOKIE_NAME)).thenReturn("device-id");

        var result = deviceResolveUtils.deviceId(httpRequest);

        assertThat(result).isEqualTo("device-id");
    }

    @Test
    void deviceId_withCookie_returnDeviceId() {
        when(httpRequest.getCookies()).thenReturn(new Cookie[]{new Cookie(DeviceResolveUtils.DEVICE_COOKIE_NAME, "device-id")});

        var result = deviceResolveUtils.deviceId(httpRequest);

        assertThat(result).isEqualTo("device-id");
    }

    @Test
    void deviceId_withoutAttributeAndCookie() {
        assertThatThrownBy(() -> deviceResolveUtils.deviceId(httpRequest))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deviceContext_withCorrectData_returnContext() {
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn("127.0.0.1");
        when(httpRequest.getHeader(HttpHeaders.USER_AGENT)).thenReturn(
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/150.0.0.0 Safari/537.36"
        );
        when(httpRequest.getAttribute(DEVICE_COOKIE_NAME)).thenReturn("device-id");

        var result = deviceResolveUtils.deviceContext(httpRequest);

        assertThat(result.deviceId()).isEqualTo("device-id");
        assertThat(result.deviceIpAddress()).isEqualTo("127.0.0.1");
        assertThat(result.deviceUserAgent()).isEqualTo("Desktop: Linux (Browser, Chrome)");
    }
}
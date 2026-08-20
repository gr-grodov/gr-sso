package gr.grodov.grsso.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2FlowServiceTest {

    @Mock
    private SessionService sessionService;
    @InjectMocks
    private OAuth2FlowService service;

    @Test
    void saveRedirectRequest_withRequest_setInSession() {
        String oauthRequest = "http://example.com/oauth/code";

        service.saveRedirectRequest(oauthRequest);

        verify(sessionService).setAttribute(anyString(), eq(oauthRequest));
    }

    @Test
    void getSavedRedirectRequest_withNoSavedRequest_returnNull() {
        when(sessionService.getAttribute(anyString(), any())).thenReturn(null);

        var result = service.getSavedRedirectRequest();

        assertThat(result).isNull();
        verify(sessionService).getAttribute(anyString(), eq(String.class));
        verify(sessionService).removeAttribute(anyString());
    }

    @Test
    void getSavedRedirectRequest_withSavedRequest_returnRequest() {
        String oauthRequest = "http://example.com/oauth/code";
        when(sessionService.getAttribute(anyString(), eq(String.class))).thenReturn(oauthRequest);

        var result = service.getSavedRedirectRequest();

        assertThat(result).isEqualTo(oauthRequest);
        verify(sessionService).getAttribute(anyString(), eq(String.class));
        verify(sessionService).removeAttribute(anyString());
    }
}
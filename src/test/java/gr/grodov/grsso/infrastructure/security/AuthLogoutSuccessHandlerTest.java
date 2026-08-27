package gr.grodov.grsso.infrastructure.security;

import gr.grodov.grsso.common.api.ErrorResponse;
import gr.grodov.grsso.common.api.SuccessResponse;
import gr.grodov.grsso.common.utils.ApiResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthLogoutSuccessHandlerTest {

    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private Authentication authentication;

    @Mock
    private ApiResponseWriter writer;
    @InjectMocks
    private AuthLogoutSuccessHandler authLogoutSuccessHandler;

    @Test
    void onLogoutSuccess_Authentication() throws IOException {
        ArgumentCaptor<HttpStatus> status = ArgumentCaptor.forClass(HttpStatus.class);
        ArgumentCaptor<Object> body = ArgumentCaptor.forClass(Object.class);

        authLogoutSuccessHandler.onLogoutSuccess(httpRequest, httpResponse, authentication);
        verify(writer).write(eq(httpResponse), status.capture(), body.capture());

        assertThat(status.getValue()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(body.getValue()).isExactlyInstanceOf(SuccessResponse.class).satisfies(val -> {
            var response = (SuccessResponse<?>) val;
            AssertionsForClassTypes.assertThat(response.success()).isTrue();
        });
    }
}
package gr.grodov.grsso.infrastructure.security;

import gr.grodov.grsso.common.api.ErrorResponse;
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
import org.springframework.security.authentication.AccountExpiredException;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApiAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private ApiResponseWriter writer;
    @InjectMocks
    private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    @Test
    void commence_AuthenticationException() throws IOException {
        ArgumentCaptor<HttpStatus> status = ArgumentCaptor.forClass(HttpStatus.class);
        ArgumentCaptor<Object> body = ArgumentCaptor.forClass(Object.class);

        apiAuthenticationEntryPoint.commence(httpRequest, httpResponse, new AccountExpiredException(""));
        verify(writer).write(eq(httpResponse), status.capture(), body.capture());

        assertThat(status.getValue()).isEqualTo(HttpStatus.UNAUTHORIZED);
        AssertionsForClassTypes.assertThat(body.getValue()).isExactlyInstanceOf(ErrorResponse.class).satisfies(val -> {
            var response = (ErrorResponse) val;
            AssertionsForClassTypes.assertThat(response.code()).isEqualTo("unauthorized");
        });
    }
}
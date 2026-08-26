package gr.grodov.grsso.service;

import gr.grodov.grsso.authentication.api.dto.request.LoginRequest;
import gr.grodov.grsso.authentication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.SecurityContextRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private SecurityContextRepository securityContextRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    private AuthenticationService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticationService(authenticationManager, securityContextRepository);
    }

    @Test
    void authenticate_withCorrect_createContextAndSaveParams() {
        var loginRequest = new LoginRequest("user@example.com", "password");
        Authentication authResult = new UsernamePasswordAuthenticationToken("user@example.com", null);
        when(authenticationManager.authenticate(any())).thenReturn(authResult);

        service.authenticate(loginRequest, request, response);

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(securityContextRepository).saveContext(any(SecurityContext.class), same(request), same(response));
        verifyNoMoreInteractions(authenticationManager, securityContextRepository);
    }
}
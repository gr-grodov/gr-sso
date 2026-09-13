package gr.grodov.grsso.authentication.service;

import gr.grodov.grsso.authentication.api.dto.request.RegistrationRequest;
import gr.grodov.grsso.user.service.dto.UserInfoDto;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.exception.EmailAlreadyExistsException;
import gr.grodov.grsso.user.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private UserInfoService userInfoService;
    @Mock
    private VerifyEmailService verifyEmailService;
    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void registration_newUser_createUserAndReturnVerifyId() {
        var request = new RegistrationRequest("user@example.com", "Password123!");
        var userInfo = UserInfoDto.builder().id(UUID.randomUUID()).email("user@example.com").build();
        when(userInfoService.createNewUser(request.getEmail(), request.getPassword(), AuthProvider.LOCAL)).thenReturn(userInfo);
        when(httpRequest.getLocale()).thenReturn(Locale.US);
        when(verifyEmailService.sendVerifyCode(userInfo, Locale.US)).thenReturn("123456");

        var result = registrationService.registration(request, httpRequest);

        assertThat(result.userEmail()).isEqualTo("user@example.com");
        assertThat(result.verifyId()).isEqualTo("123456");
        verify(userInfoService).createNewUser("user@example.com", "Password123!", AuthProvider.LOCAL);
        verify(verifyEmailService).sendVerifyCode(any(), any());
    }

    @Test
    void registration_existUser_noSendVerifyCode() {
        var request = new RegistrationRequest("user@example.com", "Password123!");
        when(userInfoService.createNewUser(request.getEmail(), request.getPassword(), AuthProvider.LOCAL))
            .thenThrow(new EmailAlreadyExistsException());

        assertThatThrownBy(() -> registrationService.registration(request, httpRequest))
            .isInstanceOf(EmailAlreadyExistsException.class)
            .satisfies(ex -> {
                var exception = (EmailAlreadyExistsException) ex;
                assertThat(exception.getCode()).isEqualTo("email_invalid");
                assertThat(exception.getErrorsField().getFirst().field()).isEqualTo("email");
                assertThat(exception.getErrorsField().getFirst().code()).isEqualTo("already_exist");
            });

        verify(verifyEmailService, never()).sendVerifyCode(any(), any());
    }
}
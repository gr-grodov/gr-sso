package gr.grodov.grsso.authentication.service;

import gr.grodov.grsso.authentication.api.dto.request.RefreshVerifyCodeRequest;
import gr.grodov.grsso.authentication.api.dto.request.VerifyEmailRequest;
import gr.grodov.grsso.authentication.cache.VerifyEmailCode;
import gr.grodov.grsso.authentication.exception.VerifyEmailCodeEndAttemptException;
import gr.grodov.grsso.authentication.exception.VerifyEmailCodeInvalidCodeException;
import gr.grodov.grsso.authentication.exception.VerifyEmailCodeNotFoundException;
import gr.grodov.grsso.common.cache.CacheStorage;
import gr.grodov.grsso.common.event.FromResourceEmailEvent;
import gr.grodov.grsso.common.props.EmailAppProperties;
import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.user.exception.UserNotFoundException;
import gr.grodov.grsso.user.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class VerifyEmailServiceTest {

    @Mock
    private EmailAppProperties.VerifyEmailCode verifyEmailCodeProperties;

    @Mock
    private ApplicationEventPublisher publisher;
    @Autowired
    @Mock
    private CacheStorage<VerifyEmailCode> verifyEmailCodeStorage;
    @Autowired
    @Mock
    private UserInfoService userInfoService;
    @Autowired
    @Mock
    private EmailAppProperties emailProperties;
    @InjectMocks
    private VerifyEmailService verifyEmailService;

    @Test
    void sendVerifyCode_withCorrectData_generateCodeAndSendEmail() {
        when(emailProperties.verifyEmailCode()).thenReturn(verifyEmailCodeProperties);
        when(verifyEmailCodeProperties.attempt()).thenReturn(5);
        when(verifyEmailCodeProperties.minuteTime()).thenReturn(15);
        var userinfo = UserInfoDto.builder().id(123456L).email("user@example.com").build();
        ArgumentCaptor<FromResourceEmailEvent> emailEvent = ArgumentCaptor.forClass(FromResourceEmailEvent.class);

        var result = verifyEmailService.sendVerifyCode(userinfo, Locale.forLanguageTag("ru"));
        verify(publisher).publishEvent(emailEvent.capture());

        assertThat(emailEvent.getValue()).isNotNull().satisfies(event -> {
            assertThat(event.toAddress()).isEqualTo("user@example.com");
            assertThat(event.subjectMessageCode()).isEqualTo("email.verify.subject");
            assertThat(event.pathToTemplate()).isEqualTo("email-verify");
            assertThat(event.contextTemplate().get("verifyCode")).isNotNull();
            assertThat(event.contextTemplate().get("expiresInMinutes")).isNotNull();
            assertThat(event.locale()).isEqualTo(Locale.forLanguageTag("ru"));
        });
        assertThat(result).contains("123456");
    }

    @Test
    void verifyEmail_withCorrectCode_returnTrue() {
        var request = new VerifyEmailRequest("1:ABCDE-12345", "123456");
        var verifyEmailCode = new VerifyEmailCode(1L, "123456", 5);
        when(verifyEmailCodeStorage.get("1:ABCDE-12345")).thenReturn(Optional.of(verifyEmailCode));

        var result = verifyEmailService.verifyEmail(request);

        assertThat(result).isTrue();
        verify(userInfoService).enabledUserInfo(1L, true);
        verify(verifyEmailCodeStorage).delete("1:ABCDE-12345");
    }

    @Test
    void verifyEmail_expireKey_throwsVerifyEmailCodeNotFoundException() {
        var request = new VerifyEmailRequest("1:ABCDE-12345", "123456");
        when(verifyEmailCodeStorage.get("1:ABCDE-12345")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verifyEmailService.verifyEmail(request))
            .isExactlyInstanceOf(VerifyEmailCodeNotFoundException.class)
            .satisfies(ex -> {
                var exception = (VerifyEmailCodeNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("verify_email_code_not_found");
            });

        verifyNoInteractions(userInfoService);
    }

    @Test
    void verifyEmail_endAttempt_throwsVerifyEmailCodeEndAttemptException() {
        var request = new VerifyEmailRequest("1:ABCDE-12345", "123456");
        var verifyEmailCode = new VerifyEmailCode(1L, "123456", 1);
        when(verifyEmailCodeStorage.get("1:ABCDE-12345")).thenReturn(Optional.of(verifyEmailCode));

        assertThatThrownBy(() -> verifyEmailService.verifyEmail(request))
            .isExactlyInstanceOf(VerifyEmailCodeEndAttemptException.class)
            .satisfies(ex -> {
                var exception = (VerifyEmailCodeEndAttemptException) ex;
                assertThat(exception.getCode()).isEqualTo("verify_email_code_end_attempt");
            });

        verifyNoInteractions(userInfoService);
    }

    @Test
    void verifyEmail_invalidInvalidCodeAndInvalidUpdateEntity_throwsVerifyEmailCodeNotFoundException() {
        var request = new VerifyEmailRequest("1:ABCDE-12345", "654321");
        var verifyEmailCode = new VerifyEmailCode(1L, "123456", 5);
        when(verifyEmailCodeStorage.get("1:ABCDE-12345")).thenReturn(Optional.of(verifyEmailCode));
        when(verifyEmailCodeStorage.update(anyString(), any())).thenReturn(false);

        assertThatThrownBy(() -> verifyEmailService.verifyEmail(request))
            .isExactlyInstanceOf(VerifyEmailCodeNotFoundException.class)
            .satisfies(ex -> {
                var exception = (VerifyEmailCodeNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("verify_email_code_not_found");
            });

        verifyNoInteractions(userInfoService);
    }

    @Test
    void verifyEmail_invalidVerifyCode_throwsVerifyEmailCodeInvalidCodeException() {
        var request = new VerifyEmailRequest("1:ABCDE-12345", "654321");
        var verifyEmailCode = new VerifyEmailCode(1L, "123456", 5);
        when(verifyEmailCodeStorage.get("1:ABCDE-12345")).thenReturn(Optional.of(verifyEmailCode));
        when(verifyEmailCodeStorage.update(anyString(), any())).thenReturn(true);

        assertThatThrownBy(() -> verifyEmailService.verifyEmail(request))
            .isExactlyInstanceOf(VerifyEmailCodeInvalidCodeException.class)
            .satisfies(ex -> {
                var exception = (VerifyEmailCodeInvalidCodeException) ex;
                assertThat(exception.getCode()).isEqualTo("verify_email_code_invalid_code");
                assertThat(exception.getErrorsField().getFirst().field()).isEqualTo("verifyCode");
                assertThat(exception.getErrorsField().getFirst().code()).isEqualTo("invalid");
            });

        verifyNoInteractions(userInfoService);
    }

    @Test
    void refreshCode_withExistValueCache_sendEmail() {
        when(emailProperties.verifyEmailCode()).thenReturn(verifyEmailCodeProperties);
        when(verifyEmailCodeProperties.attempt()).thenReturn(5);
        when(verifyEmailCodeProperties.minuteTime()).thenReturn(15);

        var request = new RefreshVerifyCodeRequest("1:ABCDE-12345");
        var userinfo = UserInfoDto.builder().id(123456L).email("user@example.com").build();
        var verifyEmailCode = new VerifyEmailCode(1L, "123456", 0);
        ArgumentCaptor<VerifyEmailCode> refreshVerifyEmailCode = ArgumentCaptor.forClass(VerifyEmailCode.class);

        when(verifyEmailCodeStorage.get("1:ABCDE-12345")).thenReturn(Optional.of(verifyEmailCode));
        when(userInfoService.findById("1")).thenReturn(userinfo);


        verifyEmailService.refreshCode(request, Locale.forLanguageTag("ru"));
        verify(verifyEmailCodeStorage).save(any(), refreshVerifyEmailCode.capture(), any());


        verify(publisher).publishEvent(any(FromResourceEmailEvent.class));
        assertThat(refreshVerifyEmailCode.getValue()).isNotNull().satisfies(emailCode -> {
            assertThat(emailCode.userId()).isEqualTo(1L);
            assertThat(emailCode.verifyCode()).isNotNull();
            assertThat(emailCode.remainAttempt()).isEqualTo(emailProperties.verifyEmailCode().attempt());
        });
    }

    @Test
    void refreshCode_expireKey_throwsVerifyEmailCodeNotFoundException() {
        var request = new RefreshVerifyCodeRequest("1:ABCDE-12345");
        when(verifyEmailCodeStorage.get("1:ABCDE-12345")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verifyEmailService.refreshCode(request, Locale.forLanguageTag("ru")))
            .isExactlyInstanceOf(VerifyEmailCodeNotFoundException.class)
            .satisfies(ex -> {
                var exception = (VerifyEmailCodeNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("verify_email_code_not_found");
            });
    }

    @Test
    void refreshCode_deleteUser_throwsVerifyEmailCodeNotFoundException() {
        var request = new RefreshVerifyCodeRequest("1:ABCDE-12345");
        var verifyEmailCode = new VerifyEmailCode(1L, "123456", 0);
        when(verifyEmailCodeStorage.get("1:ABCDE-12345")).thenReturn(Optional.of(verifyEmailCode));
        when(userInfoService.findById("1")).thenThrow(UserNotFoundException.class);

        assertThatThrownBy(() -> verifyEmailService.refreshCode(request, Locale.forLanguageTag("ru")))
            .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void cancelVerifyEmail_withVerifyId_callDeleteCacheAndUser() {
        when(userInfoService.findById("1")).thenReturn(UserInfoDto.builder().id(1L).enabled(false).build());

        verifyEmailService.cancelVerifyEmail("1:ABCDE-12345");

        verify(verifyEmailCodeStorage).delete("1:ABCDE-12345");
        verify(userInfoService).deleteById(1L);
    }

    @Test
    void handleExpireId_existUser_deleteUser() {
        var userinfo = UserInfoDto.builder().id(1L).email("user@example.com").enabled(false).build();
        when(userInfoService.findById("1")).thenReturn(userinfo);

        verifyEmailService.handleExpireId("1:ABCDE-12345");

        verify(userInfoService).deleteById(1L);
    }

    @Test
    void handleExpireId_existEnabledUser_noChange() {
        var userinfo = UserInfoDto.builder().id(123456L).email("user@example.com").enabled(true).build();
        when(userInfoService.findById("1")).thenReturn(userinfo);

        verifyEmailService.handleExpireId("123456:ABCDE-12345");

        verify(userInfoService, never()).deleteById(any());
    }

    @Test
    void handleExpireId_noExistEnabledUser_log() {
        when(userInfoService.findById("1")).thenThrow(UserNotFoundException.class);

        verifyEmailService.handleExpireId("1:ABCDE-12345");

        verify(userInfoService, never()).deleteById(any());
    }
}
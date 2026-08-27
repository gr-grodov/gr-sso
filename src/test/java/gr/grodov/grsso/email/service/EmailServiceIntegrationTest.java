package gr.grodov.grsso.email.service;

import gr.grodov.grsso.common.event.FromResourceEmailEvent;
import gr.grodov.grsso.common.props.EmailAppProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceIntegrationTest {
    @Autowired
    private SpringTemplateEngine emailTemplateEngine;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private EmailAppProperties emailProperties;
    @Lazy
    @MockitoBean
    private JavaMailSender emailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(emailSender, emailTemplateEngine, messageSource, emailProperties);
        when(emailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @ParameterizedTest
    @MethodSource("userDtoProvider")
    void handle_withEmailEventExistRuResource_sendEmail(String codeSubject, String pathToTemplate, Map<String, Object> contextTemplate) throws MessagingException {
        FromResourceEmailEvent event = new FromResourceEmailEvent(
            "user@test.com",
            codeSubject,
            pathToTemplate,
            contextTemplate,
            Locale.forLanguageTag("ru")
        );

        assertThatCode(() -> emailService.handle(event)).doesNotThrowAnyException();
    }

    @Test
    void handle_withInvalidResource_throwException() {
        FromResourceEmailEvent event = new FromResourceEmailEvent(
            "user@test.com",
            "invalid-code",
            "invalid_path",
            Map.of(),
            Locale.forLanguageTag("ru")
        );

        assertThatThrownBy(() -> emailService.handle(event)).isInstanceOf(Exception.class);
    }


    private static Stream<Arguments> userDtoProvider() {
        return Stream.of(
            Arguments.of(
                "email.verify.subject",
                "email-verify",
                Map.of(
                    "verifyCode", "123456",
                    "expiresInMinutes", 15
                )
            )
        );
    }
}

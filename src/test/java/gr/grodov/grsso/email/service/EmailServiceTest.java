package gr.grodov.grsso.email.service;

import gr.grodov.grsso.common.event.FromResourceEmailEvent;
import gr.grodov.grsso.common.event.SimpleEmailEvent;
import gr.grodov.grsso.common.props.EmailAppProperties;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;
    @Mock
    private SpringTemplateEngine emailTemplateEngine;
    @Mock
    private MessageSource messageSource;
    @Mock
    private EmailAppProperties emailProperties;
    @InjectMocks
    private EmailService emailService;

    @Test
    void handle_withSimpleEmailEvent_sendMessage() throws UnsupportedEncodingException {
        when(emailProperties.fromAddress()).thenReturn("gr.sso.com@grsso.dev");
        SimpleEmailEvent event = new SimpleEmailEvent("user@example.com", "Test subject", "Test body");
        ArgumentCaptor<SimpleMailMessage> simpleMail = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.handle(event);
        verify(emailSender).send(simpleMail.capture());

        assertThat(simpleMail.getValue()).satisfies(mail -> {
            assertThat(mail.getFrom()).contains("gr.sso.com@grsso.dev");
            assertThat(mail.getTo()).containsExactly("user@example.com");
            assertThat(mail.getSubject()).isEqualTo("Test subject");
            assertThat(mail.getText()).isEqualTo("Test body");
        });
    }

    @Test
    void handle_withFromResourceEmailEvent_sendMessage() throws MessagingException, UnsupportedEncodingException {
        Locale locale = Locale.forLanguageTag("ru");
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        FromResourceEmailEvent event = new FromResourceEmailEvent(
            "user@example.com",
            "email.verify.subject",
            "email-verify",
            Map.of("code", "123456"),
            locale
        );
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailProperties.fromAddress()).thenReturn("gr.sso.com@grsso.dev");
        when(emailProperties.fromName()).thenReturn("GrSSO");
        when(emailTemplateEngine.process(eq("email-verify"), any(Context.class))).thenReturn("<html>rendered body</html>");
        when(messageSource.getMessage("email.verify.subject", null, locale)).thenReturn("Подтвердите email");

        emailService.handle(event);

        verify(emailSender).send(mimeMessage);
        assertThat(mimeMessage.getSubject()).isEqualTo("Подтвердите email");
        assertThat(mimeMessage.getAllRecipients())
            .extracting(Address::toString)
            .containsExactly("user@example.com");
        assertThat(mimeMessage.getFrom()).contains(new InternetAddress("gr.sso.com@grsso.dev", "GrSSO", "UTF-8"));
    }

    @Test
    void handle_withFromResourceEmailEvent_buildTemplateContext() throws MessagingException, UnsupportedEncodingException {
        Locale locale = Locale.forLanguageTag("ru");
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        FromResourceEmailEvent event = new FromResourceEmailEvent(
            "user@example.com",
            "email.verify.subject",
            "email-verify",
            Map.of(
                "verifyCode", "123456",
                "expiresInMinutes", 15
            ),
            locale
        );
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailProperties.fromAddress()).thenReturn("gr.sso.com@grsso.dev");
        when(emailProperties.fromName()).thenReturn("GrSSO");
        when(emailTemplateEngine.process(eq("email-verify"), any(Context.class))).thenReturn("<html>rendered body</html>");
        when(messageSource.getMessage("email.verify.subject", null, locale)).thenReturn("Подтвердите email");
        ArgumentCaptor<Context> templateContext = ArgumentCaptor.forClass(Context.class);

        emailService.handle(event);
        verify(emailTemplateEngine).process(eq("email-verify"), templateContext.capture());

        assertThat(templateContext.getValue()).isNotNull().satisfies(context -> {
            assertThat(context.getVariable("verifyCode")).isEqualTo("123456");
            assertThat(context.getVariable("expiresInMinutes")).isEqualTo(15);
        });
    }
}
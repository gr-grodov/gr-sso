package gr.grodov.grsso.email.service;

import gr.grodov.grsso.common.event.FromResourceEmailEvent;
import gr.grodov.grsso.common.event.SimpleEmailEvent;
import gr.grodov.grsso.common.props.EmailAppProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine emailTemplateEngine;
    private final MessageSource messageSource;
    private final EmailAppProperties emailProperties;

    @ApplicationModuleListener
    public void handle(SimpleEmailEvent simpleEmailEvent) throws UnsupportedEncodingException {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(new InternetAddress(
            emailProperties.fromAddress(), emailProperties.fromAddress(), "UTF-8").toString()
        );
        simpleMailMessage.setTo(simpleEmailEvent.toAddress());
        simpleMailMessage.setSubject(simpleEmailEvent.subject());
        simpleMailMessage.setText(simpleEmailEvent.message());
        emailSender.send(simpleMailMessage);
    }

    @ApplicationModuleListener
    public void handle(FromResourceEmailEvent emailEvent) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context(emailEvent.locale(), emailEvent.contextTemplate());
        String content = emailTemplateEngine.process(emailEvent.pathToTemplate(), context);

        String subject = messageSource.getMessage(emailEvent.subjectMessageCode(), null, emailEvent.locale());

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(emailProperties.fromAddress(), emailProperties.fromName());
        helper.setTo(emailEvent.toAddress());
        helper.setSubject(subject);
        helper.setText(content, true);

        emailSender.send(mimeMessage);
    }
}

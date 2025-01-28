package com.arra.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async      // making it async, so user doesn't need to wait
    public void sendVerificationEmail(String recipient, String username,
                                      EmailTemplateName emailTemplate, String confirmationUrl,
                                      String activationCode, String subject) throws MessagingException {

        // extra safety check for template name
        String templateName;
        if(emailTemplate == null){
            templateName = "verificationEmail";
        }else {
            templateName = emailTemplate.name();
        }

        // configure mail sender
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activationCode", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        mimeHelper.setFrom("no-reply-book-social-network@gmail.com");
        mimeHelper.setTo(recipient);
        mimeHelper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        mimeHelper.setText(template, true);

        javaMailSender.send(mimeMessage);

    }

}

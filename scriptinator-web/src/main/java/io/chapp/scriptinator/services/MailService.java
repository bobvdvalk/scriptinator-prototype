/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chapp.scriptinator.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Map;

@Service
public class MailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender sender;
    private final String fromMail;

    public MailService(TemplateEngine templateEngine, JavaMailSender sender, @Value("${spring.mail.from}") String fromMail) {
        this.templateEngine = templateEngine;
        this.sender = sender;
        this.fromMail = fromMail;
    }

    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) {
        MimeMessage message = sender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED, "UTF-8");

            helper.setFrom(fromMail);
            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariables(variables);


            // Process html content
            MimeBodyPart htmlMultipart = new MimeBodyPart();
            htmlMultipart.setContent(
                    templateEngine.process(template + ".html", context),
                    "text/html"
            );

            // Process text content
            MimeBodyPart textMultipart = new MimeBodyPart();
            textMultipart.setContent(
                    templateEngine.process(template + ".txt", context),
                    "text/plain"
            );

            MimeMultipart wrapperPart = new MimeMultipart("alternative");
            wrapperPart.addBodyPart(textMultipart);
            wrapperPart.addBodyPart(htmlMultipart);

            MimeBodyPart wrapperBodyPart = new MimeBodyPart();
            wrapperBodyPart.setContent(wrapperPart);

            helper.getMimeMultipart().addBodyPart(wrapperBodyPart);

            sender.send(message);
        } catch (MessagingException e) {
            throw new IllegalArgumentException("Could not create or send the email: " + e.getMessage(), e);
        }

    }
}

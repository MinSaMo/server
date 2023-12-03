package com.konkuk.daila.service;

import com.konkuk.daila.domain.dto.response.UserInformationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MailService {


    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final MemberService memberService;
    @Setter
    private Long memberId;

    public void sendMail() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.addRecipients(Message.RecipientType.TO, "anjm1020@gmail.com");
        mimeMessage.addRecipients(Message.RecipientType.TO, "ksun4131@gmail.com");
        mimeMessage.setSubject("[DAILA 응급상황 알림] 응급상황 알림");
        mimeMessage.setText(setContext(), "utf-8", "html");
        javaMailSender.send(mimeMessage);
    }

    private String setContext() {
        UserInformationResponseDto information = memberService.getInformation(memberId);
        StringBuilder diseasesStr = new StringBuilder();
        for (String disease : information.diseases()) {
            diseasesStr.append(disease).append(" ");
        }
        Context context = new Context();
        context.setVariable("patientName", "Kwon Sun jae");
        context.setVariable("patientAge", "24");
        context.setVariable("disease", diseasesStr);
        context.setVariable("photoUrl", "http://117.16.136.172:3000/emergency");
        context.setVariable("occurrenceDateTime", DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()));
        return templateEngine.process("mail", context);
    }
}

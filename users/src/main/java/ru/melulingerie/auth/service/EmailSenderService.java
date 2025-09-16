package ru.melulingerie.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setTo(toEmail);
        messageHelper.setSubject("Подтверждение email");
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Добро пожаловать мой милый пирожок в Melu-Lingerie!</h2>"
                + "<p style=\"font-size: 16px;\">Пожалуйста введите код подтверждения для продолжения ^_^ :</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Код подтверждения:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + code + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        messageHelper.setText(htmlMessage, true);
        mailSender.send(message);
    }
}

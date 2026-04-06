package com.swapper.monolith.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String rawToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("SwappingDudes - Reset Your Password");
        message.setText("Click the link to reset your password (valid 15 minutes):\n\n"
                + "http://localhost:3000/reset-password?token=" + rawToken
                + "\n\nIf you didn't request this, ignore this email.");
        mailSender.send(message);
    }
}

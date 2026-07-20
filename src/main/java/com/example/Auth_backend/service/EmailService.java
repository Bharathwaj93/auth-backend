package com.example.Auth_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(toEmail);
            message.setSubject("Password Reset OTP");
            message.setText("Your OTP is: " + otp);

            mailSender.send(message);

            System.out.println("Email Sent Successfully");

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("Mail Error: " + e.getMessage());

        }
    }
}
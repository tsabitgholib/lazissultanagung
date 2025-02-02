package com.lazis.lazissultanagung.service;

public interface EmailSenderService {
    void sendRegisterReport(String toEmail, String subject, String body);

    void sendEmailResetPassword(String to, String subject, String body);
}

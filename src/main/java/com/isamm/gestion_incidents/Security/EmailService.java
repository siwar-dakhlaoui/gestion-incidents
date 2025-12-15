package com.isamm.gestion_incidents.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("your OTP code");
        message.setText("Your OTP code is: "+ otp);
        mailSender.send(message);
    }
    public void sendAccountCreationEmail(String to, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre compte a été créé");
        message.setText("Bonjour,\n\nVotre compte a été créé par un administrateur.\n"
                + "Vous pouvez vous connecter avec :\n"
                + "Email: " + to + "\n"
                + "Mot de passe temporaire: " + password + "\n\n"
                + "Nous vous recommandons de changer votre mot de passe après la première connexion.\n"
                + "Cordialement,\nL'équipe de la ville intelligente");

        mailSender.send(message);
    }
}


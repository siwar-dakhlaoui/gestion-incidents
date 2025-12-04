package com.isamm.gestion_incidents.Security;

import com.isamm.gestion_incidents.Models.User;
import com.isamm.gestion_incidents.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("http://localhost:8080/auth/verify")
    private String verificationUrl;

    public void sendVerificationEmail(User user) {
        String token = generateVerificationToken();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusDays(1));
        userRepository.save(user);

        String verificationLink = verificationUrl + "?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject("Account Verification");

            String emailContent = "<html><body>"
                    + "<h2>Welcome to our Service!</h2>"
                    + "<p> Please click the following link to verify your account</p>"
                    + "<a href=\"" + verificationLink + "\">Verify account</a>"
                    + "<p>Or copy this URL to your browser :<br>"
                    + verificationLink + "</p>"
                    + "</body></html>";
            helper.setText(emailContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);

        }
    }
    private String generateVerificationToken () {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public boolean verifyUser (String token){
        log.info("Attempting to verify token: {}", token);
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> {
                    log.error("Verification failed for token: {}", token);
                    return new RuntimeException("Invalid verification token");
                });
        if (user.getVerificationTokenExpiry() == null) {
            log.error("Verification failed :no expiry date for token: {}", token);
            throw new RuntimeException("invalid verification token");
        }


        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            log.error("Verification failed: expired token for user{}",user.getEmail());
            throw new RuntimeException("verificationlink has expired");
        }
        if(user.isVerified()){
            log.warn("user {} already verified", user.getEmail());
            throw new RuntimeException("Account is already verified");

        }
        log.info("verifing user {} ", user.getEmail());
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        log.info("successfully verified user {}", user.getEmail());
        return true;
    }
    public void resendVerificationEmail(User user){
        String verificationLink = verificationUrl + "?token=" + user.getVerificationToken();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message , true);
            helper.setTo(user.getEmail());
            helper.setSubject("AccountVerification - New Link");
            String emailContent = "<html><body>"
                    + "<h2>New Verification link</h2>"
                    + "<p> here is your new verification link</p>"
                    + "<a href=\"" + verificationLink + "\">Verify account</a>"
                    + "<p>Or copy this URL to your browser :<br>"
                    + verificationLink + "</p>"
                    +"<p> the link will expire in 24 h </p>"
                    + "</body></html>";
            helper.setText(emailContent , true);
            mailSender.send(message);

        }catch (MessagingException e){
            throw new RuntimeException("Failed to resend verification email", e);
        }
    }}

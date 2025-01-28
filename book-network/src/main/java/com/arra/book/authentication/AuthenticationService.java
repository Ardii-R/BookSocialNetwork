package com.arra.book.authentication;

import com.arra.book.email.EmailService;
import com.arra.book.email.EmailTemplateName;
import com.arra.book.role.RoleRepository;
import com.arra.book.user.Token;
import com.arra.book.user.TokenRepository;
import com.arra.book.user.User;
import com.arra.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        // default role --> user
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role not initialized"));

        // create user
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                // default values for locked and enabled
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        // save the user
        userRepository.save(user);

        // send a validation email to user ro enable account
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var token = generateAndSaveActivationToken(user);

        // send email with token
        emailService.sendVerificationEmail(user.getEmail(), user.getFullName(), EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl, token, "Account activation");

    }

    private String generateAndSaveActivationToken(User user) {
        // generate a unique token
        String genToken = generateActivationToken(6);
        var token = Token.builder().token(genToken).createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusMinutes(10)).user(user).build();
        // save token to the database
        tokenRepository.save(token);
        // return the token
        return genToken;
    }

    private String generateActivationToken(int tokenLength) {

        String charactes ="0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom randomGenerator = new SecureRandom();

        for(int i = 0; i < tokenLength; i++) {
            int randomIndex = randomGenerator.nextInt(charactes.length());
            codeBuilder.append(charactes.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}

/*
https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac
 */
package com.arra.book.authentication;

import com.arra.book.email.EmailService;
import com.arra.book.email.EmailTemplateName;
import com.arra.book.role.RoleRepository;
import com.arra.book.security.JwtService;
import com.arra.book.user.Token;
import com.arra.book.user.TokenRepository;
import com.arra.book.user.User;
import com.arra.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    // services
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    // bean configuration
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;                           // endpoint for account activation

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
        // generate the token to activate account before sending email
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
        // numbers for the token with specific length. example: 789341
        String charactes ="0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom randomGenerator = new SecureRandom();

        for(int i = 0; i < tokenLength; i++) {
            int randomIndex = randomGenerator.nextInt(charactes.length());
            codeBuilder.append(charactes.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // authenticate the user
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Map<String, Object> claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());
        var jwtToken = jwtService.generateToken(claims, user);


        return AuthenticationResponse.builder().token(jwtToken).build();
    }


    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Couldn't find token'"));

        // check if token is expired
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            // if token is expired send new email
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("The token is expired. New email with new token will be sent");
        }

        // get the user and activate account
        var user = savedToken.getUser();
        //var user = userRepository.findById(savedToken.getUser().getUserId()).orElseThrow(() -> new RuntimeException("Couldn't find user'"));
        user.setEnabled(true);
        userRepository.save(user);

        // set the validation time of the token
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);

    }
}


/*
https://medium.com/@tericcabrel/implement-jwt-authentication-in-a-spring-boot-3-application-5839e4fd8fac
 */
package com.arra.book.authentication;


import com.arra.book.security.JwtService;
import com.arra.book.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")                 // application.yml --> context-path: /api/v1/
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)    //202 Accepted
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request ) throws MessagingException {     // registration data is in the request body (JSON)
        // saves user in the database and sends a confirmation email with the generated token
        authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }


    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)      // 200 ok
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        // authenticates the user, by the given email and password inside the request body
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }


    @GetMapping("/activate-account")
    public void confirmAccount(@RequestParam String token) throws MessagingException {
        // activates the user's account by the given token
        authenticationService.activateAccount(token);
    }
}

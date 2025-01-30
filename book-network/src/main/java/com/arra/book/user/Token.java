package com.arra.book.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String token;               // the actual token (value of the token)
    private LocalDateTime createdAt;    // the time when the token was created
    private LocalDateTime expiresAt;    // the time when the token expires
    private LocalDateTime validatedAt;  // the time when the token was validated

    @ManyToOne                          // user can have multiple tokens - one token always belongs to one user
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}

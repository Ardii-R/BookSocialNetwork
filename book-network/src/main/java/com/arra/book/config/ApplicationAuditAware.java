package com.arra.book.config;

import com.arra.book.user.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


public class ApplicationAuditAware implements AuditorAware<Integer>{            // id of the user

    /*
     * Implement Spring Data JPA Auditing mechanisms to track changes in entities.
     * JpaAuditing only works for the fields @createdDate and @LastModifiedDate
     * for the @cratedBy and @LastModifiedBy fields the JpaAuditing will not work
     */

    @Override
    public Optional<Integer> getCurrentAuditor() {
        // get authentication obj
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // check if user is authenticated
        if(authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        // get user from authentication
        User userPrincipal = (User) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getUserId());
    }
}

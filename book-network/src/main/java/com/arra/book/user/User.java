package com.arra.book.user;

import com.arra.book.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {
/*
 *
 * The User entity implements UserDetails interface to provide  necessary user information for the security framework.
 * It includes details such as username, password, and granted authorities, along with account status information.
 */



    @Id
    @GeneratedValue
    private Integer uderId;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private LocalDate dateOfBirth;
    private String password;
    private boolean accountLocked;
    private boolean enabled;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate crateDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate lastModifiedDate;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    private String getFullName(){
        return this.firstname + " " + this.lastname;
    }

    @Override
    public String getName() {
        return this.email;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {            // User can have multiple roles
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        this.roles.forEach(role ->
                authorities.add(new SimpleGrantedAuthority(role.getName()))
        );

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {          // An expired account cannot be authenticated.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {           // A locked user cannot be authenticated.
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {          //  Expired credentials prevent authentication.
        return true;
    }

    @Override
    public boolean isEnabled() {            //  A disabled user cannot be authenticated.
        return this.enabled;
    }
}

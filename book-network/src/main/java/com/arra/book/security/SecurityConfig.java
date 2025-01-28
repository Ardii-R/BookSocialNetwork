package com.arra.book.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity          //enable web security
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final CustomJwtFilter customJwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*
        https://stackoverflow.com/questions/72499313/spring-boot-security-how-to-use-securityfilterchain-for-authentification
        implementation info of Spring security filter
         */
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)          // Cross Site Request Forgery --> not needed for this project
                .authorizeHttpRequests( authorizedRequests ->
                        authorizedRequests.requestMatchers(
                                // public endpoint no need for authentication
                                        "/auth/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html"
                        ).permitAll()
                                // any other endpoints need authorization
                                .anyRequest()
                                .authenticated()
                )
                // Each request must be authenticated because no session information is stored
                .sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // AuthenticationProvider processes an Authentication request, and a fully authenticated object with full credentials is returned.
                .authenticationProvider(authenticationProvider)

                // add custom JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(customJwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}

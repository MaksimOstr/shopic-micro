package com.authservice.config.security;

import com.authservice.config.security.filter.AuthenticationFilter;
import com.authservice.config.security.handler.CustomAuthenticationEntryPoint;
import com.authservice.config.security.handler.OAuthSuccessHandler;
import com.authservice.config.security.handler.OauthFailureHandler;
import com.authservice.config.security.service.CustomOidcUserService;
import com.authservice.config.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider daoAuthenticationProvider,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            AuthenticationFilter authenticationFilter,
            CustomOidcUserService customOidcUserService,
            OAuthSuccessHandler oAuthSuccessHandler,
            OauthFailureHandler oauthFailureHandler
    ) throws Exception {
        return http

                .authenticationProvider(daoAuthenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/verify/**").permitAll()
                        .requestMatchers("/auth/register", "/auth/refresh", "/auth/sign-in").permitAll()
                        .requestMatchers("/public-keys/**").permitAll()
                        .requestMatchers("/password/forgot-password", "/password/change").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService))
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/auth/oauth2/authorization"))
                        .redirectionEndpoint(redirect -> redirect
                                .baseUri("/auth/login/oauth2/code/*"))
                        .successHandler(oAuthSuccessHandler)
                        .failureHandler(oauthFailureHandler)
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(handler -> handler.authenticationEntryPoint(customAuthenticationEntryPoint))
                .build();
    }
}
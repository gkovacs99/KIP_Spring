
package com.SFM.KIP.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Szerepkör-hierarchia
    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("DIRECTOR").implies("SENIOR")
                .role("SENIOR").implies("DOCTOR")
                .role("DOCTOR").implies("NURSE")
                .role("NURSE").implies("USER")
                .build();
    }

    // Jelszó titkosítás
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/error",
                                "/css/**",
                                "/script/**",
                                "/images/**",
                                "/h2-console/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("DIRECTOR")
                        .requestMatchers("/schedule/**", "/rooms/**").hasRole("SENIOR")
                        .requestMatchers("/consultation-days/**").hasRole("DOCTOR")
                        .requestMatchers("/duties/**").hasRole("NURSE")
                        .anyRequest().hasRole("USER")
                )

                // H2 Console CSRF kivétel
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )

                // H2 Console iframe támogatás
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )

                // Bejelentkezés
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                // Kijelentkezés
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}

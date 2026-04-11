package com.yogi.testwebsite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Read admin credentials from environment variables
    // so they are never hardcoded in source code.
    // Set ADMIN_USERNAME and ADMIN_PASSWORD in your
    // Render dashboard (and locally in application.properties).
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Everything under /admin requires the ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // All other routes are public
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                // Use our custom login page
                .loginPage("/admin/login")
                // Spring Security processes POST to this URL
                .loginProcessingUrl("/admin/login")
                // On success go to the dashboard
                .defaultSuccessUrl("/admin", true)
                // On failure stay on login page with ?error
                .failureUrl("/admin/login?error")
                // The login page itself is public
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
                .permitAll()
            )
            // Allow H2 console in dev (frames)
            .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()))
            // Disable CSRF only for H2 console path in dev
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var admin = User.builder()
                .username(adminUsername)
                .password(encoder.encode(adminPassword))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

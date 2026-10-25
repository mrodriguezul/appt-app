package com.mrodriguezul.apptapp.config;

import com.mrodriguezul.apptapp.config.security.RestAccessDeniedHandler;
import com.mrodriguezul.apptapp.config.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(JwtFilter jwtFilter, RestAuthenticationEntryPoint restAuthenticationEntryPoint, RestAccessDeniedHandler restAccessDeniedHandler) {
        this.jwtFilter = jwtFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        //public access
                        .requestMatchers(HttpMethod.GET, "/doctor").permitAll()
                        .requestMatchers(HttpMethod.GET, "/doctor/specialty/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/doctor/search/name").permitAll()
                        //private access for ASSISTANT and ADMIN
                        .requestMatchers(HttpMethod.GET,
                                "/doctor/identification/*",
                                "/doctor/identification/*/*",
                                "/doctor/*").hasAnyRole("ASSISTANT", "ADMIN")
                        //admin access
                        .requestMatchers(HttpMethod.POST, "/doctor").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/doctor/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/doctor/*").hasRole("ADMIN")
                        //private access for ASSISTANT and ADMIN
                        .requestMatchers(HttpMethod.GET, "/patient", "/patient/search",
                                "/patient/by-identification/*/*").hasAnyRole("ASSISTANT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/patient").hasAnyRole("ASSISTANT", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/patient/*").hasAnyRole("ASSISTANT", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/appointment", "/appointment/*")
                        .hasAnyRole("ASSISTANT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/appointment").hasAnyRole("ASSISTANT", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/appointment/*").hasAnyRole("ASSISTANT", "ADMIN")
                        .anyRequest().denyAll()
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

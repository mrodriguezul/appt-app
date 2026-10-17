package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.config.JwtConfig;
import com.mrodriguezul.apptapp.domain.model.AuthenticationResult;
import com.mrodriguezul.apptapp.domain.model.User;
import com.mrodriguezul.apptapp.persistence.adapter.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserSecurityService implements UserDetailsService {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    // Authorities
    private static final String AUTHORITY_DOCTOR = "VIEW_DOCTORS_ALL";
    private static final String AUTHORITY_VIEW_SPECIALTIES = "VIEW_DOCTORS_BY_SPECIALTIES";

    // ROL -> AUTHORITIES mapping
    private static final Map<String, List<String>> ROLE_AUTHORITIES_MAP = Map.of(
        "ADMIN", List.of(AUTHORITY_DOCTOR, AUTHORITY_VIEW_SPECIALTIES),
        "ASSISTANT", List.of(AUTHORITY_DOCTOR, AUTHORITY_VIEW_SPECIALTIES)
    );

    public UserSecurityService(@Lazy AuthenticationManager authenticationManager, JwtConfig jwtConfig, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String[] roles = Optional.ofNullable(user.getRoles())
                .orElse(Collections.emptyList())
                .stream()
                .map(rol -> rol.getName().toUpperCase())
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(this.getGrantedAuthorities(roles))
                .accountLocked(Boolean.TRUE.equals(user.getLocked()))
                .disabled(Boolean.TRUE.equals(user.getDisabled()))
                .build();
    }

    private String[] getAuthorities(String role) {
        List<String> authorities = ROLE_AUTHORITIES_MAP.getOrDefault(role, Collections.emptyList());
        return authorities.toArray(new String[0]);
    }

    private List<GrantedAuthority> getGrantedAuthorities(String[] roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String role: roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

            for (String authority: this.getAuthorities(role)) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        return authorities;
    }

    public AuthenticationResult loginUser(String username, String password) {
        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(login);
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        String token = jwtConfig.createToken(authenticatedUser.getUsername());
        return new AuthenticationResult(token, jwtConfig.getExpirationSeconds());
    }
}

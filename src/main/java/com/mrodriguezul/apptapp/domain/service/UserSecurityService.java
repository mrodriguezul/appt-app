package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.config.JwtConfig;
import com.mrodriguezul.apptapp.domain.model.User;
import com.mrodriguezul.apptapp.persistence.adapter.UserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class UserSecurityService implements UserDetailsService {

    @Autowired
    private ObjectProvider<AuthenticationManager> authenticationManagerProvider;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    // Authorities
    private static final String AUTHORITY_DOCTOR = "VIEW_DOCTORS_ALL";
    private static final String AUTHORITY_VIEW_SPECIALTIES = "VIEW_DOCTORS_BY_SPECIALTIES";

    // ROL -> AUTHORITIES mapping
    private static final Map<String, List<String>> ROLE_AUTHORITIES_MAP = Map.of(
        "ADMIN", List.of(AUTHORITY_DOCTOR, AUTHORITY_VIEW_SPECIALTIES),
        "CUSTOMER", Arrays.asList(AUTHORITY_DOCTOR, AUTHORITY_VIEW_SPECIALTIES)
    );

    @Autowired
    public UserSecurityService(JwtConfig jwtConfig, UserRepository userRepository) {
        this.jwtConfig = jwtConfig;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        String[] roles = user.getRoles() != null && !user.getRoles().isEmpty() ?
                user.getRoles().stream()
                        .map(rol -> rol.getName())
                        .toArray(String[]::new) :
                new String[]{"NO-ROL"};

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                //.roles(roles)
                .authorities(this.getGrantedAuthorities(roles))
                .accountLocked(user.getLocked())
                .disabled(user.getDisabled())
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

    public String loginUser(User user) {
        AuthenticationManager authenticationManager = authenticationManagerProvider.getIfAvailable();
        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(login);
        //System.out.println(authentication.isAuthenticated());
        //System.out.println(authentication.getPrincipal());
        String token = jwtConfig.createToken(user.getUsername());
        System.out.println("Generated Token: " + token);
        return token;
    }
}

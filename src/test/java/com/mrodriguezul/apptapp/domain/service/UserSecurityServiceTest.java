package com.mrodriguezul.apptapp.domain.service;

import com.mrodriguezul.apptapp.config.JwtConfig;
import com.mrodriguezul.apptapp.config.JwtProperties;
import com.mrodriguezul.apptapp.domain.model.Role;
import com.mrodriguezul.apptapp.domain.model.User;
import com.mrodriguezul.apptapp.persistence.adapter.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSecurityServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Test
    void loadUserByUsername_shouldLoadRolesAndMappedAuthorities() {
        User user = user("alice", List.of(new Role("admin", null), new Role("assistant", null)));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDetails result = service().loadUserByUsername("alice");

        assertEquals("alice", result.getUsername());
        assertEquals("encoded-password", result.getPassword());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ASSISTANT")));
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("VIEW_DOCTORS_ALL")));
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("VIEW_DOCTORS_BY_SPECIALTIES")));
        verify(userRepository).findByUsername("alice");
    }

    @Test
    void loadUserByUsername_shouldPreserveAccountFlagsAndHandleNoRoles() {
        User user = user("alice", null);
        user.setLocked(true);
        user.setDisabled(true);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        UserDetails result = service().loadUserByUsername("alice");

        assertFalse(result.isAccountNonLocked());
        assertFalse(result.isEnabled());
        assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_shouldRejectUnknownUser() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service().loadUserByUsername("missing"));
    }

    private UserSecurityService service() {
        return new UserSecurityService(authenticationManager,
                new JwtConfig(new JwtProperties("01234567890123456789012345678901", "issuer", 60)),
                userRepository);
    }

    private User user(String username, List<Role> roles) {
        User user = new User(username);
        user.setPassword("encoded-password");
        user.setRoles(roles);
        return user;
    }
}

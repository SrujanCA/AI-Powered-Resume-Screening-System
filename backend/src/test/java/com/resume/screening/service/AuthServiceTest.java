package com.resume.screening.service;

import com.resume.screening.dto.AuthResponse;
import com.resume.screening.dto.LoginRequest;
import com.resume.screening.dto.RegisterRequest;
import com.resume.screening.entity.Role;
import com.resume.screening.entity.User;
import com.resume.screening.exception.EmailAlreadyExistsException;
import com.resume.screening.repository.UserRepository;
import com.resume.screening.security.JwtTokenProvider;
import com.resume.screening.security.UserPrincipal;
import com.resume.screening.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .fullName("Jane Recruiter")
                .email("jane@example.com")
                .password("encoded_password")
                .role(Role.ROLE_RECRUITER)
                .build();

        registerRequest = RegisterRequest.builder()
                .fullName("Jane Recruiter")
                .email("jane@example.com")
                .password("Password123!")
                .role(Role.ROLE_RECRUITER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("jane@example.com")
                .password("Password123!")
                .build();
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenRequestIsValid() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateToken(any())).thenReturn("mocked_jwt_token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jane@example.com", response.getEmail());
        assertEquals("mocked_jwt_token", response.getToken());
        assertEquals(Role.ROLE_RECRUITER, response.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        UserPrincipal userPrincipal = UserPrincipal.create(sampleUser);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(tokenProvider.generateToken(authentication)).thenReturn("mocked_jwt_token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jane@example.com", response.getEmail());
        assertEquals("mocked_jwt_token", response.getToken());
    }
}

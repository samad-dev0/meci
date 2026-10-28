package meci_e_services.auth;

import meci_e_services.entity.AppUsers;
import meci_e_services.repository.AppUsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUsersRepository utilisateursRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginMatchesPasswordUsingTheSameTrimmingAsUserCreation() {
        AppUsers user = new AppUsers();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setNom("Test");
        user.setPrenom("User");
        user.setActif(true);
        user.setPassword("encoded-password");

        LoginRequest request = new LoginRequest();
        request.setEmail(" TEST@example.com ");
        request.setPassword(" Secret123 ");

        when(utilisateursRepository.findByEmailIgnoreCase("TEST@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Secret123", "encoded-password")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
    }
}

package meci_e_services.service;

import meci_e_services.entity.AppUsers;
import meci_e_services.repository.AppUsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUsersServiceTest {

    @Mock
    private AppUsersRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccessControlService accessControlService;

    @InjectMocks
    private AppUsersService service;

    @Test
    void createUserEncodesThePasswordProvidedByTheAdmin() {
        AppUsers user = new AppUsers();
        user.setEmail(" test@example.com ");
        user.setMotDePasse("  Secret123  ");
        when(repository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123")).thenReturn("encoded-password");
        when(repository.save(user)).thenReturn(user);

        AppUsers savedUser = service.createUser(user);

        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        verify(passwordEncoder).encode("Secret123");
    }

    @Test
    void createUserRejectsMissingPasswordInsteadOfUsingAnUnannouncedDefault() {
        AppUsers user = new AppUsers();
        user.setEmail("test@example.com");
        when(repository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.createUser(user));

        verify(repository, never()).save(user);
        verifyNoInteractions(passwordEncoder);
    }
}

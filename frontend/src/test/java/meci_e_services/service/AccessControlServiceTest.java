package meci_e_services.service;

import meci_e_services.entity.AppUsers;
import meci_e_services.repository.AppUsersRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessControlServiceTest {

    private final AppUsersRepository users = mock(AppUsersRepository.class);
    private final AccessControlService service = new AccessControlService(users);

    @Test
    void selectedPageCanBeViewedWithoutGrantingItsActions() {
        AppUsers user = activeUser(Set.of("pages.reports.list"));
        when(users.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));

        assertTrue(service.hasAccess("user@example.com", "pages.reports.list"));
        assertFalse(service.hasAccess("user@example.com", "pages.reports.list.delete"));
    }

    @Test
    void selectedActionAllowsItsPageButNotSiblingActions() {
        AppUsers user = activeUser(Set.of("pages.reports.list.download"));
        when(users.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));

        assertTrue(service.hasAccess("user@example.com", "pages.reports.list"));
        assertTrue(service.hasAccess("user@example.com", "pages.reports.list.download"));
        assertFalse(service.hasAccess("user@example.com", "pages.reports.list.delete"));
    }

    @Test
    void unknownRightsCannotBeAssigned() {
        AppUsers user = activeUser(Set.of());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.replaceAccessRights(user, Set.of("unknown.right"))
        );
    }

    private AppUsers activeUser(Set<String> rights) {
        AppUsers user = new AppUsers();
        user.setActif(true);
        user.setSuperAdmin(false);
        user.setAccessRightCodes(rights);
        return user;
    }
}

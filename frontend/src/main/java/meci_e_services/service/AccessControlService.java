package meci_e_services.service;

import meci_e_services.dto.AccessRightNodeDTO;
import meci_e_services.entity.AppUsers;
import meci_e_services.repository.AppUsersRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AccessControlService {

    private final AppUsersRepository users;

    public AccessControlService(AppUsersRepository users) {
        this.users = users;
    }

    public List<AccessRightNodeDTO> getCatalog() {
        return AccessRightCatalog.getTree();
    }

    public Set<String> getAccessRights(String email) {
        AppUsers user = findActiveUser(email);
        if (Boolean.TRUE.equals(user.getSuperAdmin())) {
            return Set.copyOf(AccessRightCatalog.getAllCodes());
        }
        return Set.copyOf(user.getAccessRightCodes());
    }

    public boolean isSuperAdmin(String email) {
        return Boolean.TRUE.equals(findActiveUser(email).getSuperAdmin());
    }

    public boolean hasDirectAccess(AppUsers user, String code) {
        return user.getAccessRightCodes().contains(code);
    }

    public boolean hasAccess(String email, String requiredCode) {
        AppUsers user = findActiveUser(email);
        if (Boolean.TRUE.equals(user.getSuperAdmin())) {
            return true;
        }

        Set<String> assigned = user.getAccessRightCodes();
        if (assigned.contains(requiredCode)) {
            return true;
        }

        return isPageCode(requiredCode)
                && AccessRightCatalog.descendantsIncluding(requiredCode).stream()
                .anyMatch(assigned::contains);
    }

    @Transactional
    public void replaceAccessRights(AppUsers user, Collection<String> requestedCodes) {
        Set<String> expanded = new HashSet<>();
        if (requestedCodes != null) {
            for (String code : requestedCodes) {
                if (code == null || !AccessRightCatalog.contains(code)) {
                    throw new IllegalArgumentException("Un droit sélectionné est inconnu.");
                }
                expanded.add(code);
            }
        }
        user.setAccessRightCodes(expanded);
        user.setAccessRightsConfigured(true);
    }

    @Transactional
    public void setAccessRight(AppUsers user, String code, boolean granted) {
        if (!AccessRightCatalog.contains(code)) {
            throw new IllegalArgumentException("Un droit sélectionné est inconnu.");
        }
        Set<String> rights = new HashSet<>(user.getAccessRightCodes());
        if (granted) {
            rights.add(code);
        } else {
            rights.remove(code);
        }
        user.setAccessRightCodes(rights);
        user.setAccessRightsConfigured(true);
    }

    @Transactional
    public void initializeLegacyUsers() {
        List<String> allCodes = AccessRightCatalog.getAllCodes();
        for (AppUsers user : users.findAll()) {
            if (!Boolean.TRUE.equals(user.getAccessRightsConfigured())) {
                if (!Boolean.TRUE.equals(user.getSuperAdmin())) {
                    Set<String> legacyRights = new HashSet<>(allCodes);
                    legacyRights.remove("pages.administration.users.create");
                    legacyRights.remove("pages.administration.users.edit");
                    legacyRights.remove("pages.administration.users.delete");
                    legacyRights.remove("pages.administration.report-access.manage");
                    if (Boolean.TRUE.equals(user.getGestionnairePartage())) {
                        legacyRights.add("pages.administration.report-access.manage");
                    }
                    user.setAccessRightCodes(legacyRights);
                }
                user.setAccessRightsConfigured(true);
            }
        }
        users.flush();
    }

    private AppUsers findActiveUser(String email) {
        if (email == null) {
            throw new AccessDeniedException("Utilisateur non authentifié.");
        }
        return users.findByEmailIgnoreCase(email)
                .filter(user -> Boolean.TRUE.equals(user.getActif()))
                .orElseThrow(() -> new AccessDeniedException("Utilisateur inactif ou introuvable."));
    }

    private boolean isPageCode(String code) {
        return findPage(AccessRightCatalog.getTree(), code);
    }

    private boolean findPage(List<AccessRightNodeDTO> nodes, String code) {
        return nodes.stream().anyMatch(node ->
                ("PAGE".equals(node.type()) && node.code().equals(code))
                        || findPage(node.children(), code));
    }
}

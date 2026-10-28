package meci_e_services.controller;

import meci_e_services.entity.AppUsers;
import meci_e_services.service.AppUsersService;
import meci_e_services.service.AccessControlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class AppUsersController {

    private final AppUsersService service;
    private final AccessControlService accessControlService;

    public AppUsersController(
            AppUsersService service,
            AccessControlService accessControlService
    ) {
        this.service = service;
        this.accessControlService = accessControlService;
    }

    @GetMapping
    public ResponseEntity<List<AppUsers>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUsers> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @GetMapping("/{id}/access-rights")
    public ResponseEntity<java.util.Set<String>> getAccessRights(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserById(id).getAccessRightCodes());
    }

    @PostMapping
    public ResponseEntity<AppUsers> createUser(@RequestBody AppUsers user) {
        return ResponseEntity.ok(service.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUsers> updateUser(
            @PathVariable Long id,
            @RequestBody AppUsers user) {
        return ResponseEntity.ok(service.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
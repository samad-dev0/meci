package meci_e_services.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping({"/", "/api", "/api/"})
    public ResponseEntity<Map<String, Object>> getApiStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Bienvenue sur l'API e-service-meci");
        response.put("endpoints", List.of(
                "/api/test",
                "/api/rapports",
                "/api/utilisateurs",
                "/api/admin"
        ));
        return ResponseEntity.ok(response);
    }
}

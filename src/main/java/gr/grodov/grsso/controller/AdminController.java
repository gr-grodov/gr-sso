package gr.grodov.grsso.controller;

import gr.grodov.grsso.domain.dto.OAuthClientDto;
import gr.grodov.grsso.service.OAuthClientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OAuthClientsService oAuthClientsService;

    @GetMapping("/list-clients")
    public ResponseEntity<List<OAuthClientDto>> listClients(Principal user) {
        return ResponseEntity.ok(oAuthClientsService.list());
    }

    @PostMapping("/create-client")
    public ResponseEntity<String> createClient(@RequestBody OAuthClientDto client) {
        oAuthClientsService.save(client);
        return ResponseEntity.ok("");
    }
}
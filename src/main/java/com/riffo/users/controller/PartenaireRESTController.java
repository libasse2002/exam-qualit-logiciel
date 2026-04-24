package com.riffo.users.controller;

import com.riffo.users.entity.Partenaire;
import com.riffo.users.exception.PartenaireNotFoundException;
import com.riffo.users.service.PartenaireService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des partenaires.
 *
 * Corrections :
 * - @Valid ajouté sur @RequestBody
 * - Injection par constructeur
 * - Gestion des erreurs déléguée au GlobalExceptionHandler
 */
@RestController
@RequestMapping("/partenaires")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PartenaireRESTController {

    private final PartenaireService partenaireService;

    /** Injection par constructeur (correction). */
    public PartenaireRESTController(PartenaireService partenaireService) {
        this.partenaireService = partenaireService;
    }

    /** GET /api/partenaires - Liste tous les partenaires. */
    @GetMapping
    public ResponseEntity<List<Partenaire>> getAllPartenaires() {
        return ResponseEntity.ok(partenaireService.getAllPartenaires());
    }

    /**
     * GET /api/partenaires/{id} - Retourne un partenaire par son ID (404 si
     * absent).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Partenaire> getPartenaireById(@PathVariable Long id) {
        Partenaire p = partenaireService.getPartenaireById(id)
                .orElseThrow(() -> new PartenaireNotFoundException(id));
        return ResponseEntity.ok(p);
    }

    /** GET /api/partenaires/search/nom?nom={nom} */
    @GetMapping("/search/nom")
    public ResponseEntity<Partenaire> getPartenaireByNom(@RequestParam String nom) {
        Optional<Partenaire> partenaire = partenaireService.getPartenaireByNom(nom);
        return partenaire.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/partenaires/search/categorie?categorie={categorie} */
    @GetMapping("/search/categorie")
    public ResponseEntity<List<Partenaire>> getPartenairesByCategorie(@RequestParam String categorie) {
        return ResponseEntity.ok(partenaireService.getPartenairesByCategorie(categorie));
    }

    /** GET /api/partenaires/search/statut?statut={statut} */
    @GetMapping("/search/statut")
    public ResponseEntity<List<Partenaire>> getPartenairesByStatut(@RequestParam String statut) {
        return ResponseEntity.ok(partenaireService.getPartenairesByStatut(statut));
    }

    /** GET /api/partenaires/search/ville?ville={ville} */
    @GetMapping("/search/ville")
    public ResponseEntity<List<Partenaire>> getPartenairesByVille(@RequestParam String ville) {
        return ResponseEntity.ok(partenaireService.getPartenairesByVille(ville));
    }

    /** GET /api/partenaires/search/email?email={email} */
    @GetMapping("/search/email")
    public ResponseEntity<Partenaire> getPartenaireByEmail(@RequestParam String email) {
        Optional<Partenaire> partenaire = partenaireService.getPartenaireByEmail(email);
        return partenaire.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /partenaires - Crée un partenaire.
     * 
     * @Valid déclenche la validation avant d'appeler le service.
     */
    @PostMapping
    public ResponseEntity<Partenaire> addPartenaire(@Valid @RequestBody Partenaire partenaire) {
        Partenaire nouveau = partenaireService.addPartenaire(partenaire);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveau);
    }

    /**
     * PUT /api/partenaires/{id} - Met à jour un partenaire existant.
     * correct : @Valid + PartenaireNotFoundException gérée par
     * GlobalExceptionHandler.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Partenaire> updatePartenaire(
            @PathVariable Long id,
            @Valid @RequestBody Partenaire partenaire) {
        return ResponseEntity.ok(partenaireService.updatePartenaire(id, partenaire));
    }

    /**
     * DELETE /api/partenaires/{id} - Supprime un partenaire.
     * correct : plus de try/catch local, le GlobalExceptionHandler gère le 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartenaire(@PathVariable Long id) {
        partenaireService.deletePartenaire(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/partenaires/count - Nombre total de partenaires. */
    @GetMapping("/count")
    public ResponseEntity<Long> countPartenaires() {
        return ResponseEntity.ok(partenaireService.countPartenaires());
    }

    /** GET /api/partenaires/exists/email?email={email} */
    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(partenaireService.existsByEmail(email));
    }
}

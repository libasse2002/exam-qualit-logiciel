package com.riffo.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riffo.users.entity.Partenaire;
import com.riffo.users.repository.PartenaireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * tests d'intégration des endpoints REST avec MockMvc.
 * utilise H2 en mémoire (profil test) - pas besoin de MySQL.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PartenaireControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PartenaireRepository partenaireRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Partenaire partenaireSample;

    @BeforeEach
    void setUp() {
        partenaireRepository.deleteAll();
        partenaireSample = new Partenaire(
                "Clinique Limamou",
                "SANTE",
                "Medina Rue 15 X 12",
                "Dakar",
                "+221338234567",
                "limamou@clinique.sn",
                14.6892, -17.4388,
                "ACTIF",
                3000000.0);
    }

    // POST /api/partenaires

    @Test
    @DisplayName("POST /partenaires avec données valides -> 201 Created")
    void postPartenaire_shouldReturn201_whenValid() throws Exception {
        mockMvc.perform(post("/partenaires")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partenaireSample)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom", is("Clinique de la Madeleine")))
                .andExpect(jsonPath("$.email", is("madeleine@clinique.sn")))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /partenaires avec email invalide -> 400 Bad Request")
    void postPartenaire_shouldReturn400_whenEmailInvalid() throws Exception {
        partenaireSample.setEmail("email-invalide");

        mockMvc.perform(post("/partenaires")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partenaireSample)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /partenaires avec nom vide -> 400 Bad Request")
    void postPartenaire_shouldReturn400_whenNomBlank() throws Exception {
        partenaireSample.setNom("");

        mockMvc.perform(post("/partenaires")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partenaireSample)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /partenaires avec email déjà existant -> 409 Conflict")
    void postPartenaire_shouldReturn409_whenEmailDuplicate() throws Exception {
        partenaireRepository.save(partenaireSample);

        Partenaire doublon = new Partenaire("Autre Clinique", "SANTE",
                "Rue 10", "Dakar", "+221771111111",
                "limamou@clinique.sn", 14.69, -17.44, "ACTIF", 1000000.0);

        mockMvc.perform(post("/partenaires")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doublon)))
                .andExpect(status().isConflict());
    }

    // GET /api/partenaires

    @Test
    @DisplayName("GET /partenaires avec base vide -> liste vide")
    void getAllPartenaires_shouldReturnEmptyList_whenNone() throws Exception {
        mockMvc.perform(get("/partenaires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /partenaires avec 2 partenaires en base -> liste de 2")
    void getAllPartenaires_shouldReturnAll_whenMultiple() throws Exception {
        partenaireRepository.save(partenaireSample);
        Partenaire p2 = new Partenaire("Pharmacie Centrale", "PHARMACIE",
                "Avenue Bourguiba", "Dakar", "+221770000002",
                "centrale@pharma.sn", 14.68, -17.44, "ACTIF", 500000.0);
        partenaireRepository.save(p2);

        mockMvc.perform(get("/partenaires"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("GET /partenaires/{id} avec ID existant -> 200 OK")
    void getPartenaireById_shouldReturn200_whenExists() throws Exception {
        Partenaire saved = partenaireRepository.save(partenaireSample);

        mockMvc.perform(get("/partenaires/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Clinique Limamou")));
    }

    @Test
    @DisplayName("GET /partenaires/{id} avec ID inexistant -> 404 Not Found")
    void getPartenaireById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/partenaires/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erreur").exists());
    }

    // DELETE /api/partenaires/{id}

    @Test
    @DisplayName("DELETE /partenaires/{id} avec ID existant -> 204 No Content")
    void deletePartenaire_shouldReturn204_whenExists() throws Exception {
        Partenaire saved = partenaireRepository.save(partenaireSample);

        mockMvc.perform(delete("/partenaires/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /partenaires/{id} avec ID inexistant -> 404 Not Found")
    void deletePartenaire_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/partenaires/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erreur").exists());
    }
}

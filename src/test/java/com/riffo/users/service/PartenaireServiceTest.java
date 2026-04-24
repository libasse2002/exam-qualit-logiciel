package com.riffo.users.service;

import com.riffo.users.entity.Partenaire;
import com.riffo.users.exception.PartenaireNotFoundException;
import com.riffo.users.repository.PartenaireRepository;
import com.riffo.users.service.impl.PartenaireServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * tests unitaires de PartenaireServiceImpl avec Mockito.
 * le repository est mocké : aucune base de données réelle n'est sollicitée.
 */
@ExtendWith(MockitoExtension.class)
class PartenaireServiceTest {

    @Mock
    private PartenaireRepository partenaireRepository;

    @InjectMocks
    private PartenaireServiceImpl partenaireService;

    private Partenaire partenaireSample;

    @BeforeEach
    void setUp() {
        partenaireSample = new Partenaire(
                "Clinique Pasteur",
                "SANTE",
                "12 Avenue Cheikh Anta Diop",
                "Dakar",
                "+221771234567",
                "pasteur@clinique.sn",
                14.6937, -17.4441,
                "ACTIF",
                5000000.0);
        partenaireSample.setId(1L);
    }

    // Tests : création d'un partenaire

    @Test
    @DisplayName("Création d'un partenaire avec des données valides -> succès")
    void addPartenaire_shouldReturnSavedPartenaire_whenValid() {
        when(partenaireRepository.existsByEmail("pasteur@clinique.sn")).thenReturn(false);
        when(partenaireRepository.save(any(Partenaire.class))).thenReturn(partenaireSample);

        Partenaire result = partenaireService.addPartenaire(partenaireSample);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNom()).isEqualTo("Clinique Pasteur");
        verify(partenaireRepository).save(partenaireSample);
    }

    @Test
    @DisplayName("Création avec email déjà existant -> IllegalArgumentException")
    void addPartenaire_shouldThrow_whenEmailAlreadyExists() {
        when(partenaireRepository.existsByEmail("pasteur@clinique.sn")).thenReturn(true);

        assertThatThrownBy(() -> partenaireService.addPartenaire(partenaireSample))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pasteur@clinique.sn");
    }

    @Test
    @DisplayName("Création avec partenaire null -> IllegalArgumentException")
    void addPartenaire_shouldThrow_whenNull() {
        assertThatThrownBy(() -> partenaireService.addPartenaire(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // Tests : recherche par ID

    @Test
    @DisplayName("findById avec ID existant -> retourne le partenaire")
    void getPartenaireById_shouldReturnPartenaire_whenExists() {
        when(partenaireRepository.findById(1L)).thenReturn(Optional.of(partenaireSample));

        Optional<Partenaire> result = partenaireService.getPartenaireById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNom()).isEqualTo("Clinique Pasteur");
    }

    @Test
    @DisplayName("findById avec ID inexistant -> Optional vide")
    void getPartenaireById_shouldReturnEmpty_whenNotFound() {
        when(partenaireRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Partenaire> result = partenaireService.getPartenaireById(99L);

        assertThat(result).isEmpty();
    }

    // Tests : cas d'erreur (partenaire inexistant)

    @Test
    @DisplayName("deletePartenaire avec ID inexistant -> PartenaireNotFoundException")
    void deletePartenaire_shouldThrow_whenNotFound() {
        when(partenaireRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> partenaireService.deletePartenaire(99L))
                .isInstanceOf(PartenaireNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("deletePartenaire avec ID existant -> appelle deleteById")
    void deletePartenaire_shouldCallDeleteById_whenExists() {
        when(partenaireRepository.existsById(1L)).thenReturn(true);

        partenaireService.deletePartenaire(1L);

        verify(partenaireRepository).deleteById(1L);
    }

    @Test
    @DisplayName("updatePartenaire avec ID inexistant -> PartenaireNotFoundException")
    void updatePartenaire_shouldThrow_whenNotFound() {
        when(partenaireRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> partenaireService.updatePartenaire(99L, partenaireSample))
                .isInstanceOf(PartenaireNotFoundException.class)
                .hasMessageContaining("99");
    }

    // tests : récupération liste + comptage

    @Test
    @DisplayName("getAllPartenaires retourne la liste complète")
    void getAllPartenaires_shouldReturnAll() {
        Partenaire p2 = new Partenaire("Pharmacie Keur Serigne",
                "PHARMACIE", "Plateau", "Dakar", "+221760000000",
                "ks@pharma.sn", 14.69, -17.44, "ACTIF", 100000.0);
        when(partenaireRepository.findAll()).thenReturn(Arrays.asList(partenaireSample, p2));

        List<Partenaire> result = partenaireService.getAllPartenaires();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("countPartenaires retourne le bon nombre")
    void countPartenaires_shouldReturnCount() {
        when(partenaireRepository.count()).thenReturn(5L);

        long count = partenaireService.countPartenaires();

        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("existsByEmail retourne true si email présent")
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        when(partenaireRepository.existsByEmail("pasteur@clinique.sn")).thenReturn(true);

        assertThat(partenaireService.existsByEmail("pasteur@clinique.sn")).isTrue();
    }
}

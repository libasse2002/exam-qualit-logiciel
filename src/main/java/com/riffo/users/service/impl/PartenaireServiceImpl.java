package com.riffo.users.service.impl;

import com.riffo.users.entity.Partenaire;
import com.riffo.users.exception.PartenaireNotFoundException;
import com.riffo.users.repository.PartenaireRepository;
import com.riffo.users.service.PartenaireService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des partenaires.
 *
 * corrections :
 * - Injection par constructeur
 * - PartenaireNotFoundException pour les cas "non trouvé" (404 vs 400)
 * - existsByEmail utilise la méthode native du repository
 */
@Service
@Transactional
public class PartenaireServiceImpl implements PartenaireService {

    private final PartenaireRepository partenaireRepository;

    /** Injection par constructeur (CORRECTION : remplace @Autowired sur champ). */
    public PartenaireServiceImpl(PartenaireRepository partenaireRepository) {
        this.partenaireRepository = partenaireRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partenaire> getAllPartenaires() {
        return partenaireRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Partenaire> getPartenaireById(Long id) {
        return partenaireRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Partenaire> getPartenaireByNom(String nom) {
        return partenaireRepository.findByNom(nom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partenaire> getPartenairesByCategorie(String categorie) {
        return partenaireRepository.findByCategorie(categorie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partenaire> getPartenairesByStatut(String statut) {
        return partenaireRepository.findByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Partenaire> getPartenairesByVille(String ville) {
        return partenaireRepository.findByVille(ville);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Partenaire> getPartenaireByEmail(String email) {
        return partenaireRepository.findByEmail(email);
    }

    @Override
    public Partenaire addPartenaire(Partenaire partenaire) {
        if (partenaire == null) {
            throw new IllegalArgumentException("Le partenaire ne peut pas être null");
        }
        if (existsByEmail(partenaire.getEmail())) {
            throw new IllegalArgumentException(
                    "Un partenaire avec l'email '" + partenaire.getEmail() + "' existe déjà");
        }
        return partenaireRepository.save(partenaire);
    }

    @Override
    public Partenaire updatePartenaire(Long id, Partenaire partenaire) {
        if (partenaire == null) {
            throw new IllegalArgumentException("Le partenaire ne peut pas être null");
        }
        // correct : PartenaireNotFoundException (404) au lieu de
        // IllegalArgumentException (400)
        Partenaire existing = partenaireRepository.findById(id)
                .orElseThrow(() -> new PartenaireNotFoundException(id));

        if (partenaire.getNom() != null)
            existing.setNom(partenaire.getNom());
        if (partenaire.getCategorie() != null)
            existing.setCategorie(partenaire.getCategorie());
        if (partenaire.getAdresse() != null)
            existing.setAdresse(partenaire.getAdresse());
        if (partenaire.getVille() != null)
            existing.setVille(partenaire.getVille());
        if (partenaire.getTelephone() != null)
            existing.setTelephone(partenaire.getTelephone());
        if (partenaire.getEmail() != null)
            existing.setEmail(partenaire.getEmail());
        if (partenaire.getLatitude() != null)
            existing.setLatitude(partenaire.getLatitude());
        if (partenaire.getLongitude() != null)
            existing.setLongitude(partenaire.getLongitude());
        if (partenaire.getStatut() != null)
            existing.setStatut(partenaire.getStatut());
        if (partenaire.getPlafondPriseEnCharge() != null) {
            existing.setPlafondPriseEnCharge(partenaire.getPlafondPriseEnCharge());
        }
        return partenaireRepository.save(existing);
    }

    @Override
    public void deletePartenaire(Long id) {
        // correct : PartenaireNotFoundException (404) au lieu de
        // IllegalArgumentException (400)
        if (!partenaireRepository.existsById(id)) {
            throw new PartenaireNotFoundException(id);
        }
        partenaireRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPartenaires() {
        return partenaireRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        // correct : utilisation de existsByEmail du repository (requête COUNT plus
        // efficace)
        return partenaireRepository.existsByEmail(email);
    }
}

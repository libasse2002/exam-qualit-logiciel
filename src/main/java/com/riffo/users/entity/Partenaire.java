package com.riffo.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un partenaire d'assurance.
 *
 * correct :
 * - annotations de validation ajoutées (@NotBlank, @Email, @Pattern, @NotNull)
 * - champs renommés en ASCII (categorie, telephone)
 * - @Column(unique=true) sur email
 */
@Entity
@Table(name = "partenaires")
public class Partenaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom du partenaire : obligatoire, 2 à 100 caractères. */
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;

    /** Catégorie : obligatoire. correct : nom de champ ASCII (était catégorie). */
    @NotBlank(message = "La catégorie est obligatoire")
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères")
    @Column(name = "categorie", nullable = false, length = 50)
    private String categorie;

    /** Adresse complète : obligatoire. */
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    @Column(nullable = false, length = 255)
    private String adresse;

    /** Ville : obligatoire. */
    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String ville;

    /** Téléphone : format numérique international. correct : nom ASCII. */
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Le téléphone doit être un numéro valide (7 à 15 chiffres)")
    @Column(name = "telephone", length = 20)
    private String telephone;

    /** Email : obligatoire, format valide, unique en base. */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit avoir un format valide")
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    /** Latitude GPS. */
    @NotNull(message = "La latitude est obligatoire")
    @Column(nullable = false)
    private Double latitude;

    /** Longitude GPS. */
    @NotNull(message = "La longitude est obligatoire")
    @Column(nullable = false)
    private Double longitude;

    /** Statut du partenaire (ex: ACTIF, INACTIF, SUSPENDU). */
    @NotBlank(message = "Le statut est obligatoire")
    @Size(max = 20, message = "Le statut ne peut pas dépasser 20 caractères")
    @Column(nullable = false, length = 20)
    private String statut;

    /** Plafond de prise en charge en FCFA : doit être positif. */
    @NotNull(message = "Le plafond de prise en charge est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le plafond doit être un montant positif")
    @Column(nullable = false)
    private Double plafondPriseEnCharge;

    /** Constructeur vide requis par JPA. */
    public Partenaire() {
    }

    /** Constructeur complet. */
    public Partenaire(String nom, String categorie, String adresse, String ville,
            String telephone, String email, Double latitude, Double longitude,
            String statut, Double plafondPriseEnCharge) {
        this.nom = nom;
        this.categorie = categorie;
        this.adresse = adresse;
        this.ville = ville;
        this.telephone = telephone;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.statut = statut;
        this.plafondPriseEnCharge = plafondPriseEnCharge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Double getPlafondPriseEnCharge() {
        return plafondPriseEnCharge;
    }

    public void setPlafondPriseEnCharge(Double plafondPriseEnCharge) {
        this.plafondPriseEnCharge = plafondPriseEnCharge;
    }

    @Override
    public String toString() {
        return "Partenaire{id=" + id + ", nom='" + nom + "', email='" + email + "'}";
    }
}

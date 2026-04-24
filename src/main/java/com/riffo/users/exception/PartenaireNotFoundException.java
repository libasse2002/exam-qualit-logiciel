package com.riffo.users.exception;

/**
 * Exception métier levée lorsqu'un partenaire est introuvable.
 *
 * correct : remplacement de IllegalArgumentException générique par
 * une exception dédiée, plus lisible et facilement catchable dans le handler
 * global.
 */
public class PartenaireNotFoundException extends RuntimeException {

    /**
     * @param id identifiant du partenaire introuvable
     */
    public PartenaireNotFoundException(Long id) {
        super("Partenaire introuvable avec l'identifiant : " + id);
    }
}

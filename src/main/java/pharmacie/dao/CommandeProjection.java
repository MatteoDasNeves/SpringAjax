package pharmacie.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * On définit une projection pour transmettre des informations sur une commande
 * et ses lignes de commande.
 */
public interface CommandeProjection {
    // Pour les commandes on ne garde que le numéro et la date de saisie
    Integer getNumero();

    LocalDate getSaisiele();

    // Pour le dispensaire on ne garde que le nom et le nom du contact
    interface DispensaireProjection {
        String getNom();

        String getContact();
    }

    DispensaireProjection getDispensaire();

    // Pour les lignes on ne garde que la quantité et le produit
    interface LigneProjection {
        Integer getQuantite();

        MedicamentProjection getMedicament();
    }

    List<LigneProjection> getLignes();

    // Pour les médicaments on ne garde que le nom et le prix unitaire
    interface MedicamentProjection {
        String getNom();

        BigDecimal getPrixUnitaire();
    }
}

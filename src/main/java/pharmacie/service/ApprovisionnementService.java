package pharmacie.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Medicament;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApprovisionnementService {

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Identifie les médicaments à réapprovisionner et envoie un mail à chaque fournisseur concerné.
     * Les mails sont regroupés par fournisseur et les médicaments par catégorie.
     * @return La liste des adresses emails des fournisseurs contactés.
     */
    @Transactional(readOnly = true)
    public List<String> processApprovisionnement() {
        // 1. Identifier les médicaments sous le seuil de réapprovisionnement
        List<Medicament> aReapprovisionner = medicamentRepository.findAll().stream()
                .filter(m -> m.getUnitesEnStock() < m.getNiveauDeReappro())
                .collect(Collectors.toList());

        if (aReapprovisionner.isEmpty()) {
            log.info("Aucun médicament ne nécessite de réapprovisionnement.");
            return Collections.emptyList();
        }

        // 2. Regrouper par fournisseur, puis par catégorie
        // Map<Fournisseur, Map<Categorie, List<Medicament>>>
        Map<Fournisseur, Map<Categorie, List<Medicament>>> regroupement = new HashMap<>();

        for (Medicament m : aReapprovisionner) {
            Categorie cat = m.getCategorie();
            if (cat != null) {
                for (Fournisseur f : cat.getFournisseurs()) {
                    regroupement
                        .computeIfAbsent(f, k -> new HashMap<>())
                        .computeIfAbsent(cat, k -> new ArrayList<>())
                        .add(m);
                }
            }
        }

        // 3. Envoyer les emails
        List<String> fournisseursContactes = new ArrayList<>();
        for (Map.Entry<Fournisseur, Map<Categorie, List<Medicament>>> entry : regroupement.entrySet()) {
            Fournisseur f = entry.getKey();
            Map<Categorie, List<Medicament>> parCategorie = entry.getValue();

            String body = genererCorpsEmail(f, parCategorie);
            
            try {
                envoyerEmail(f.getEmail(), "Demande de devis de réapprovisionnement", body);
                fournisseursContactes.add(f.getEmail());
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'email au fournisseur {} ({})", f.getNom(), f.getEmail(), e);
                // On peut décider de continuer pour les autres ou d'arrêter.
                // Ici on choisit de lever une exception si au moins un envoi échoue pour signaler le problème.
                throw new RuntimeException("Échec de l'envoi de l'email à " + f.getEmail() + " : " + e.getMessage(), e);
            }
        }

        return fournisseursContactes;
    }

    private String genererCorpsEmail(Fournisseur f, Map<Categorie, List<Medicament>> parCategorie) {
        StringBuilder body = new StringBuilder();
        body.append("Bonjour ").append(f.getNom()).append(",\n\n");
        body.append("Merci de nous établir un devis pour les médicaments suivants, classés par catégorie :\n\n");

        for (Map.Entry<Categorie, List<Medicament>> catEntry : parCategorie.entrySet()) {
            body.append("Catégorie : ").append(catEntry.getKey().getLibelle()).append("\n");
            for (Medicament m : catEntry.getValue()) {
                body.append("  - ").append(m.getNom())
                    .append(" (Stock actuel : ").append(m.getUnitesEnStock())
                    .append(", Niveau de réappro : ").append(m.getNiveauDeReappro()).append(")\n");
            }
            body.append("\n");
        }

        body.append("Cordialement,\nLa Pharmacie de l'Isis.");
        return body.toString();
    }

    private void envoyerEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
        log.info("Email envoyé avec succès à {}", to);
    }
}

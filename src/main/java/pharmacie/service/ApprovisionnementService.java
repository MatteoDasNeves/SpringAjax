package pharmacie.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Medicament;

@Service
public class ApprovisionnementService {

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private JavaMailSender mailSender;

    public List<String> processApprovisionnement() {
        List<Medicament> aReapprovisionner = medicamentRepository.findAll().stream()
                .filter(m -> m.getUnitesEnStock() < m.getNiveauDeReappro())
                .collect(Collectors.toList());

        Map<String, StringBuilder> emailsParFournisseur = new HashMap<>();

        for (Medicament m : aReapprovisionner) {
            Categorie cat = m.getCategorie();
            if (cat != null) {
                cat.getFournisseurs().forEach(f -> {
                    String email = f.getEmail();
                    emailsParFournisseur.computeIfAbsent(email, k -> new StringBuilder("Bonjour " + f.getNom() + ",\n\nMerci de nous établir un devis pour les médicaments suivants :\n\n"))
                        .append("Catégorie: ").append(cat.getLibelle()).append("\n")
                        .append("- ").append(m.getNom()).append(" (Stock: ").append(m.getUnitesEnStock()).append(", Reappro: ").append(m.getNiveauDeReappro()).append(")\n\n");
                });
            }
        }

        List<String> fournisseursContactes = new ArrayList<>();
        emailsParFournisseur.forEach((email, body) -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Devis de réapprovisionnement");
            message.setText(body.toString());
            // Uncomment the next line when configuring actual SMTP
            // mailSender.send(message);
            fournisseursContactes.add(email);
        });

        return fournisseursContactes;
    }
}

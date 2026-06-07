package pharmacie.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ApprovisionnementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/approvisionnement")
@Tag(name = "Approvisionnement", description = "Gestion du réapprovisionnement automatique")
public class ApprovisionnementController {

    @Autowired
    private ApprovisionnementService approvisionnementService;

    @PostMapping("/lancer")
    @Operation(summary = "Lance la procédure de réapprovisionnement", 
               description = "Identifie les médicaments en rupture de stock et contacte les fournisseurs par email.")
    @ApiResponse(responseCode = "200", description = "La liste des fournisseurs contactés")
    @ApiResponse(responseCode = "500", description = "Erreur lors du traitement ou de l'envoi des emails")
    public ResponseEntity<List<String>> lancerApprovisionnement() {
        List<String> result = approvisionnementService.processApprovisionnement();
        return ResponseEntity.ok(result);
    }
}

package pharmacie.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ApprovisionnementService;

@RestController
@RequestMapping("/api/approvisionnement")
public class ApprovisionnementController {

    @Autowired
    private ApprovisionnementService approvisionnementService;

    @PostMapping("/lancer")
    public List<String> lancerApprovisionnement() {
        return approvisionnementService.processApprovisionnement();
    }
}

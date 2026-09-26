package com.defectiq.controller;

import com.defectiq.dto.*;
import com.defectiq.model.Defect;
import com.defectiq.repository.DefectRepository;
import com.defectiq.service.EnhancementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/defects")
@CrossOrigin(origins="http://localhost:5173")
public class DefectController {
    private final EnhancementService service; private final DefectRepository repo;
    public DefectController(EnhancementService service,DefectRepository repo){this.service=service;this.repo=repo;}

    @PostMapping("/enhance")
    public EnhanceResponse enhance(@Valid @RequestBody EnhanceRequest request){return service.enhance(request);}

    @PostMapping
    public ResponseEntity<Defect> save(@RequestBody SaveRequest request){
        EnhanceResponse e=request.enhanced(); Defect d=new Defect();
        d.setTitle(e.title()); d.setRawDescription(request.rawDescription()); d.setEnhancedSummary(e.summary());
        d.setStepsToReproduce(String.join("\n",e.stepsToReproduce())); d.setExpectedResult(e.expectedResult());
        d.setActualResult(e.actualResult()); d.setEnvironment(e.environment()); d.setBrowser(e.browser());
        d.setApplicationVersion(e.applicationVersion()); d.setSeverity(e.severity()); d.setPriority(e.priority());
        d.setReproducibility(e.reproducibility()); d.setQualityScore(e.qualityScore());
        d.setClarityScore(e.qualityBreakdown().clarity()); d.setCompletenessScore(e.qualityBreakdown().completeness());
        d.setReproducibilityScore(e.qualityBreakdown().reproducibility()); d.setExpectedActualScore(e.qualityBreakdown().expectedActual());
        d.setEnvironmentScore(e.qualityBreakdown().environment()); d.setEvidenceScore(e.qualityBreakdown().evidence());
        d.setMissingInformation(String.join("\n",e.missingInformation()));
        d.setClarificationQuestions(String.join("\n",e.clarificationQuestions()));
        return ResponseEntity.ok(repo.save(d));
    }

    @GetMapping public List<Defect> list(){return repo.findTop20ByOrderByCreatedAtDesc();}
    @GetMapping("/{id}") public ResponseEntity<Defect> get(@PathVariable Long id){
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    public record SaveRequest(String rawDescription,EnhanceResponse enhanced){}
}

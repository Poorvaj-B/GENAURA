package com.defectiq.service;

import com.defectiq.dto.EnhanceResponse;
import com.defectiq.model.Defect;
import com.defectiq.repository.DefectRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DuplicateService {
    private final DefectRepository repo;
    public DuplicateService(DefectRepository repo){this.repo=repo;}
    public List<EnhanceResponse.DuplicateMatch> find(String text){
        Set<String> source=tokens(text);
        return repo.findTop20ByOrderByCreatedAtDesc().stream().map(d->{
            Set<String> target=tokens(safe(d.getTitle())+" "+safe(d.getEnhancedSummary())+" "+safe(d.getRawDescription()));
            Set<String> in=new HashSet<>(source); in.retainAll(target);
            Set<String> un=new HashSet<>(source); un.addAll(target);
            double sim=un.isEmpty()?0:(double)in.size()/un.size();
            return new EnhanceResponse.DuplicateMatch(d.getId(),d.getTitle(),d.getQualityScore(),Math.round(sim*1000)/1000.0);
        }).filter(x->x.similarity()>=.25).sorted(Comparator.comparing(EnhanceResponse.DuplicateMatch::similarity).reversed()).limit(3).toList();
    }
    private Set<String> tokens(String s){return new HashSet<>(Arrays.asList(safe(s).toLowerCase().split("[^a-z0-9]+")));}
    private String safe(String s){return s==null?"":s;}
}

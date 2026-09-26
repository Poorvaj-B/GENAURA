package com.defectiq.service;

import com.defectiq.dto.EnhanceResponse;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
import java.util.List;

@Service
public class QualityService {
    public EnhanceResponse.QualityBreakdown score(String raw,String title,List<String> steps,
        String expected,String actual,String env,String browser,String version) {
        int clarity = 45 + (raw.length()>60?15:0) + (raw.length()>120?10:0)
                + (title!=null && title.length()>14?15:0)
                + (!raw.matches("(?i).*\\b(thing|issue|problem)\\b.*")?10:0);
        clarity=Math.min(100,clarity);
        int completeness=(expected!=null?25:0)+(actual!=null?25:0)+(has(env)?20:0)+(has(browser)?15:0)+(has(version)?15:0);
        int reproducibility=Math.min(100,45+steps.size()*10+(raw.matches("(?i).*\\b(always|every time|100%).*")?10:0));
        int expectedActual=expected!=null&&actual!=null?95:(expected!=null||actual!=null?55:20);
        int environment=has(env)?(has(browser)?95:70):25;
        String evidenceText=(raw+" "+actual);
        boolean err=Pattern.compile("(?i)(error|exception|http\\s*[45]\\d\\d|timeout|failed)").matcher(evidenceText).find();
        boolean id=Pattern.compile("(?i)(id|transaction|request|trace)[\\s:#-]*[a-z0-9-]{4,}").matcher(evidenceText).find();
        int evidence=err&&id?95:err?75:30;
        return new EnhanceResponse.QualityBreakdown(clarity,completeness,reproducibility,expectedActual,environment,evidence);
    }
    public int overall(EnhanceResponse.QualityBreakdown q) {
        return Math.round(q.clarity()*.20f+q.completeness()*.20f+q.reproducibility()*.25f+
                q.expectedActual()*.15f+q.environment()*.10f+q.evidence()*.10f);
    }
    private boolean has(String s){return s!=null&&!s.isBlank();}
}

package com.defectiq.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "defects")
public class Defect {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length=6000) private String rawDescription;
    @Column(length=6000) private String enhancedSummary;
    @Column(length=6000) private String stepsToReproduce;
    @Column(length=3000) private String expectedResult;
    @Column(length=3000) private String actualResult;
    private String environment;
    private String browser;
    private String applicationVersion;
    private String severity;
    private String priority;
    private String reproducibility;
    private Integer qualityScore;
    private Integer clarityScore;
    private Integer completenessScore;
    private Integer reproducibilityScore;
    private Integer expectedActualScore;
    private Integer environmentScore;
    private Integer evidenceScore;
    @Column(length=3000) private String missingInformation;
    @Column(length=3000) private String clarificationQuestions;
    private LocalDateTime createdAt;

    @PrePersist void created() { if (createdAt == null) createdAt = LocalDateTime.now(); }

    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getRawDescription(){return rawDescription;} public void setRawDescription(String v){rawDescription=v;}
    public String getEnhancedSummary(){return enhancedSummary;} public void setEnhancedSummary(String v){enhancedSummary=v;}
    public String getStepsToReproduce(){return stepsToReproduce;} public void setStepsToReproduce(String v){stepsToReproduce=v;}
    public String getExpectedResult(){return expectedResult;} public void setExpectedResult(String v){expectedResult=v;}
    public String getActualResult(){return actualResult;} public void setActualResult(String v){actualResult=v;}
    public String getEnvironment(){return environment;} public void setEnvironment(String v){environment=v;}
    public String getBrowser(){return browser;} public void setBrowser(String v){browser=v;}
    public String getApplicationVersion(){return applicationVersion;} public void setApplicationVersion(String v){applicationVersion=v;}
    public String getSeverity(){return severity;} public void setSeverity(String v){severity=v;}
    public String getPriority(){return priority;} public void setPriority(String v){priority=v;}
    public String getReproducibility(){return reproducibility;} public void setReproducibility(String v){reproducibility=v;}
    public Integer getQualityScore(){return qualityScore;} public void setQualityScore(Integer v){qualityScore=v;}
    public Integer getClarityScore(){return clarityScore;} public void setClarityScore(Integer v){clarityScore=v;}
    public Integer getCompletenessScore(){return completenessScore;} public void setCompletenessScore(Integer v){completenessScore=v;}
    public Integer getReproducibilityScore(){return reproducibilityScore;} public void setReproducibilityScore(Integer v){reproducibilityScore=v;}
    public Integer getExpectedActualScore(){return expectedActualScore;} public void setExpectedActualScore(Integer v){expectedActualScore=v;}
    public Integer getEnvironmentScore(){return environmentScore;} public void setEnvironmentScore(Integer v){environmentScore=v;}
    public Integer getEvidenceScore(){return evidenceScore;} public void setEvidenceScore(Integer v){evidenceScore=v;}
    public String getMissingInformation(){return missingInformation;} public void setMissingInformation(String v){missingInformation=v;}
    public String getClarificationQuestions(){return clarificationQuestions;} public void setClarificationQuestions(String v){clarificationQuestions=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;}
}

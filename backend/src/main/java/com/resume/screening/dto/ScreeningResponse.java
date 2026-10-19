package com.resume.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningResponse {

    private Long id;
    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;

    private double overallScore;
    private double skillScore;
    private double experienceScore;
    private double educationScore;
    private double keywordScore;
    private double qualificationScore;

    @Builder.Default
    private List<String> matchedSkills = new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();

    private String strengths;
    private String experienceAnalysis;
    private String recommendation;
    private String explanationJson;
    private LocalDateTime screenedAt;
}

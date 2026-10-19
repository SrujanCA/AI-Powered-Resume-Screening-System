package com.resume.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateRankingResponse {

    private int rank;
    private Long applicationId;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private int totalExperience;
    private String education;
    private String resumeFilePath;

    private double overallScore;
    private double skillScore;
    private double experienceScore;
    private double educationScore;
    private double keywordScore;
    private double qualificationScore;

    private LocalDateTime screenedAt;
}

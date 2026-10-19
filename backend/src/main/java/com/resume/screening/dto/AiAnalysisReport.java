package com.resume.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAnalysisReport {

    private String overallSummary;

    @Builder.Default
    private List<String> detailedStrengths = new ArrayList<>();

    @Builder.Default
    private List<String> areasForImprovement = new ArrayList<>();

    private String experienceEvaluation;
    private String recommendationReasoning;

    private boolean aiPowered;
    private String modelUsed;
}

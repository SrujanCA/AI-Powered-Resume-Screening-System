package com.resume.screening.ai;

import com.resume.screening.dto.AiAnalysisReport;
import com.resume.screening.dto.CandidateResponse;
import com.resume.screening.dto.JobResponse;
import com.resume.screening.dto.ScreeningResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RuleBasedFallbackEngine {

    public AiAnalysisReport generateFallbackReport(JobResponse job, CandidateResponse candidate, ScreeningResponse screening) {
        log.info("Generating Rule-Based Fallback Analysis Report for candidate: {}", candidate.getName());

        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        // Analyze Skills
        if (screening.getMatchedSkills() != null && !screening.getMatchedSkills().isEmpty()) {
            strengths.add("Demonstrates strong expertise in key required skills: " + String.join(", ", screening.getMatchedSkills()) + ".");
        }
        if (screening.getMissingSkills() != null && !screening.getMissingSkills().isEmpty()) {
            improvements.add("Missing required or preferred job skills: " + String.join(", ", screening.getMissingSkills()) + ".");
        }

        // Analyze Experience
        int candidateExp = candidate.getTotalExperience();
        int minReqExp = job.getMinExperience();
        String expEval;
        if (candidateExp >= minReqExp) {
            strengths.add(String.format("Has %d years of experience, satisfying the minimum requirement of %d years for %s.", candidateExp, minReqExp, job.getTitle()));
            expEval = String.format("Candidate possesses %d years of relevant professional experience, exceeding the baseline job requirement of %d years.", candidateExp, minReqExp);
        } else {
            improvements.add(String.format("Candidate has %d years of experience, which is below the minimum required %d years.", candidateExp, minReqExp));
            expEval = String.format("Candidate possesses %d years of experience vs %d years minimum required. Additional mentoring may be needed.", candidateExp, minReqExp);
        }

        // Analyze Education & Certifications
        if (candidate.getEducation() != null && !candidate.getEducation().isEmpty()) {
            strengths.add("Educational qualification: " + candidate.getEducation().replaceAll("\n", " ") + ".");
        }
        if (candidate.getCertifications() != null && !candidate.getCertifications().isEmpty()) {
            strengths.add("Holds relevant certifications: " + candidate.getCertifications().replaceAll("\n", " ") + ".");
        } else {
            improvements.add("No explicit certifications or professional accreditations found on resume.");
        }

        // Summary & Recommendation
        String summary = String.format("Candidate %s achieved an overall match score of %.1f%% for the position of %s at %s.",
                candidate.getName(), screening.getOverallScore(), job.getTitle(), job.getCompanyName());

        String recommendationReasoning = String.format("%s Candidate score breakdown: Skills (%.0f%%), Experience (%.0f%%), Education (%.0f%%), Keywords (%.0f%%).",
                screening.getRecommendation(), screening.getSkillScore(), screening.getExperienceScore(), screening.getEducationScore(), screening.getKeywordScore());

        return AiAnalysisReport.builder()
                .overallSummary(summary)
                .detailedStrengths(strengths)
                .areasForImprovement(improvements)
                .experienceEvaluation(expEval)
                .recommendationReasoning(recommendationReasoning)
                .aiPowered(false)
                .modelUsed("Rule-Based Heuristic Fallback Engine")
                .build();
    }
}

package com.resume.screening.mapper;

import com.resume.screening.dto.CandidateRankingResponse;
import com.resume.screening.dto.ScreeningResponse;
import com.resume.screening.entity.Application;
import com.resume.screening.entity.Candidate;
import com.resume.screening.entity.Job;
import com.resume.screening.entity.ScreeningResult;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ScreeningResultMapper {

    public ScreeningResponse toResponse(ScreeningResult result) {
        Application application = result.getApplication();
        Job job = application != null ? application.getJob() : null;
        Candidate candidate = application != null ? application.getCandidate() : null;

        List<String> matchedSkills = parseCommaList(result.getMatchedSkills());
        List<String> missingSkills = parseCommaList(result.getMissingSkills());

        return ScreeningResponse.builder()
                .id(result.getId())
                .applicationId(application != null ? application.getId() : null)
                .jobId(job != null ? job.getId() : null)
                .jobTitle(job != null ? job.getTitle() : "")
                .companyName(job != null ? job.getCompanyName() : "")
                .candidateId(candidate != null ? candidate.getId() : null)
                .candidateName(candidate != null ? candidate.getName() : "")
                .candidateEmail(candidate != null ? candidate.getEmail() : "")
                .overallScore(result.getOverallScore())
                .skillScore(result.getSkillScore())
                .experienceScore(result.getExperienceScore())
                .educationScore(result.getEducationScore())
                .keywordScore(result.getKeywordScore())
                .qualificationScore(result.getQualificationScore())
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .strengths(result.getStrengths())
                .experienceAnalysis(result.getExperienceAnalysis())
                .recommendation(result.getRecommendation())
                .explanationJson(result.getExplanationJson())
                .screenedAt(result.getScreenedAt())
                .build();
    }

    public CandidateRankingResponse toRankingResponse(ScreeningResult result, int rank) {
        Application application = result.getApplication();
        Candidate candidate = application != null ? application.getCandidate() : null;

        return CandidateRankingResponse.builder()
                .rank(rank)
                .applicationId(application != null ? application.getId() : null)
                .candidateId(candidate != null ? candidate.getId() : null)
                .candidateName(candidate != null ? candidate.getName() : "")
                .candidateEmail(candidate != null ? candidate.getEmail() : "")
                .totalExperience(candidate != null ? candidate.getTotalExperience() : 0)
                .education(candidate != null ? candidate.getEducation() : "")
                .resumeFilePath(candidate != null ? candidate.getResumeFilePath() : null)
                .overallScore(result.getOverallScore())
                .skillScore(result.getSkillScore())
                .experienceScore(result.getExperienceScore())
                .educationScore(result.getEducationScore())
                .keywordScore(result.getKeywordScore())
                .qualificationScore(result.getQualificationScore())
                .screenedAt(result.getScreenedAt())
                .build();
    }

    private List<String> parseCommaList(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}

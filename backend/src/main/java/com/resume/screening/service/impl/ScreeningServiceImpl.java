package com.resume.screening.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.screening.dto.CandidateRankingResponse;
import com.resume.screening.dto.ScreeningResponse;
import com.resume.screening.dto.SkillMatchResult;
import com.resume.screening.entity.*;
import com.resume.screening.exception.ResourceNotFoundException;
import com.resume.screening.mapper.ScreeningResultMapper;
import com.resume.screening.repository.ApplicationRepository;
import com.resume.screening.repository.CandidateRepository;
import com.resume.screening.repository.JobRepository;
import com.resume.screening.repository.ScreeningResultRepository;
import com.resume.screening.service.ScreeningService;
import com.resume.screening.service.SkillExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreeningServiceImpl implements ScreeningService {

    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final ApplicationRepository applicationRepository;
    private final ScreeningResultRepository screeningResultRepository;
    private final SkillExtractionService skillExtractionService;
    private final ScreeningResultMapper screeningResultMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ScreeningResponse screenCandidate(Long jobId, Long candidateId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", candidateId));

        // Get or create application link
        Application application = applicationRepository.findByJobIdAndCandidateId(jobId, candidateId)
                .orElseGet(() -> applicationRepository.save(Application.builder()
                        .job(job)
                        .candidate(candidate)
                        .status("SCREENED")
                        .build()));

        application.setStatus("SCREENED");
        applicationRepository.save(application);

        // 1. Skill Score (40%)
        List<String> jobSkills = job.getSkills().stream().map(JobSkill::getSkillName).toList();
        List<String> candidateSkills = candidate.getSkills().stream().map(CandidateSkill::getSkillName).toList();

        // Include auto-extracted skills from resume text if available
        if (candidate.getResumeText() != null && !candidate.getResumeText().isEmpty()) {
            Set<String> autoExtracted = skillExtractionService.extractSkills(candidate.getResumeText());
            Set<String> combined = new LinkedHashSet<>(candidateSkills);
            combined.addAll(autoExtracted);
            candidateSkills = new ArrayList<>(combined);
        }

        SkillMatchResult skillMatch = skillExtractionService.compareSkills(jobSkills, candidateSkills);
        double skillScore = skillMatch.getMatchPercentage(); // 0 - 100

        // 2. Experience Score (25%)
        double experienceScore = calculateExperienceScore(job.getMinExperience(), candidate.getTotalExperience());

        // 3. Education Score (10%)
        double educationScore = calculateEducationScore(job.getEducationRequirement(), candidate.getEducation());

        // 4. Keyword Score (15%)
        double keywordScore = calculateKeywordScore(job.getDescription(), candidate.getResumeText());

        // 5. Qualification Score (10%)
        double qualificationScore = calculateQualificationScore(candidate.getCertifications(), candidate.getProjects());

        // Overall Weighted Formula: 0.40 * Skill + 0.25 * Exp + 0.10 * Edu + 0.15 * Key + 0.10 * Qual
        double overallScore = (0.40 * skillScore) +
                             (0.25 * experienceScore) +
                             (0.10 * educationScore) +
                             (0.15 * keywordScore) +
                             (0.10 * qualificationScore);

        overallScore = Math.round(overallScore * 100.0) / 100.0;

        // Generate Explanations & Analysis
        String matchedSkillsStr = String.join(", ", skillMatch.getMatchedSkills());
        String missingSkillsStr = String.join(", ", skillMatch.getMissingSkills());
        String strengths = generateStrengthsSummary(skillMatch, candidate.getTotalExperience(), job.getMinExperience());
        String expAnalysis = String.format("Candidate has %d years of experience vs %d years minimum required.", candidate.getTotalExperience(), job.getMinExperience());
        String recommendation = generateRecommendation(overallScore);

        String explanationJson = generateExplanationJson(
                overallScore, skillScore, experienceScore, educationScore, keywordScore, qualificationScore,
                skillMatch, candidate.getTotalExperience(), job.getMinExperience()
        );

        // Save or update ScreeningResult
        ScreeningResult result = screeningResultRepository.findByApplicationId(application.getId())
                .orElse(ScreeningResult.builder().application(application).build());

        result.setOverallScore(overallScore);
        result.setSkillScore(skillScore);
        result.setExperienceScore(experienceScore);
        result.setEducationScore(educationScore);
        result.setKeywordScore(keywordScore);
        result.setQualificationScore(qualificationScore);
        result.setMatchedSkills(matchedSkillsStr);
        result.setMissingSkills(missingSkillsStr);
        result.setStrengths(strengths);
        result.setExperienceAnalysis(expAnalysis);
        result.setRecommendation(recommendation);
        result.setExplanationJson(explanationJson);

        ScreeningResult saved = screeningResultRepository.save(result);
        return screeningResultMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ScreeningResponse getScreeningResult(Long screeningResultId) {
        ScreeningResult result = screeningResultRepository.findById(screeningResultId)
                .orElseThrow(() -> new ResourceNotFoundException("ScreeningResult", "id", screeningResultId));
        return screeningResultMapper.toResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public ScreeningResponse getScreeningResultByApplication(Long applicationId) {
        ScreeningResult result = screeningResultRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("ScreeningResult", "applicationId", applicationId));
        return screeningResultMapper.toResponse(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateRankingResponse> getCandidateRankings(
            Long jobId,
            Double minScore,
            String skillFilter,
            Integer minExperience,
            String candidateName
    ) {
        List<ScreeningResult> results = screeningResultRepository.findByApplicationJobIdOrderByOverallScoreDesc(jobId);

        // Apply filters
        List<ScreeningResult> filtered = results.stream().filter(r -> {
            Candidate c = r.getApplication().getCandidate();
            if (minScore != null && r.getOverallScore() < minScore) return false;
            if (minExperience != null && c.getTotalExperience() < minExperience) return false;
            if (candidateName != null && !candidateName.trim().isEmpty() &&
                !c.getName().toLowerCase().contains(candidateName.toLowerCase().trim())) return false;
            if (skillFilter != null && !skillFilter.trim().isEmpty()) {
                String normalizedFilter = skillExtractionService.normalizeSkill(skillFilter);
                if (r.getMatchedSkills() == null || !r.getMatchedSkills().toLowerCase().contains(normalizedFilter.toLowerCase())) {
                    return false;
                }
            }
            return true;
        }).toList();

        AtomicInteger rankCounter = new AtomicInteger(1);
        return filtered.stream()
                .map(r -> screeningResultMapper.toRankingResponse(r, rankCounter.getAndIncrement()))
                .toList();
    }

    private double calculateExperienceScore(int minReq, int candidateExp) {
        if (minReq <= 0) return 100.0;
        if (candidateExp >= minReq) return 100.0;
        return Math.round(((double) candidateExp / minReq) * 100.0);
    }

    private double calculateEducationScore(String reqEdu, String candidateEdu) {
        if (reqEdu == null || reqEdu.trim().isEmpty()) return 100.0;
        if (candidateEdu == null || candidateEdu.trim().isEmpty()) return 50.0;

        String lowerReq = reqEdu.toLowerCase();
        String lowerCand = candidateEdu.toLowerCase();

        if (lowerCand.contains("ph.d") || lowerCand.contains("doctorate")) return 100.0;
        if (lowerReq.contains("master") && lowerCand.contains("master")) return 100.0;
        if (lowerReq.contains("bachelor") && (lowerCand.contains("bachelor") || lowerCand.contains("b.s") || lowerCand.contains("b.e") || lowerCand.contains("b.tech"))) return 100.0;
        if (lowerCand.contains("bachelor") || lowerCand.contains("b.s") || lowerCand.contains("b.tech")) return 90.0;
        if (lowerCand.contains("degree") || lowerCand.contains("diploma")) return 75.0;

        return 60.0;
    }

    private double calculateKeywordScore(String jobDesc, String resumeText) {
        if (jobDesc == null || resumeText == null || resumeText.isEmpty()) return 50.0;

        String[] words = jobDesc.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "").split("\\s+");
        Set<String> keywords = Arrays.stream(words)
                .filter(w -> w.length() > 4)
                .limit(40)
                .collect(Collectors.toSet());

        if (keywords.isEmpty()) return 100.0;

        String lowerResume = resumeText.toLowerCase();
        long matches = keywords.stream().filter(lowerResume::contains).count();

        return Math.round(((double) matches / keywords.size()) * 100.0);
    }

    private double calculateQualificationScore(String certs, String projects) {
        double score = 50.0; // Base baseline
        if (certs != null && !certs.trim().isEmpty()) score += 30.0;
        if (projects != null && !projects.trim().isEmpty()) score += 20.0;
        return Math.min(score, 100.0);
    }

    private String generateStrengthsSummary(SkillMatchResult skillMatch, int exp, int minReqExp) {
        List<String> strengths = new ArrayList<>();
        if (!skillMatch.getMatchedSkills().isEmpty()) {
            strengths.add("Matched key required skills: " + String.join(", ", skillMatch.getMatchedSkills()));
        }
        if (exp >= minReqExp) {
            strengths.add(String.format("Meets required experience threshold (%d years).", exp));
        }
        return String.join("; ", strengths);
    }

    private String generateRecommendation(double overallScore) {
        if (overallScore >= 85.0) return "Strong Candidate — Recommended to advance to technical interview.";
        if (overallScore >= 70.0) return "Good Candidate — Consider for initial recruiter screening.";
        if (overallScore >= 55.0) return "Moderate Candidate — Review missing skills before proceeding.";
        return "Low Fit — Candidate does not meet core job requirements.";
    }

    private String generateExplanationJson(
            double overall, double skill, double exp, double edu, double key, double qual,
            SkillMatchResult skillMatch, int candidateExp, int minReqExp
    ) {
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("overallScore", overall);
            map.put("breakdown", Map.of(
                    "skills", Map.of("weightedPoints", Math.round(0.40 * skill * 100.0) / 100.0, "maxPoints", 40.0, "narrative", String.format("%.0f%% of required skills matched (%s)", skill, String.join(", ", skillMatch.getMatchedSkills()))),
                    "experience", Map.of("weightedPoints", Math.round(0.25 * exp * 100.0) / 100.0, "maxPoints", 25.0, "narrative", String.format("Candidate has %d years vs %d required years", candidateExp, minReqExp)),
                    "education", Map.of("weightedPoints", Math.round(0.10 * edu * 100.0) / 100.0, "maxPoints", 10.0, "narrative", "Educational background evaluation completed"),
                    "keywords", Map.of("weightedPoints", Math.round(0.15 * key * 100.0) / 100.0, "maxPoints", 15.0, "narrative", String.format("%.0f%% keyword overlap with job description", key)),
                    "qualifications", Map.of("weightedPoints", Math.round(0.10 * qual * 100.0) / 100.0, "maxPoints", 10.0, "narrative", "Certifications and portfolio projects evaluation")
            ));
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return "{}";
        }
    }
}

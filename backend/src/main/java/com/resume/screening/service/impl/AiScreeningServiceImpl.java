package com.resume.screening.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.screening.ai.RuleBasedFallbackEngine;
import com.resume.screening.dto.AiAnalysisReport;
import com.resume.screening.dto.CandidateResponse;
import com.resume.screening.dto.JobResponse;
import com.resume.screening.dto.ScreeningResponse;
import com.resume.screening.service.AiScreeningService;
import com.resume.screening.service.CandidateService;
import com.resume.screening.service.JobService;
import com.resume.screening.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiScreeningServiceImpl implements AiScreeningService {

    private final ScreeningService screeningService;
    private final JobService jobService;
    private final CandidateService candidateService;
    private final RuleBasedFallbackEngine fallbackEngine;

    @Value("${app.ai.openai.api-key:}")
    private String openAiApiKey;

    @Value("${app.ai.openai.model:gpt-3.5-turbo}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AiAnalysisReport generateAiReport(Long screeningResultId) {
        ScreeningResponse screening = screeningService.getScreeningResult(screeningResultId);
        JobResponse job = jobService.getJobById(screening.getJobId());
        CandidateResponse candidate = candidateService.getCandidateById(screening.getCandidateId());

        if (StringUtils.hasText(openAiApiKey)) {
            try {
                log.info("OpenAI API key detected. Attempting Spring AI / LLM semantic analysis...");
                return callLlmForReport(job, candidate, screening);
            } catch (Exception ex) {
                log.warn("LLM API call failed or timed out. Falling back to Rule-Based Engine: {}", ex.getMessage());
            }
        } else {
            log.info("No LLM API Key configured. Using Rule-Based Fallback Engine for Candidate Screening Report.");
        }

        return fallbackEngine.generateFallbackReport(job, candidate, screening);
    }

    @SuppressWarnings("unchecked")
    private AiAnalysisReport callLlmForReport(JobResponse job, CandidateResponse candidate, ScreeningResponse screening) throws Exception {
        String prompt = String.format("""
                You are an expert HR Technical Recruiter. Analyze the candidate resume against the job description and return a JSON evaluation response.
                
                JOB TITLE: %s
                REQUIRED SKILLS: %s
                MINIMUM EXPERIENCE: %d years
                JOB DESCRIPTION: %s
                
                CANDIDATE NAME: %s
                MATCHED SKILLS: %s
                MISSING SKILLS: %s
                CANDIDATE EXPERIENCE: %d years
                OVERALL MATCH SCORE: %.1f%%
                RESUME TEXT SNIPPET: %s
                
                Return JSON format ONLY with keys:
                {
                  "overallSummary": "...",
                  "detailedStrengths": ["..."],
                  "areasForImprovement": ["..."],
                  "experienceEvaluation": "...",
                  "recommendationReasoning": "..."
                }
                """,
                job.getTitle(),
                job.getSkills().stream().map(s -> s.getSkillName()).toList(),
                job.getMinExperience(),
                job.getDescription(),
                candidate.getName(),
                screening.getMatchedSkills(),
                screening.getMissingSkills(),
                candidate.getTotalExperience(),
                screening.getOverallScore(),
                candidate.getResumeText() != null ? candidate.getResumeText().substring(0, Math.min(candidate.getResumeText().length(), 1000)) : ""
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> requestBody = Map.of(
                "model", modelName,
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a precise technical hiring assistant that outputs clean JSON."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.3
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        Map<?, ?> response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", entity, Map.class);

        if (response != null && response.containsKey("choices")) {
            List<?> choices = (List<?>) response.get("choices");
            if (!choices.isEmpty()) {
                Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                String content = (String) message.get("content");

                Map<?, ?> jsonResult = objectMapper.readValue(content, Map.class);

                return AiAnalysisReport.builder()
                        .overallSummary((String) jsonResult.get("overallSummary"))
                        .detailedStrengths((List<String>) jsonResult.get("detailedStrengths"))
                        .areasForImprovement((List<String>) jsonResult.get("areasForImprovement"))
                        .experienceEvaluation((String) jsonResult.get("experienceEvaluation"))
                        .recommendationReasoning((String) jsonResult.get("recommendationReasoning"))
                        .aiPowered(true)
                        .modelUsed(modelName)
                        .build();
            }
        }

        throw new RuntimeException("Empty response from LLM API");
    }
}

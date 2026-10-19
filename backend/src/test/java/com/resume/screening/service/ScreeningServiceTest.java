package com.resume.screening.service;

import com.resume.screening.dto.ScreeningResponse;
import com.resume.screening.dto.SkillMatchResult;
import com.resume.screening.entity.*;
import com.resume.screening.mapper.ScreeningResultMapper;
import com.resume.screening.repository.ApplicationRepository;
import com.resume.screening.repository.CandidateRepository;
import com.resume.screening.repository.JobRepository;
import com.resume.screening.repository.ScreeningResultRepository;
import com.resume.screening.service.impl.ScreeningServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ScreeningResultRepository screeningResultRepository;

    @Mock
    private SkillExtractionService skillExtractionService;

    @Mock
    private ScreeningResultMapper screeningResultMapper;

    @InjectMocks
    private ScreeningServiceImpl screeningService;

    private Job sampleJob;
    private Candidate sampleCandidate;
    private Application sampleApplication;
    private ScreeningResult sampleScreeningResult;

    @BeforeEach
    void setUp() {
        sampleJob = Job.builder()
                .id(1L)
                .title("Java Developer")
                .companyName("TechCorp")
                .minExperience(2)
                .educationRequirement("Bachelor")
                .description("Looking for a Java and Spring Boot Developer with REST API experience")
                .skills(new ArrayList<>())
                .build();
        sampleJob.addSkill(JobSkill.builder().skillName("Java").required(true).build());
        sampleJob.addSkill(JobSkill.builder().skillName("Spring Boot").required(true).build());

        sampleCandidate = Candidate.builder()
                .id(1L)
                .name("John Candidate")
                .email("john@example.com")
                .totalExperience(3)
                .education("B.S. in Computer Science")
                .resumeText("Experienced Java and Spring Boot Developer")
                .skills(new ArrayList<>())
                .build();
        sampleCandidate.addSkill(CandidateSkill.builder().skillName("Java").build());
        sampleCandidate.addSkill(CandidateSkill.builder().skillName("Spring Boot").build());

        sampleApplication = Application.builder()
                .id(1L)
                .job(sampleJob)
                .candidate(sampleCandidate)
                .status("SCREENED")
                .build();

        sampleScreeningResult = ScreeningResult.builder()
                .id(1L)
                .application(sampleApplication)
                .overallScore(91.0)
                .skillScore(100.0)
                .experienceScore(100.0)
                .educationScore(100.0)
                .keywordScore(75.0)
                .qualificationScore(80.0)
                .matchedSkills("Java, Spring Boot")
                .missingSkills("")
                .recommendation("Strong Candidate")
                .build();
    }

    @Test
    void screenCandidate_ShouldCalculateMultiFactorScore() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(sampleJob));
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(sampleCandidate));
        when(applicationRepository.findByJobIdAndCandidateId(1L, 1L)).thenReturn(Optional.of(sampleApplication));
        when(applicationRepository.save(any())).thenReturn(sampleApplication);

        SkillMatchResult mockMatch = SkillMatchResult.builder()
                .matchedSkills(List.of("Java", "Spring Boot"))
                .missingSkills(List.of())
                .matchPercentage(100.0)
                .build();

        when(skillExtractionService.compareSkills(any(), any())).thenReturn(mockMatch);
        when(screeningResultRepository.findByApplicationId(1L)).thenReturn(Optional.empty());
        when(screeningResultRepository.save(any())).thenReturn(sampleScreeningResult);

        ScreeningResponse expectedResponse = ScreeningResponse.builder()
                .id(1L)
                .overallScore(91.0)
                .recommendation("Strong Candidate")
                .build();

        when(screeningResultMapper.toResponse(any())).thenReturn(expectedResponse);

        ScreeningResponse result = screeningService.screenCandidate(1L, 1L);

        assertNotNull(result);
        assertEquals(91.0, result.getOverallScore());
        verify(screeningResultRepository, times(1)).save(any(ScreeningResult.class));
    }
}

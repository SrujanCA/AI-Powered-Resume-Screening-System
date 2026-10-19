package com.resume.screening.service;

import com.resume.screening.dto.CandidateRankingResponse;
import com.resume.screening.dto.ScreeningResponse;

import java.util.List;

public interface ScreeningService {

    ScreeningResponse screenCandidate(Long jobId, Long candidateId);

    ScreeningResponse getScreeningResult(Long screeningResultId);

    ScreeningResponse getScreeningResultByApplication(Long applicationId);

    List<CandidateRankingResponse> getCandidateRankings(
            Long jobId,
            Double minScore,
            String skillFilter,
            Integer minExperience,
            String candidateName
    );
}

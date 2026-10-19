package com.resume.screening.service;

import com.resume.screening.dto.CandidateRequest;
import com.resume.screening.dto.CandidateResponse;

import java.util.List;

public interface CandidateService {

    CandidateResponse createOrUpdateCandidate(CandidateRequest candidateRequest, Long userId);

    List<CandidateResponse> getAllCandidates();

    CandidateResponse getCandidateById(Long id);

    CandidateResponse getCandidateByUserId(Long userId);

    void deleteCandidate(Long id);
}

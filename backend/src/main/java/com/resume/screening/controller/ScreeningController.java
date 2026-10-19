package com.resume.screening.controller;

import com.resume.screening.dto.ApiResponse;
import com.resume.screening.dto.CandidateRankingResponse;
import com.resume.screening.dto.ScreeningResponse;
import com.resume.screening.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
public class ScreeningController {

    private final ScreeningService screeningService;

    @PostMapping("/{jobId}/{candidateId}")
    public ResponseEntity<ApiResponse<ScreeningResponse>> screenCandidate(
            @PathVariable Long jobId,
            @PathVariable Long candidateId) {
        ScreeningResponse response = screeningService.screenCandidate(jobId, candidateId);
        return ResponseEntity.ok(ApiResponse.success("Candidate screened successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScreeningResponse>> getScreeningResult(@PathVariable Long id) {
        ScreeningResponse response = screeningService.getScreeningResult(id);
        return ResponseEntity.ok(ApiResponse.success("Screening result retrieved", response));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<ApiResponse<ScreeningResponse>> getScreeningResultByApplication(@PathVariable Long applicationId) {
        ScreeningResponse response = screeningService.getScreeningResultByApplication(applicationId);
        return ResponseEntity.ok(ApiResponse.success("Screening result retrieved", response));
    }

    @GetMapping("/jobs/{jobId}/rankings")
    public ResponseEntity<ApiResponse<List<CandidateRankingResponse>>> getCandidateRankings(
            @PathVariable Long jobId,
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) String name) {
        List<CandidateRankingResponse> rankings = screeningService.getCandidateRankings(jobId, minScore, skill, minExperience, name);
        return ResponseEntity.ok(ApiResponse.success("Candidate rankings retrieved", rankings));
    }
}

package com.resume.screening.controller;

import com.resume.screening.dto.ApiResponse;
import com.resume.screening.dto.CandidateRequest;
import com.resume.screening.dto.CandidateResponse;
import com.resume.screening.dto.ParsedResumeData;
import com.resume.screening.security.UserPrincipal;
import com.resume.screening.service.CandidateService;
import com.resume.screening.service.ResumeParserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;
    private final ResumeParserService resumeParserService;

    @PostMapping("/parse-text")
    public ResponseEntity<ApiResponse<ParsedResumeData>> parseResumeText(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        ParsedResumeData parsedData = resumeParserService.parseResumeText(text);
        return ResponseEntity.ok(ApiResponse.success("Resume text parsed successfully", parsedData));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CandidateResponse>> saveCandidate(
            @Valid @RequestBody CandidateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        CandidateResponse response = candidateService.createOrUpdateCandidate(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Candidate profile saved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_RECRUITER')")
    public ResponseEntity<ApiResponse<List<CandidateResponse>>> getAllCandidates() {
        List<CandidateResponse> candidates = candidateService.getAllCandidates();
        return ResponseEntity.ok(ApiResponse.success("Candidates retrieved successfully", candidates));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> getCandidateById(@PathVariable Long id) {
        CandidateResponse candidate = candidateService.getCandidateById(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile retrieved", candidate));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CandidateResponse>> getMyCandidateProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        CandidateResponse candidate = candidateService.getCandidateByUserId(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Candidate profile retrieved", candidate));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER')")
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile deleted successfully"));
    }
}

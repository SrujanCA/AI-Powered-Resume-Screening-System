package com.resume.screening.controller;

import com.resume.screening.dto.AiAnalysisReport;
import com.resume.screening.dto.ApiResponse;
import com.resume.screening.service.AiScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/screening")
@RequiredArgsConstructor
public class AiScreeningController {

    private final AiScreeningService aiScreeningService;

    @GetMapping("/{id}/ai-report")
    public ResponseEntity<ApiResponse<AiAnalysisReport>> getAiReport(@PathVariable Long id) {
        AiAnalysisReport report = aiScreeningService.generateAiReport(id);
        return ResponseEntity.ok(ApiResponse.success("AI Candidate Analysis Report generated successfully", report));
    }
}

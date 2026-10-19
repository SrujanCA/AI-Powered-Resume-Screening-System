package com.resume.screening.controller;

import com.resume.screening.dto.ApiResponse;
import com.resume.screening.dto.SkillMatchResult;
import com.resume.screening.service.SkillExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillExtractionService skillExtractionService;

    @PostMapping("/normalize")
    public ResponseEntity<ApiResponse<Map<String, String>>> normalizeSkill(@RequestBody Map<String, String> payload) {
        String rawSkill = payload.get("skill");
        String normalized = skillExtractionService.normalizeSkill(rawSkill);
        return ResponseEntity.ok(ApiResponse.success("Skill normalized successfully", Map.of(
                "original", rawSkill != null ? rawSkill : "",
                "normalized", normalized
        )));
    }

    @PostMapping("/extract")
    public ResponseEntity<ApiResponse<Set<String>>> extractSkills(@RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        Set<String> skills = skillExtractionService.extractSkills(text);
        return ResponseEntity.ok(ApiResponse.success("Skills extracted successfully", skills));
    }

    @PostMapping("/compare")
    public ResponseEntity<ApiResponse<SkillMatchResult>> compareSkills(@RequestBody Map<String, List<String>> payload) {
        List<String> requiredSkills = payload.getOrDefault("requiredSkills", List.of());
        List<String> candidateSkills = payload.getOrDefault("candidateSkills", List.of());
        SkillMatchResult result = skillExtractionService.compareSkills(requiredSkills, candidateSkills);
        return ResponseEntity.ok(ApiResponse.success("Skills compared successfully", result));
    }

    @GetMapping("/dictionary")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getSkillDictionary() {
        return ResponseEntity.ok(ApiResponse.success("Skill dictionary retrieved successfully", skillExtractionService.getSkillDictionary()));
    }
}

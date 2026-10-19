package com.resume.screening.service;

import com.resume.screening.dto.SkillMatchResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SkillExtractionService {

    String normalizeSkill(String skillName);

    Set<String> extractSkills(String text);

    SkillMatchResult compareSkills(List<String> requiredSkills, List<String> candidateSkills);

    Map<String, List<String>> getSkillDictionary();
}

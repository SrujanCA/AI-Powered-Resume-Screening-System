package com.resume.screening.service;

import com.resume.screening.dto.SkillMatchResult;
import com.resume.screening.service.impl.SkillExtractionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SkillExtractionServiceTest {

    private SkillExtractionService skillExtractionService;

    @BeforeEach
    void setUp() {
        skillExtractionService = new SkillExtractionServiceImpl();
    }

    @Test
    void normalizeSkill_ShouldNormalizeAliasesToCanonicalNames() {
        assertEquals("REST API", skillExtractionService.normalizeSkill("RESTful Web Services"));
        assertEquals("REST API", skillExtractionService.normalizeSkill("REST API"));
        assertEquals("JavaScript", skillExtractionService.normalizeSkill("JS"));
        assertEquals("Spring Boot", skillExtractionService.normalizeSkill("SpringBoot"));
        assertEquals("Docker", skillExtractionService.normalizeSkill("Docker Container"));
        assertEquals("Kubernetes", skillExtractionService.normalizeSkill("K8s"));
    }

    @Test
    void extractSkills_ShouldDetectSkillsInUnstructuredText() {
        String sampleText = "Senior Developer experienced with Java 21, Spring Boot Framework, JS, and MySQL Database. Deployed applications on AWS using Docker containers.";
        Set<String> extracted = skillExtractionService.extractSkills(sampleText);

        assertTrue(extracted.contains("Java"));
        assertTrue(extracted.contains("Spring Boot"));
        assertTrue(extracted.contains("JavaScript"));
        assertTrue(extracted.contains("MySQL"));
        assertTrue(extracted.contains("AWS"));
        assertTrue(extracted.contains("Docker"));
    }

    @Test
    void compareSkills_ShouldCalculateMatchPercentageCorrectly() {
        List<String> required = List.of("REST API", "Java", "Docker", "AWS");
        List<String> candidate = List.of("RESTful Services", "Java 17", "MySQL");

        SkillMatchResult result = skillExtractionService.compareSkills(required, candidate);

        assertNotNull(result);
        assertEquals(2, result.getMatchedSkills().size());
        assertTrue(result.getMatchedSkills().contains("REST API"));
        assertTrue(result.getMatchedSkills().contains("Java"));
        assertEquals(2, result.getMissingSkills().size());
        assertEquals(50.0, result.getMatchPercentage());
    }
}

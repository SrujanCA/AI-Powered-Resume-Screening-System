package com.resume.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParsedResumeData {

    private String name;
    private String email;
    private String phone;
    private String education;
    private int totalExperienceYears;
    private String certifications;
    private String projects;

    @Builder.Default
    private List<String> extractedSkills = new ArrayList<>();

    private String rawText;
}

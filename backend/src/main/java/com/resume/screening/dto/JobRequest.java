package com.resume.screening.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String location;

    @NotBlank(message = "Job description is required")
    private String description;

    @Min(value = 0, message = "Minimum experience cannot be negative")
    private int minExperience;

    private String educationRequirement;

    @NotEmpty(message = "At least one skill is required")
    @Valid
    @Builder.Default
    private List<JobSkillDto> skills = new ArrayList<>();
}

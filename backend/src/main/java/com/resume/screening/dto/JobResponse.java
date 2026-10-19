package com.resume.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String companyName;
    private String location;
    private String description;
    private int minExperience;
    private String educationRequirement;
    private Long recruiterId;
    private String recruiterName;

    @Builder.Default
    private List<JobSkillDto> skills = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

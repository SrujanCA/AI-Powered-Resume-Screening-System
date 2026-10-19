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
public class CandidateResponse {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String education;
    private int totalExperience;
    private String certifications;
    private String projects;
    private String resumeFilePath;
    private String resumeText;

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    private LocalDateTime createdAt;
}

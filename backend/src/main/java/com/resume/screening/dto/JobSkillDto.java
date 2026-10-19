package com.resume.screening.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSkillDto {

    private Long id;

    @NotBlank(message = "Skill name is required")
    private String skillName;

    @Builder.Default
    private boolean required = true;
}

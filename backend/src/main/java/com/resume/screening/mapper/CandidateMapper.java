package com.resume.screening.mapper;

import com.resume.screening.dto.CandidateRequest;
import com.resume.screening.dto.CandidateResponse;
import com.resume.screening.entity.Candidate;
import com.resume.screening.entity.CandidateSkill;
import com.resume.screening.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CandidateMapper {

    public Candidate toEntity(CandidateRequest request, User user) {
        Candidate candidate = Candidate.builder()
                .user(user)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .education(request.getEducation())
                .totalExperience(request.getTotalExperience())
                .certifications(request.getCertifications())
                .projects(request.getProjects())
                .resumeFilePath(request.getResumeFilePath())
                .resumeText(request.getResumeText())
                .build();

        if (request.getSkills() != null) {
            for (String skillName : request.getSkills()) {
                CandidateSkill skill = CandidateSkill.builder()
                        .skillName(skillName.trim())
                        .build();
                candidate.addSkill(skill);
            }
        }

        return candidate;
    }

    public CandidateResponse toResponse(Candidate candidate) {
        List<String> skills = candidate.getSkills().stream()
                .map(CandidateSkill::getSkillName)
                .toList();

        return CandidateResponse.builder()
                .id(candidate.getId())
                .userId(candidate.getUser() != null ? candidate.getUser().getId() : null)
                .name(candidate.getName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .education(candidate.getEducation())
                .totalExperience(candidate.getTotalExperience())
                .certifications(candidate.getCertifications())
                .projects(candidate.getProjects())
                .resumeFilePath(candidate.getResumeFilePath())
                .resumeText(candidate.getResumeText())
                .skills(skills)
                .createdAt(candidate.getCreatedAt())
                .build();
    }
}

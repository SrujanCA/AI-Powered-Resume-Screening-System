package com.resume.screening.mapper;

import com.resume.screening.dto.JobRequest;
import com.resume.screening.dto.JobResponse;
import com.resume.screening.dto.JobSkillDto;
import com.resume.screening.entity.Job;
import com.resume.screening.entity.JobSkill;
import com.resume.screening.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobMapper {

    public Job toEntity(JobRequest request, User recruiter) {
        Job job = Job.builder()
                .title(request.getTitle())
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .description(request.getDescription())
                .minExperience(request.getMinExperience())
                .educationRequirement(request.getEducationRequirement())
                .recruiter(recruiter)
                .build();

        if (request.getSkills() != null) {
            for (JobSkillDto skillDto : request.getSkills()) {
                JobSkill skill = JobSkill.builder()
                        .skillName(skillDto.getSkillName().trim())
                        .required(skillDto.isRequired())
                        .build();
                job.addSkill(skill);
            }
        }

        return job;
    }

    public JobResponse toResponse(Job job) {
        List<JobSkillDto> skillDtos = job.getSkills().stream()
                .map(s -> JobSkillDto.builder()
                        .id(s.getId())
                        .skillName(s.getSkillName())
                        .required(s.isRequired())
                        .build())
                .toList();

        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .companyName(job.getCompanyName())
                .location(job.getLocation())
                .description(job.getDescription())
                .minExperience(job.getMinExperience())
                .educationRequirement(job.getEducationRequirement())
                .recruiterId(job.getRecruiter() != null ? job.getRecruiter().getId() : null)
                .recruiterName(job.getRecruiter() != null ? job.getRecruiter().getFullName() : null)
                .skills(skillDtos)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}

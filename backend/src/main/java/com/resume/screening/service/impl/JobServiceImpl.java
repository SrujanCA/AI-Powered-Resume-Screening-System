package com.resume.screening.service.impl;

import com.resume.screening.dto.JobRequest;
import com.resume.screening.dto.JobResponse;
import com.resume.screening.dto.JobSkillDto;
import com.resume.screening.entity.Job;
import com.resume.screening.entity.JobSkill;
import com.resume.screening.entity.User;
import com.resume.screening.exception.ResourceNotFoundException;
import com.resume.screening.exception.UnauthorizedException;
import com.resume.screening.mapper.JobMapper;
import com.resume.screening.repository.JobRepository;
import com.resume.screening.repository.UserRepository;
import com.resume.screening.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public JobResponse createJob(JobRequest jobRequest, Long recruiterId) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", recruiterId));

        Job job = jobMapper.toEntity(jobRequest, recruiter);
        Job savedJob = jobRepository.save(job);
        return jobMapper.toResponse(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(jobMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
        return jobMapper.toResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getJobsByRecruiter(Long recruiterId) {
        return jobRepository.findByRecruiterId(recruiterId).stream()
                .map(jobMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long id, JobRequest jobRequest, Long recruiterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));

        if (job.getRecruiter() != null && !job.getRecruiter().getId().equals(recruiterId)) {
            throw new UnauthorizedException("You are not authorized to update this job listing");
        }

        job.setTitle(jobRequest.getTitle());
        job.setCompanyName(jobRequest.getCompanyName());
        job.setLocation(jobRequest.getLocation());
        job.setDescription(jobRequest.getDescription());
        job.setMinExperience(jobRequest.getMinExperience());
        job.setEducationRequirement(jobRequest.getEducationRequirement());

        // Update skills list
        job.getSkills().clear();
        if (jobRequest.getSkills() != null) {
            for (JobSkillDto skillDto : jobRequest.getSkills()) {
                JobSkill skill = JobSkill.builder()
                        .skillName(skillDto.getSkillName().trim())
                        .required(skillDto.isRequired())
                        .build();
                job.addSkill(skill);
            }
        }

        Job updatedJob = jobRepository.save(job);
        return jobMapper.toResponse(updatedJob);
    }

    @Override
    @Transactional
    public void deleteJob(Long id, Long recruiterId) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));

        if (job.getRecruiter() != null && !job.getRecruiter().getId().equals(recruiterId)) {
            throw new UnauthorizedException("You are not authorized to delete this job listing");
        }

        jobRepository.delete(job);
    }
}

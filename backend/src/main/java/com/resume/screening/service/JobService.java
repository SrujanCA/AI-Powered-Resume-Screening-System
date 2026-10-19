package com.resume.screening.service;

import com.resume.screening.dto.JobRequest;
import com.resume.screening.dto.JobResponse;

import java.util.List;

public interface JobService {

    JobResponse createJob(JobRequest jobRequest, Long recruiterId);

    List<JobResponse> getAllJobs();

    JobResponse getJobById(Long id);

    List<JobResponse> getJobsByRecruiter(Long recruiterId);

    JobResponse updateJob(Long id, JobRequest jobRequest, Long recruiterId);

    void deleteJob(Long id, Long recruiterId);
}

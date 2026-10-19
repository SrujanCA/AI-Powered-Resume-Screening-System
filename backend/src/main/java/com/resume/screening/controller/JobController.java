package com.resume.screening.controller;

import com.resume.screening.dto.ApiResponse;
import com.resume.screening.dto.JobRequest;
import com.resume.screening.dto.JobResponse;
import com.resume.screening.security.UserPrincipal;
import com.resume.screening.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> getAllJobs() {
        List<JobResponse> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(ApiResponse.success("Jobs fetched successfully", jobs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        JobResponse job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success("Job details fetched successfully", job));
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER')")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getMyJobs(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<JobResponse> jobs = jobService.getJobsByRecruiter(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Recruiter jobs fetched successfully", jobs));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_RECRUITER')")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody JobRequest jobRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        JobResponse response = jobService.createJob(jobRequest, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER')")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest jobRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        JobResponse response = jobService.updateJob(id, jobRequest, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Job updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_RECRUITER')")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        jobService.deleteJob(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Job deleted successfully"));
    }
}

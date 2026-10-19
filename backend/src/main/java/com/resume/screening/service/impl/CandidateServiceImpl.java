package com.resume.screening.service.impl;

import com.resume.screening.dto.CandidateRequest;
import com.resume.screening.dto.CandidateResponse;
import com.resume.screening.entity.Candidate;
import com.resume.screening.entity.CandidateSkill;
import com.resume.screening.entity.User;
import com.resume.screening.exception.ResourceNotFoundException;
import com.resume.screening.mapper.CandidateMapper;
import com.resume.screening.repository.CandidateRepository;
import com.resume.screening.repository.UserRepository;
import com.resume.screening.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final CandidateMapper candidateMapper;

    @Override
    @Transactional
    public CandidateResponse createOrUpdateCandidate(CandidateRequest request, Long userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        // Check if candidate already exists by email or userId
        Optional<Candidate> existingCandidate = Optional.empty();
        if (userId != null) {
            existingCandidate = candidateRepository.findByUserId(userId);
        }
        if (existingCandidate.isEmpty() && request.getEmail() != null) {
            existingCandidate = candidateRepository.findByEmail(request.getEmail());
        }

        Candidate candidate;
        if (existingCandidate.isPresent()) {
            candidate = existingCandidate.get();
            candidate.setName(request.getName());
            candidate.setEmail(request.getEmail());
            candidate.setPhone(request.getPhone());
            candidate.setEducation(request.getEducation());
            candidate.setTotalExperience(request.getTotalExperience());
            candidate.setCertifications(request.getCertifications());
            candidate.setProjects(request.getProjects());
            if (request.getResumeFilePath() != null) {
                candidate.setResumeFilePath(request.getResumeFilePath());
            }
            if (request.getResumeText() != null) {
                candidate.setResumeText(request.getResumeText());
            }

            // Update skills
            candidate.getSkills().clear();
            if (request.getSkills() != null) {
                for (String skillName : request.getSkills()) {
                    CandidateSkill skill = CandidateSkill.builder()
                            .skillName(skillName.trim())
                            .build();
                    candidate.addSkill(skill);
                }
            }
        } else {
            candidate = candidateMapper.toEntity(request, user);
        }

        Candidate saved = candidateRepository.save(candidate);
        return candidateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll().stream()
                .map(candidateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", id));
        return candidateMapper.toResponse(candidate);
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getCandidateByUserId(Long userId) {
        Candidate candidate = candidateRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "userId", userId));
        return candidateMapper.toResponse(candidate);
    }

    @Override
    @Transactional
    public void deleteCandidate(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", "id", id));
        candidateRepository.delete(candidate);
    }
}

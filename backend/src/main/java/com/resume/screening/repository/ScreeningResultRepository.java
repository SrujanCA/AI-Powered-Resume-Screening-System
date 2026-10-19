package com.resume.screening.repository;

import com.resume.screening.entity.ScreeningResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreeningResultRepository extends JpaRepository<ScreeningResult, Long> {

    Optional<ScreeningResult> findByApplicationId(Long applicationId);

    List<ScreeningResult> findByApplicationJobIdOrderByOverallScoreDesc(Long jobId);
}

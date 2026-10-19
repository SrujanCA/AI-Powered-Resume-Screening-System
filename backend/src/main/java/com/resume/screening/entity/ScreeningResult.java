package com.resume.screening.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "screening_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @Column(name = "overall_score", nullable = false)
    private double overallScore;

    @Column(name = "skill_score", nullable = false)
    private double skillScore;

    @Column(name = "experience_score", nullable = false)
    private double experienceScore;

    @Column(name = "education_score", nullable = false)
    private double educationScore;

    @Column(name = "keyword_score", nullable = false)
    private double keywordScore;

    @Column(name = "qualification_score", nullable = false)
    private double qualificationScore;

    @Column(name = "matched_skills", columnDefinition = "TEXT")
    private String matchedSkills;

    @Column(name = "missing_skills", columnDefinition = "TEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "experience_analysis", columnDefinition = "TEXT")
    private String experienceAnalysis;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "explanation_json", columnDefinition = "TEXT")
    private String explanationJson;

    @CreationTimestamp
    @Column(name = "screened_at", updatable = false)
    private LocalDateTime screenedAt;
}

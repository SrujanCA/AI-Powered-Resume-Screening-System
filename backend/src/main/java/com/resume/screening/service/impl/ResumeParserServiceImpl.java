package com.resume.screening.service.impl;

import com.resume.screening.dto.ParsedResumeData;
import com.resume.screening.service.ResumeParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ResumeParserServiceImpl implements ResumeParserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "(?:\\+?\\d{1,3}[ -]?)?\\(?\\d{3}\\)?[ -]?\\d{3}[ -]?\\d{4}"
    );

    private static final Pattern EXPERIENCE_YEARS_PATTERN = Pattern.compile(
            "(\\d{1,2})\\+?\\s*(?:years?|yrs?)\\s*(?:of)?\\s*(?:experience|exp)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern DATE_RANGE_PATTERN = Pattern.compile(
            "\\b(20\\d{2}|19\\d{2})\\s*[-–—to]+\\s*(20\\d{2}|19\\d{2}|present|current)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final List<String> DEGREE_KEYWORDS = List.of(
            "bachelor", "master", "ph.d", "doctorate", "b.s", "m.s", "b.e", "b.tech", "m.tech",
            "b.a", "m.a", "b.c.a", "m.c.a", "associate", "degree", "diploma"
    );

    private static final List<String> CERT_KEYWORDS = List.of(
            "certified", "certification", "aws", "azure", "gcp", "pmp", "oracle", "scrum",
            "ckad", "cka", "cissp", "ceh", "comptia", "certificate"
    );

    @Override
    public ParsedResumeData parseResumeText(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return ParsedResumeData.builder().build();
        }

        String email = extractEmail(rawText);
        String phone = extractPhone(rawText);
        String name = extractCandidateName(rawText, email);
        int totalExperienceYears = extractTotalExperience(rawText);
        String education = extractEducation(rawText);
        String certifications = extractCertifications(rawText);
        String projects = extractProjects(rawText);

        log.info("Parsed Resume -> Name: {}, Email: {}, Phone: {}, Exp: {} yrs", name, email, phone, totalExperienceYears);

        return ParsedResumeData.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .education(education)
                .totalExperienceYears(totalExperienceYears)
                .certifications(certifications)
                .projects(projects)
                .rawText(rawText)
                .build();
    }

    private String extractEmail(String text) {
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    private String extractPhone(String text) {
        Matcher matcher = PHONE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    private String extractCandidateName(String text, String email) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // Skip lines containing email, phone, or obvious section headers
            if (EMAIL_PATTERN.matcher(trimmed).find() || PHONE_PATTERN.matcher(trimmed).find()) continue;

            String lower = trimmed.toLowerCase();
            if (lower.contains("resume") || lower.contains("curriculum vitae") || lower.contains("cv") ||
                lower.contains("page") || lower.contains("contact") || lower.contains("summary") ||
                lower.contains("profile") || lower.contains("experience") || lower.contains("education")) {
                continue;
            }

            // Name candidate is usually 2 to 4 words on the first non-header line
            String[] words = trimmed.split("\\s+");
            if (words.length >= 2 && words.length <= 5 && trimmed.matches("^[a-zA-Z.\\s'-]+$")) {
                return trimmed;
            }
        }

        // Fallback: Infer name from email address if available
        if (email != null && email.contains("@")) {
            String prefix = email.split("@")[0];
            String[] parts = prefix.split("[._-]");
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) {
                    sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
                }
            }
            if (!sb.isEmpty()) {
                return sb.toString().trim();
            }
        }

        return "Candidate";
    }

    private int extractTotalExperience(String text) {
        // Direct pattern search: "X years of experience"
        Matcher matcher = EXPERIENCE_YEARS_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {}
        }

        // Date range calculation (e.g. 2020 - 2024 or 2021 - Present)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Matcher dateMatcher = DATE_RANGE_PATTERN.matcher(text);
        int calculatedYears = 0;

        while (dateMatcher.find()) {
            try {
                int startYear = Integer.parseInt(dateMatcher.group(1));
                String endStr = dateMatcher.group(2).toLowerCase();
                int endYear = (endStr.contains("present") || endStr.contains("current")) ? currentYear : Integer.parseInt(endStr);

                int diff = endYear - startYear;
                if (diff > 0 && diff < 40) {
                    calculatedYears += diff;
                }
            } catch (Exception ignored) {}
        }

        return Math.min(calculatedYears, 35); // Cap at reasonable number
    }

    private String extractEducation(String text) {
        StringBuilder educationText = new StringBuilder();
        String[] lines = text.split("\n");
        boolean inEducationSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            String lower = trimmed.toLowerCase();

            if (lower.equals("education") || lower.startsWith("education:") || lower.contains("academic qualification")) {
                inEducationSection = true;
                continue;
            }

            if (inEducationSection) {
                if (isNewSectionHeader(lower)) {
                    break;
                }
                if (!trimmed.isEmpty()) {
                    educationText.append(trimmed).append("\n");
                }
            } else {
                // Check standalone degree lines
                for (String degree : DEGREE_KEYWORDS) {
                    if (lower.contains(degree)) {
                        educationText.append(trimmed).append("\n");
                        break;
                    }
                }
            }
        }

        return educationText.toString().trim();
    }

    private String extractCertifications(String text) {
        StringBuilder certText = new StringBuilder();
        String[] lines = text.split("\n");
        boolean inCertSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            String lower = trimmed.toLowerCase();

            if (lower.contains("certifications") || lower.contains("certificates") || lower.contains("licenses")) {
                inCertSection = true;
                continue;
            }

            if (inCertSection) {
                if (isNewSectionHeader(lower)) {
                    break;
                }
                if (!trimmed.isEmpty()) {
                    certText.append(trimmed).append("\n");
                }
            } else {
                for (String cert : CERT_KEYWORDS) {
                    if (lower.contains(cert) && !lower.contains("education") && !lower.contains("degree")) {
                        certText.append(trimmed).append("\n");
                        break;
                    }
                }
            }
        }

        return certText.toString().trim();
    }

    private String extractProjects(String text) {
        StringBuilder projectText = new StringBuilder();
        String[] lines = text.split("\n");
        boolean inProjectSection = false;

        for (String line : lines) {
            String trimmed = line.trim();
            String lower = trimmed.toLowerCase();

            if (lower.equals("projects") || lower.startsWith("projects:") || lower.contains("key projects")) {
                inProjectSection = true;
                continue;
            }

            if (inProjectSection) {
                if (isNewSectionHeader(lower)) {
                    break;
                }
                if (!trimmed.isEmpty()) {
                    projectText.append(trimmed).append("\n");
                }
            }
        }

        return projectText.toString().trim();
    }

    private boolean isNewSectionHeader(String lowerLine) {
        return lowerLine.equals("skills") || lowerLine.startsWith("skills:") ||
               lowerLine.equals("experience") || lowerLine.startsWith("experience:") ||
               lowerLine.equals("work experience") || lowerLine.equals("employment history") ||
               lowerLine.equals("summary") || lowerLine.equals("objective") ||
               lowerLine.equals("languages") || lowerLine.equals("hobbies");
    }
}

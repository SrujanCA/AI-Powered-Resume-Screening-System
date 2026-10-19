package com.resume.screening.service.impl;

import com.resume.screening.dto.SkillMatchResult;
import com.resume.screening.service.SkillExtractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SkillExtractionServiceImpl implements SkillExtractionService {

    private static final Map<String, String> SKILL_ALIAS_MAP = new LinkedHashMap<>();
    private static final Map<String, List<String>> SKILL_DICTIONARY = new LinkedHashMap<>();

    static {
        // Backend Skills
        addSkillAlias("Java", List.of("java", "core java", "java 8", "java 11", "java 17", "java 21", "java ee", "jakarta ee"));
        addSkillAlias("Spring Boot", List.of("spring boot", "springboot", "spring boot framework"));
        addSkillAlias("Spring", List.of("spring", "spring framework", "spring mvc", "spring core"));
        addSkillAlias("JPA / Hibernate", List.of("jpa", "hibernate", "spring data jpa", "object relational mapping"));
        addSkillAlias("REST API", List.of("rest api", "restful api", "rest services", "restful services", "restful web services", "restful"));
        addSkillAlias("Node.js", List.of("node.js", "nodejs", "node"));
        addSkillAlias("Python", List.of("python", "python3", "django", "flask", "fastapi"));
        addSkillAlias("C# / .NET", List.of("c#", "csharp", ".net", "dotnet", "asp.net"));
        addSkillAlias("C++", List.of("c++", "cpp"));
        addSkillAlias("Microservices", List.of("microservices", "microservice", "microservices architecture"));
        addSkillAlias("GraphQL", List.of("graphql"));

        // Frontend Skills
        addSkillAlias("JavaScript", List.of("javascript", "js", "ecmascript"));
        addSkillAlias("TypeScript", List.of("typescript", "ts"));
        addSkillAlias("React", List.of("react", "react.js", "reactjs"));
        addSkillAlias("Angular", List.of("angular", "angular.js", "angularjs"));
        addSkillAlias("Vue.js", List.of("vue", "vue.js", "vuejs"));
        addSkillAlias("HTML5", List.of("html", "html5"));
        addSkillAlias("CSS3", List.of("css", "css3", "sass", "scss", "tailwind", "bootstrap"));

        // Database Skills
        addSkillAlias("MySQL", List.of("mysql", "mysql database"));
        addSkillAlias("PostgreSQL", List.of("postgresql", "postgres", "postgre"));
        addSkillAlias("MongoDB", List.of("mongodb", "mongo"));
        addSkillAlias("Redis", List.of("redis", "redis cache"));
        addSkillAlias("Oracle DB", List.of("oracle", "oracle database", "pl/sql"));
        addSkillAlias("SQL", List.of("sql", "relational database"));

        // Cloud & DevOps
        addSkillAlias("Docker", List.of("docker", "docker container", "dockerizing"));
        addSkillAlias("Kubernetes", List.of("kubernetes", "k8s"));
        addSkillAlias("AWS", List.of("aws", "amazon web services", "ec2", "s3", "lambda"));
        addSkillAlias("Azure", List.of("azure", "microsoft azure"));
        addSkillAlias("GCP", List.of("gcp", "google cloud", "google cloud platform"));
        addSkillAlias("Git", List.of("git", "github", "gitlab", "version control"));
        addSkillAlias("CI/CD", List.of("ci/cd", "cicd", "jenkins", "github actions", "gitlab ci"));

        // Testing & Methods
        addSkillAlias("JUnit", List.of("junit", "junit 5", "mockito"));
        addSkillAlias("Agile / Scrum", List.of("agile", "scrum", "jira"));
        addSkillAlias("Kafka", List.of("kafka", "apache kafka"));
    }

    private static void addSkillAlias(String canonicalName, List<String> aliases) {
        SKILL_DICTIONARY.put(canonicalName, aliases);
        for (String alias : aliases) {
            SKILL_ALIAS_MAP.put(alias.toLowerCase(), canonicalName);
        }
    }

    @Override
    public String normalizeSkill(String skillName) {
        if (skillName == null || skillName.trim().isEmpty()) {
            return "";
        }
        String cleanSkill = skillName.trim().toLowerCase();
        return SKILL_ALIAS_MAP.getOrDefault(cleanSkill, capitalizeWords(skillName.trim()));
    }

    @Override
    public Set<String> extractSkills(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> detectedSkills = new LinkedHashSet<>();
        String lowerText = text.toLowerCase();

        // Check each canonical skill and its aliases against the text using regex boundaries
        for (Map.Entry<String, List<String>> entry : SKILL_DICTIONARY.entrySet()) {
            String canonical = entry.getKey();
            List<String> aliases = entry.getValue();

            for (String alias : aliases) {
                // Word boundary matching to avoid false positives (e.g. "java" inside "javascript")
                String escapedAlias = Pattern.quote(alias);
                String patternString = "(?i)(^|[^a-zA-Z0-9#+])" + escapedAlias + "([^a-zA-Z0-9#+]|$)";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(text);

                if (matcher.find()) {
                    detectedSkills.add(canonical);
                    break;
                }
            }
        }

        return detectedSkills;
    }

    @Override
    public SkillMatchResult compareSkills(List<String> requiredSkills, List<String> candidateSkills) {
        if (requiredSkills == null) requiredSkills = Collections.emptyList();
        if (candidateSkills == null) candidateSkills = Collections.emptyList();

        Set<String> normalizedRequired = new HashSet<>();
        for (String req : requiredSkills) {
            normalizedRequired.add(normalizeSkill(req));
        }

        Set<String> normalizedCandidate = new HashSet<>();
        Map<String, String> candidateSkillMapping = new HashMap<>();
        for (String cand : candidateSkills) {
            String norm = normalizeSkill(cand);
            normalizedCandidate.add(norm);
            candidateSkillMapping.put(cand, norm);
        }

        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String reqSkill : normalizedRequired) {
            if (normalizedCandidate.contains(reqSkill)) {
                matchedSkills.add(reqSkill);
            } else {
                missingSkills.add(reqSkill);
            }
        }

        double matchPercentage = 0.0;
        if (!normalizedRequired.isEmpty()) {
            matchPercentage = ((double) matchedSkills.size() / normalizedRequired.size()) * 100.0;
        }

        return SkillMatchResult.builder()
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .matchPercentage(Math.round(matchPercentage * 100.0) / 100.0)
                .normalizedSkillsMap(candidateSkillMapping)
                .build();
    }

    @Override
    public Map<String, List<String>> getSkillDictionary() {
        return SKILL_DICTIONARY;
    }

    private String capitalizeWords(String input) {
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}

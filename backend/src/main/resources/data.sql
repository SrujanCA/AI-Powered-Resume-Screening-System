-- Demo data for local development. The BCrypt password for all demo users is: Password123!
-- IDs are explicit so the relationships remain readable in the sample dataset.

INSERT IGNORE INTO users (id, email, password, full_name, role)
VALUES
    (1, 'recruiter@demo.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO4ZfM8pW4v7Y5x2gqK9kLxM0y9n2Jx6G', 'Avery Morgan', 'ROLE_RECRUITER'),
    (2, 'talent@northstar.example', '$2a$10$7EqJtq98hPqEX7fNZaFWoO4ZfM8pW4v7Y5x2gqK9kLxM0y9n2Jx6G', 'Jordan Lee', 'ROLE_RECRUITER'),
    (3, 'maya.chen@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO4ZfM8pW4v7Y5x2gqK9kLxM0y9n2Jx6G', 'Maya Chen', 'ROLE_CANDIDATE'),
    (4, 'daniel.ortiz@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO4ZfM8pW4v7Y5x2gqK9kLxM0y9n2Jx6G', 'Daniel Ortiz', 'ROLE_CANDIDATE'),
    (5, 'priya.nair@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO4ZfM8pW4v7Y5x2gqK9kLxM0y9n2Jx6G', 'Priya Nair', 'ROLE_CANDIDATE'),
    (6, 'sam.wilson@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO4ZfM8pW4v7Y5x2gqK9kLxM0y9n2Jx6G', 'Sam Wilson', 'ROLE_CANDIDATE');

INSERT IGNORE INTO jobs (id, title, company_name, location, description, min_experience, education_requirement, recruiter_id)
VALUES
    (1, 'Senior Java Platform Engineer', 'Northstar Labs', 'Remote - US', 'Build resilient Spring Boot services and event-driven platform capabilities for a growing developer tools product.', 4, 'Bachelor degree in Computer Science or equivalent experience', 1),
    (2, 'Frontend Product Engineer', 'Northstar Labs', 'New York, NY / Hybrid', 'Own accessible React experiences and partner with design and backend teams to ship measurable product improvements.', 3, 'Bachelor degree in Computer Science, Design, or equivalent experience', 1);

INSERT IGNORE INTO job_skills (id, job_id, skill_name, is_required)
VALUES
    (1, 1, 'Java', TRUE),
    (2, 1, 'Spring Boot', TRUE),
    (3, 1, 'REST APIs', TRUE),
    (4, 1, 'MySQL', TRUE),
    (5, 1, 'Docker', FALSE),
    (6, 1, 'Kafka', FALSE),
    (7, 2, 'React', TRUE),
    (8, 2, 'JavaScript', TRUE),
    (9, 2, 'TypeScript', TRUE),
    (10, 2, 'CSS', TRUE),
    (11, 2, 'Accessibility', FALSE),
    (12, 2, 'Jest', FALSE);

INSERT IGNORE INTO candidates (id, user_id, name, email, phone, education, total_experience, certifications, projects, resume_text)
VALUES
    (1, 3, 'Maya Chen', 'maya.chen@example.com', '+1-415-555-0101', 'B.S. Computer Science, University of Washington', 7, 'AWS Certified Developer - Associate', 'Led migration of a monolith to Spring Boot services; introduced Kafka-based audit events.', 'Maya Chen is a backend platform engineer with seven years of experience building Java and Spring Boot services. Strong experience with REST APIs, MySQL, Docker, Kafka, AWS, and distributed systems.'),
    (2, 4, 'Daniel Ortiz', 'daniel.ortiz@example.com', '+1-512-555-0102', 'B.S. Software Engineering, Texas A&M University', 5, 'Oracle Certified Professional: Java SE 11 Developer', 'Built payment APIs and automated deployment pipelines for a financial services platform.', 'Daniel Ortiz is a Java engineer with five years of experience in Spring Boot, REST APIs, MySQL, Docker, and CI/CD. Has delivered reliable payment and integration services.'),
    (3, 5, 'Priya Nair', 'priya.nair@example.com', '+1-646-555-0103', 'M.S. Information Systems, Northeastern University', 4, 'AWS Certified Cloud Practitioner', 'Designed a React and TypeScript analytics workspace used by operations teams.', 'Priya Nair is a product engineer with four years of experience using React, JavaScript, TypeScript, CSS, Jest, and accessible design practices. Comfortable collaborating across product and design.'),
    (4, 6, 'Sam Wilson', 'sam.wilson@example.com', '+1-206-555-0104', 'B.A. Mathematics, University of Oregon', 2, '', 'Created internal dashboards with React and Node.js for customer support teams.', 'Sam Wilson is an early-career software engineer with two years of experience in React, JavaScript, CSS, Node.js, and testing. Interested in growing into full-stack product engineering.');

INSERT IGNORE INTO candidate_skills (id, candidate_id, skill_name)
VALUES
    (1, 1, 'Java'), (2, 1, 'Spring Boot'), (3, 1, 'REST APIs'), (4, 1, 'MySQL'), (5, 1, 'Docker'), (6, 1, 'Kafka'),
    (7, 2, 'Java'), (8, 2, 'Spring Boot'), (9, 2, 'REST APIs'), (10, 2, 'MySQL'), (11, 2, 'Docker'),
    (12, 3, 'React'), (13, 3, 'JavaScript'), (14, 3, 'TypeScript'), (15, 3, 'CSS'), (16, 3, 'Jest'), (17, 3, 'Accessibility'),
    (18, 4, 'React'), (19, 4, 'JavaScript'), (20, 4, 'CSS'), (21, 4, 'Node.js');

INSERT IGNORE INTO applications (id, job_id, candidate_id, status)
VALUES
    (1, 1, 1, 'SHORTLISTED'),
    (2, 1, 2, 'IN_REVIEW'),
    (3, 1, 3, 'REJECTED'),
    (4, 2, 3, 'SHORTLISTED'),
    (5, 2, 4, 'IN_REVIEW');

INSERT IGNORE INTO screening_results (id, application_id, overall_score, skill_score, experience_score, education_score, keyword_score, qualification_score, matched_skills, missing_skills, strengths, experience_analysis, recommendation, explanation_json)
VALUES
    (1, 1, 94.0, 98.0, 95.0, 90.0, 93.0, 92.0, 'Java, Spring Boot, REST APIs, MySQL, Docker, Kafka', '', 'Deep platform experience; strong distributed systems background; relevant AWS certification.', 'Seven years of directly relevant backend experience exceeds the four-year requirement.', 'STRONGLY_RECOMMEND', '{"skills":98,"experience":95,"keywords":93,"education":90,"certifications":92}'),
    (2, 2, 82.0, 88.0, 78.0, 86.0, 81.0, 80.0, 'Java, Spring Boot, REST APIs, MySQL, Docker', 'Kafka', 'Solid API and deployment experience with an industry-recognized Java certification.', 'Five years of relevant experience meets the minimum requirement.', 'RECOMMEND', '{"skills":88,"experience":78,"keywords":81,"education":86,"certifications":80}'),
    (3, 3, 36.0, 18.0, 45.0, 72.0, 38.0, 55.0, 'JavaScript', 'Java, Spring Boot, REST APIs, MySQL, Docker', 'Some transferable engineering experience.', 'Four years of experience but limited overlap with the backend role requirements.', 'DO_NOT_RECOMMEND', '{"skills":18,"experience":45,"keywords":38,"education":72,"certifications":55}'),
    (4, 4, 96.0, 98.0, 90.0, 94.0, 97.0, 92.0, 'React, JavaScript, TypeScript, CSS, Jest, Accessibility', '', 'Complete frontend skill match; strong product and accessibility experience.', 'Four years of relevant experience exceeds the three-year requirement.', 'STRONGLY_RECOMMEND', '{"skills":98,"experience":90,"keywords":97,"education":94,"certifications":92}'),
    (5, 5, 68.0, 72.0, 50.0, 70.0, 65.0, 48.0, 'React, JavaScript, CSS', 'TypeScript, Jest', 'Relevant React foundation and practical dashboard experience.', 'Two years of experience is below the stated three-year minimum.', 'CONSIDER', '{"skills":72,"experience":50,"keywords":65,"education":70,"certifications":48}');
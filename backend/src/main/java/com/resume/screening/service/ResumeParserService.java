package com.resume.screening.service;

import com.resume.screening.dto.ParsedResumeData;

public interface ResumeParserService {

    ParsedResumeData parseResumeText(String rawText);
}

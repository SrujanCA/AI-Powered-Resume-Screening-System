package com.resume.screening.service;

import com.resume.screening.dto.AiAnalysisReport;

public interface AiScreeningService {

    AiAnalysisReport generateAiReport(Long screeningResultId);
}

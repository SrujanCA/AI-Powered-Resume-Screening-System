package com.resume.screening.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeUploadResponse {

    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private String extractedText;
}

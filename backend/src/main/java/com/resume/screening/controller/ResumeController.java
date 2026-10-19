package com.resume.screening.controller;

import com.resume.screening.dto.ApiResponse;
import com.resume.screening.dto.ResumeUploadResponse;
import com.resume.screening.service.DocumentParserService;
import com.resume.screening.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final FileStorageService fileStorageService;
    private final DocumentParserService documentParserService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> uploadResume(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        Path filePath = fileStorageService.getFilePath(fileName);

        String extractedText = documentParserService.extractText(filePath);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/resumes/download/")
                .path(fileName)
                .toUriString();

        ResumeUploadResponse response = ResumeUploadResponse.builder()
                .fileName(fileName)
                .fileDownloadUri(fileDownloadUri)
                .fileType(file.getContentType())
                .size(file.getSize())
                .extractedText(extractedText)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Resume uploaded and parsed successfully using Apache Tika", response));
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadResume(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

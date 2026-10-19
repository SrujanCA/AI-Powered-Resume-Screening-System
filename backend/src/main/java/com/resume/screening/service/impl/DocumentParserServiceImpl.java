package com.resume.screening.service.impl;

import com.resume.screening.exception.FileStorageException;
import com.resume.screening.service.DocumentParserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class DocumentParserServiceImpl implements DocumentParserService {

    private final Tika tika = new Tika();

    @Override
    public String extractText(Path filePath) {
        log.info("Parsing text from file using Apache Tika: {}", filePath);
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return extractText(inputStream);
        } catch (IOException e) {
            log.error("Failed to read file for Tika text extraction: {}", filePath, e);
            throw new FileStorageException("Failed to read uploaded resume file for text parsing.", e);
        }
    }

    @Override
    public String extractText(InputStream inputStream) {
        try {
            String extractedText = tika.parseToString(inputStream);
            if (extractedText == null || extractedText.trim().isEmpty()) {
                log.warn("Apache Tika extracted empty text from input document stream.");
                return "";
            }
            // Clean up excessive whitespace and control characters while preserving lines
            return extractedText.replaceAll("\\r\\n|\\r", "\n")
                    .replaceAll("[\\t\\x0B\\f]", " ")
                    .replaceAll(" {2,}", " ")
                    .trim();
        } catch (IOException | TikaException e) {
            log.error("Apache Tika text extraction failed", e);
            throw new FileStorageException("Failed to parse document text content with Apache Tika.", e);
        }
    }
}

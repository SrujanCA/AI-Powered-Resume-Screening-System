package com.resume.screening.service;

import java.io.InputStream;
import java.nio.file.Path;

public interface DocumentParserService {

    String extractText(Path filePath);

    String extractText(InputStream inputStream);
}

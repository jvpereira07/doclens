package com.jvpereira.doclens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvpereira.doclens.exception.ResourceNotFoundException;
import com.jvpereira.doclens.model.extractionRequest.ExtractionRequest;
import com.jvpereira.doclens.model.extractionRequest.ExtractionStatus;
import com.jvpereira.doclens.model.extractionResult.ExtractionResult;
import com.jvpereira.doclens.model.file.File;
import com.jvpereira.doclens.model.template.Template;
import com.jvpereira.doclens.repository.ExtractionRequestRepository;
import com.jvpereira.doclens.repository.ExtractionResultRepository;
import com.jvpereira.doclens.repository.FileRepository;
import com.jvpereira.doclens.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtractionRequestService {

    private final ExtractionRequestRepository extractionRequestRepository;
    private final FileRepository fileRepository;
    private final TemplateRepository templateRepository;
    private final ExtractionResultRepository extractionResultRepository;
    private final MinioStorageService minioStorageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:${GEMINI_API_KEY:}}")
    private String geminiApiKey;

    @Transactional
    public String extract(String templateCode, MultipartFile fileUpload) {
        // 1. Fetch template
        Template template = templateRepository.findByTag(templateCode)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with tag: " + templateCode));

        // Upload file to MinIO storage
        String storagePath = minioStorageService.uploadFile(fileUpload);

        // 2. Save file info
        File file = new File();
        file.setName(fileUpload.getOriginalFilename() != null ? fileUpload.getOriginalFilename() : "uploaded_document_" + System.currentTimeMillis());
        file.setType(fileUpload.getContentType() != null ? fileUpload.getContentType() : "application/octet-stream");
        file.setStoragePath(storagePath);
        file = fileRepository.save(file);

        // 3. Build AI Prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an expert document data extraction system. Your task is to extract specific fields from the attached document and return them in a strict JSON format.\n\n");
        promptBuilder.append("Here is the template structure:\n");
        promptBuilder.append("Template Name: ").append(template.getName()).append("\n");
        if (template.getDescription() != null) {
            promptBuilder.append("Template Description: ").append(template.getDescription()).append("\n");
        }
        if (template.getPropHints() != null) {
            promptBuilder.append("Additional Hints: ").append(template.getPropHints()).append("\n");
        }
        promptBuilder.append("\nFields to extract (return a JSON object mapping the field code to the extracted value):\n");
        for (var field : template.getFields()) {
            promptBuilder.append("- Code: \"").append(field.getCode()).append("\"\n");
            promptBuilder.append("  Name: ").append(field.getName()).append("\n");
            promptBuilder.append("  Type: ").append(field.getType().name()).append("\n");
            promptBuilder.append("  Required: ").append(field.getRequired()).append("\n");
            if (field.getDescription() != null) {
                promptBuilder.append("  Description/Instructions: ").append(field.getDescription()).append("\n");
            }
        }
        promptBuilder.append("\nStrict Rules:\n");
        promptBuilder.append("1. Output MUST be a valid JSON object matching the keys listed above.\n");
        promptBuilder.append("2. Do not include markdown code block syntax (like ```json ... ```) or any other explanation.\n");
        promptBuilder.append("3. For missing optional fields, return null.\n");
        promptBuilder.append("4. Values must match the specified type formats (e.g. numbers, booleans, strings).\n");

        String prompt = promptBuilder.toString();

        // 4. Save ExtractionRequest entity as pending/processing
        ExtractionRequest extractionRequest = new ExtractionRequest();
        extractionRequest.setFile(file);
        extractionRequest.setPromptUsed(prompt);
        extractionRequest.setCreatedAt(Instant.now());
        extractionRequest.setStatus(ExtractionStatus.AI_ERROR); // default error state in case API call fails
        extractionRequest = extractionRequestRepository.save(extractionRequest);

        long startTime = System.currentTimeMillis();
        String aiResponseText = null;

        try {
            if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
                throw new IllegalStateException("Gemini API key is not configured. Please set GEMINI_API_KEY environment variable.");
            }

            // Convert file upload to Base64
            byte[] fileBytes = fileUpload.getBytes();
            String base64Data = Base64.getEncoder().encodeToString(fileBytes);

            // 5. Call Gemini API using standard java.net.http.HttpClient
            HttpClient client = HttpClient.newHttpClient();
            
            // Build Gemini request body payload with prompt and inlineData file
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);
            
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", file.getType());
            inlineData.put("data", base64Data);

            Map<String, Object> filePart = new HashMap<>();
            filePart.put("inlineData", inlineData);
            
            Map<String, Object> contentNode = new HashMap<>();
            contentNode.put("parts", new Object[]{textPart, filePart});
            
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("responseMimeType", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Object[]{contentNode});
            requestBody.put("generationConfig", generationConfig);

            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();

            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                log.error("AI API returned status code {}: {}", httpResponse.statusCode(), httpResponse.body());
                extractionRequest.setStatus(ExtractionStatus.AI_ERROR);
                extractionRequest.setRawAiResponse(httpResponse.body());
                extractionRequestRepository.save(extractionRequest);
                throw new RuntimeException("Failed to call AI API. Status code: " + httpResponse.statusCode());
            }

            aiResponseText = httpResponse.body();
            extractionRequest.setRawAiResponse(aiResponseText);

            // Parse Gemini response
            JsonNode rootNode = objectMapper.readTree(aiResponseText);
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isMissingNode() || candidates.isEmpty()) {
                throw new RuntimeException("No candidates returned from Gemini API");
            }
            JsonNode candidateNode = candidates.get(0);
            String extractedJsonText = candidateNode.path("content").path("parts").get(0).path("text").asText();

            if (extractedJsonText == null || extractedJsonText.trim().isEmpty()) {
                throw new RuntimeException("Empty response content returned from Gemini API");
            }

            // 6. Validate/Parse the extracted JSON to make sure it's valid JSON
            JsonNode parsedExtractedJson;
            try {
                parsedExtractedJson = objectMapper.readTree(extractedJsonText);
            } catch (Exception e) {
                extractionRequest.setStatus(ExtractionStatus.PARSE_ERROR);
                extractionRequestRepository.save(extractionRequest);
                throw new RuntimeException("Failed to parse AI output as JSON: " + extractedJsonText, e);
            }

            // 7. Validate required fields
            for (var field : template.getFields()) {
                if (Boolean.TRUE.equals(field.getRequired())) {
                    JsonNode val = parsedExtractedJson.path(field.getCode());
                    if (val.isMissingNode() || val.isNull()) {
                        extractionRequest.setStatus(ExtractionStatus.VALIDATION_ERROR);
                        extractionRequestRepository.save(extractionRequest);
                        throw new RuntimeException("Required field validation failed. Missing field: " + field.getCode());
                    }
                }
            }

            // 8. Success: Save extraction result
            extractionRequest.setStatus(ExtractionStatus.SUCCESS);
            extractionRequestRepository.save(extractionRequest);

            ExtractionResult result = new ExtractionResult();
            result.setRequest(extractionRequest);
            result.setData(objectMapper.writeValueAsString(parsedExtractedJson));
            result.setProcessTimeMs((int) (System.currentTimeMillis() - startTime));
            extractionResultRepository.save(result);

            return result.getData();

        } catch (Exception e) {
            log.error("Error during document extraction", e);
            if (extractionRequest.getStatus() == ExtractionStatus.AI_ERROR && aiResponseText != null) {
                // Keep AI_ERROR or set accordingly
            } else if (extractionRequest.getStatus() == ExtractionStatus.AI_ERROR) {
                extractionRequest.setStatus(ExtractionStatus.AI_ERROR);
                extractionRequestRepository.save(extractionRequest);
            }
            throw new RuntimeException("Extraction failed: " + e.getMessage(), e);
        }
    }

    public java.util.List<ExtractionRequest> getAllExtractions() {
        return extractionRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public byte[] downloadFile(java.util.UUID fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));
        return minioStorageService.downloadFile(file.getStoragePath());
    }

    public File getFileMetadata(java.util.UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + fileId));
    }

    public java.util.Optional<ExtractionResult> getExtractionResultByRequestId(java.util.UUID requestId) {
        return extractionResultRepository.findByRequestId(requestId);
    }
}

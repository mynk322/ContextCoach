package com.contextcoach.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FileService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final ObjectMapper objectMapper;
    
    public FileService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Extracts text content from various file formats
     * 
     * @param file The uploaded file
     * @return The extracted text content
     * @throws IOException If there's an error reading the file
     */
    public String extractTextFromFile(MultipartFile file) throws IOException {
        if (file == null) {
            logger.error("File is null");
            throw new IOException("File is null");
        }
        
        logger.info("Extracting text from file: {}", file.getOriginalFilename());
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        
        if (fileName == null || contentType == null) {
            logger.error("Invalid file: filename or content type is null");
            throw new IOException("Invalid file");
        }
        
        // Handle different file types
        try {
            String result;
            if (contentType.contains("pdf")) {
                logger.debug("Processing PDF file");
                result = extractTextFromPdf(file);
            } else if (contentType.contains("text") || fileName.endsWith(".txt")) {
                logger.debug("Processing text file");
                result = new String(file.getBytes(), StandardCharsets.UTF_8);
            } else if (contentType.contains("json") || fileName.endsWith(".json")) {
                logger.debug("Processing JSON file");
                result = extractTextFromJson(file);
            } else if (contentType.contains("excel") || fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                logger.debug("Processing Excel file");
                result = extractTextFromExcel(file);
            } else if (contentType.contains("word") || fileName.endsWith(".docx") || fileName.endsWith(".doc")) {
                // For simplicity, we're not implementing Word document parsing here
                // In a real application, you would use Apache POI's XWPF or HWPF
                logger.error("Word document parsing not implemented");
                throw new IOException("Word document parsing not implemented");
            } else {
                logger.error("Unsupported file type: {}", contentType);
                throw new IOException("Unsupported file type: " + contentType);
            }
            
            logger.info("Successfully extracted text from file: {}", fileName);
            return result;
        } catch (IOException e) {
            logger.error("Error extracting text from file: {}", fileName, e);
            throw e;
        }
    }
    
    /**
     * Extracts text from a PDF file
     */
    private String extractTextFromPdf(MultipartFile file) throws IOException {
        logger.debug("Extracting text from PDF file: {}", file.getOriginalFilename());
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            logger.debug("Successfully extracted {} characters from PDF", text.length());
            return text;
        } catch (IOException e) {
            logger.error("Error extracting text from PDF file: {}", file.getOriginalFilename(), e);
            throw e;
        }
    }
    
    /**
     * Extracts text from a JSON file
     */
    private String extractTextFromJson(MultipartFile file) throws IOException {
        logger.debug("Extracting text from JSON file: {}", file.getOriginalFilename());
        try {
            // Parse JSON to extract relevant fields
            Map<String, Object> jsonMap = objectMapper.readValue(file.getInputStream(), 
                                          new TypeReference<Map<String, Object>>() {});
            
            StringBuilder textBuilder = new StringBuilder();
            
            // Extract text from common fields that might contain requirements
            extractJsonField(jsonMap, "title", textBuilder);
            extractJsonField(jsonMap, "description", textBuilder);
            extractJsonField(jsonMap, "requirements", textBuilder);
            extractJsonField(jsonMap, "content", textBuilder);
            extractJsonField(jsonMap, "text", textBuilder);
            
            // If no specific fields were found, use the entire JSON as text
            if (textBuilder.length() == 0) {
                logger.debug("No specific fields found in JSON, using entire content");
                return objectMapper.writeValueAsString(jsonMap);
            }
            
            logger.debug("Successfully extracted {} characters from JSON", textBuilder.length());
            return textBuilder.toString();
        } catch (IOException e) {
            logger.error("Error extracting text from JSON file: {}", file.getOriginalFilename(), e);
            throw e;
        }
    }
    
    /**
     * Helper method to extract a field from JSON
     */
    private void extractJsonField(Map<String, Object> jsonMap, String fieldName, StringBuilder textBuilder) {
        if (jsonMap.containsKey(fieldName)) {
            logger.trace("Found field '{}' in JSON", fieldName);
            Object value = jsonMap.get(fieldName);
            if (value != null) {
                if (textBuilder.length() > 0) {
                    textBuilder.append("\n\n");
                }
                String valueStr = value.toString();
                textBuilder.append(valueStr);
                logger.trace("Extracted {} characters from field '{}'", valueStr.length(), fieldName);
            } else {
                logger.trace("Field '{}' has null value", fieldName);
            }
        } else {
            logger.trace("Field '{}' not found in JSON", fieldName);
        }
    }
    
    /**
     * Extracts text from an Excel file
     */
    private String extractTextFromExcel(MultipartFile file) throws IOException {
        logger.debug("Extracting text from Excel file: {}", file.getOriginalFilename());
        StringBuilder textBuilder = new StringBuilder();
        
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            logger.debug("Excel file has {} sheets", workbook.getNumberOfSheets());
            
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                logger.debug("Processing sheet: {}", sheet.getSheetName());
                
                if (textBuilder.length() > 0) {
                    textBuilder.append("\n\n");
                }
                textBuilder.append("Sheet: ").append(sheet.getSheetName()).append("\n");
                
                int rowCount = 0;
                for (Row row : sheet) {
                    StringBuilder rowText = new StringBuilder();
                    for (Cell cell : row) {
                        String cellValue;
                        switch (cell.getCellType()) {
                            case STRING:
                                cellValue = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                cellValue = String.valueOf(cell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                cellValue = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                cellValue = cell.getCellFormula();
                                break;
                            default:
                                cellValue = "";
                        }
                        
                        if (!cellValue.isEmpty()) {
                            if (rowText.length() > 0) {
                                rowText.append("\t");
                            }
                            rowText.append(cellValue);
                        }
                    }
                    
                    if (rowText.length() > 0) {
                        textBuilder.append(rowText).append("\n");
                        rowCount++;
                    }
                }
                logger.debug("Processed {} non-empty rows in sheet: {}", rowCount, sheet.getSheetName());
            }
            
            String result = textBuilder.toString();
            logger.debug("Successfully extracted {} characters from Excel file", result.length());
            return result;
        } catch (IOException e) {
            logger.error("Error extracting text from Excel file: {}", file.getOriginalFilename(), e);
            throw e;
        }
    }
}

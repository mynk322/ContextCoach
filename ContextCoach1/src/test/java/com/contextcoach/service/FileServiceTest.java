package com.contextcoach.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testExtractTextFromFile_TextFile() throws IOException {
        // Arrange
        String content = "This is a test text file content";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                content.getBytes(StandardCharsets.UTF_8));

        // Act
        String result = fileService.extractTextFromFile(file);

        // Assert
        assertEquals(content, result);
    }

    @Test
    void testExtractTextFromFile_JsonFile() throws IOException {
        // Arrange
        String jsonContent = "{\"title\":\"Test Title\",\"description\":\"Test Description\"}";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                jsonContent.getBytes(StandardCharsets.UTF_8));

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("title", "Test Title");
        jsonMap.put("description", "Test Description");

        when(objectMapper.readValue(any(InputStream.class), eq(HashMap.class))).thenReturn(jsonMap);

        // Act
        String result = fileService.extractTextFromFile(file);

        // Assert
        assertTrue(result.contains("Test Title"));
        assertTrue(result.contains("Test Description"));
        verify(objectMapper).readValue(any(InputStream.class), eq(HashMap.class));
    }

    @Test
    void testExtractTextFromFile_JsonFileNoSpecificFields() throws IOException {
        // Arrange
        String jsonContent = "{\"someField\":\"Some Value\"}";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                jsonContent.getBytes(StandardCharsets.UTF_8));

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("someField", "Some Value");

        when(objectMapper.readValue(any(InputStream.class), eq(HashMap.class))).thenReturn(jsonMap);
        when(objectMapper.writeValueAsString(jsonMap)).thenReturn(jsonContent);

        // Act
        String result = fileService.extractTextFromFile(file);

        // Assert
        assertEquals(jsonContent, result);
        verify(objectMapper).readValue(any(InputStream.class), eq(HashMap.class));
        verify(objectMapper).writeValueAsString(jsonMap);
    }

    @Test
    void testExtractTextFromFile_PdfFile() throws IOException {
        // Create a simple PDF document
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Test PDF Content");
                contentStream.endText();
            }
            
            document.save(baos);
        }
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                baos.toByteArray());
        
        // Act
        String result = fileService.extractTextFromFile(file);
        
        // Assert
        assertTrue(result.contains("Test PDF Content"));
    }

    @Test
    void testExtractTextFromFile_ExcelFile() throws IOException {
        // Create a simple Excel workbook
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TestSheet");
            Row headerRow = sheet.createRow(0);
            Cell headerCell1 = headerRow.createCell(0);
            headerCell1.setCellValue("Header1");
            Cell headerCell2 = headerRow.createCell(1);
            headerCell2.setCellValue("Header2");
            
            Row dataRow = sheet.createRow(1);
            Cell dataCell1 = dataRow.createCell(0);
            dataCell1.setCellValue("Data1");
            Cell dataCell2 = dataRow.createCell(1);
            dataCell2.setCellValue("Data2");
            
            Row numericRow = sheet.createRow(2);
            Cell numericCell = numericRow.createCell(0);
            numericCell.setCellValue(123.45);
            
            Row booleanRow = sheet.createRow(3);
            Cell booleanCell = booleanRow.createCell(0);
            booleanCell.setCellValue(true);
            
            workbook.write(baos);
        }
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                baos.toByteArray());
        
        // Act
        String result = fileService.extractTextFromFile(file);
        
        // Assert
        assertTrue(result.contains("TestSheet"));
        assertTrue(result.contains("Header1"));
        assertTrue(result.contains("Data1"));
        assertTrue(result.contains("123.45"));
        assertTrue(result.contains("true"));
    }

    @Test
    void testExtractTextFromFile_WordDocument() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "test data".getBytes());

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> fileService.extractTextFromFile(file));
        assertEquals("Word document parsing not implemented", exception.getMessage());
    }

    @Test
    void testExtractTextFromFile_UnsupportedFileType() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "test data".getBytes());

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> fileService.extractTextFromFile(file));
        assertTrue(exception.getMessage().startsWith("Unsupported file type:"));
    }

    @Test
    void testExtractTextFromFile_NullFileName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                "text/plain",
                "test data".getBytes());

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> fileService.extractTextFromFile(file));
        assertEquals("Invalid file", exception.getMessage());
    }
    
    @Test
    void testExtractTextFromFile_NullFile() {
        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> fileService.extractTextFromFile(null));
        assertEquals("File is null", exception.getMessage());
    }

    @Test
    void testExtractTextFromFile_NullContentType() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                null,
                "test data".getBytes());

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> fileService.extractTextFromFile(file));
        assertEquals("Invalid file", exception.getMessage());
    }

    @Test
    void testExtractTextFromPdf_IOException() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "invalid pdf content".getBytes());

        // Act & Assert
        assertThrows(IOException.class, () -> fileService.extractTextFromFile(file));
    }

    @Test
    void testExtractTextFromJson_IOException() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                "invalid json content".getBytes());

        when(objectMapper.readValue(any(InputStream.class), eq(HashMap.class)))
                .thenThrow(new IOException("JSON parsing error"));

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> fileService.extractTextFromFile(file));
        assertEquals("JSON parsing error", exception.getMessage());
    }

    @Test
    void testExtractTextFromExcel_IOException() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "invalid excel content".getBytes());

        // Act & Assert
        assertThrows(IOException.class, () -> fileService.extractTextFromFile(file));
    }
}

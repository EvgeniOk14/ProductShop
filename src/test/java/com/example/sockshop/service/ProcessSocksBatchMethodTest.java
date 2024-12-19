package com.example.sockshop.service;

import com.example.sockshop.exceptions.custom.NullArgException;
import com.example.sockshop.repository.SockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessSocksBatchMethodTest
{
    @Mock
    private SockRepository sockRepository; // Мок репозитория

    @InjectMocks
    private SockService sockService; // Тестируемый сервис

    @Mock
    private MultipartFile file; // Мок файла

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }


    @Test
    void processSocksBatch_EmptyFile_ThrowsNullArgException() throws Exception
    {
        // Arrange
        when(file.isEmpty()).thenReturn(true);

        // Act & Assert
        NullArgException exception = assertThrows(NullArgException.class, () -> sockService.processSocksBatch(file));
        assertEquals("В метод передан файл с пустым содержанием ! ", exception.getMessage());
    }

    @Test
    void processSocksBatch_InvalidFileFormat_ThrowsIllegalArgumentException() throws Exception
    {
        // Arrange
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("socks.txt");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> sockService.processSocksBatch(file));
        assertEquals("Некорректный формат файла. Пожалуйста, загрузите файл формата - Excel!", exception.getMessage());
    }
}



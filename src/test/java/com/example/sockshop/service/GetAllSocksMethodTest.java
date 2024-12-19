package com.example.sockshop.service;

import com.example.sockshop.exceptions.custom.DatabaseException;
import com.example.sockshop.exceptions.custom.NotFoundItemException;
import com.example.sockshop.models.Socks;
import com.example.sockshop.repository.SockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.persistence.PersistenceException;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllSocksMethodTest
{
    @Mock
    private SockRepository sockRepository; // Мок репозитория

    @InjectMocks
    private SockService sockService; // Тестируемый сервис

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }

    @Test
    void getAllSocks_ReturnsListOfSocks()
    {
        // Arrange
        Socks sock1 = new Socks(/* параметры конструктора */);
        Socks sock2 = new Socks(/* параметры конструктора */);
        List<Socks> expectedSocks = Arrays.asList(sock1, sock2);

        when(sockRepository.findAll()).thenReturn(expectedSocks); // Настройка поведения моков

        // Act
        List<Socks> actualSocks = sockService.getAllSocks();

        // Assert
        assertEquals(expectedSocks, actualSocks); // Проверка, что результат соответствует ожиданиям
        verify(sockRepository, times(1)).findAll(); // Проверка, что метод findAll был вызван один раз
    }

    @Test
    void getAllSocks_NotFoundItemExceptionThrown()
    {
        // Arrange
        when(sockRepository.findAll()).thenThrow(new NotFoundItemException("No items found")); // Настройка поведения моков

        // Act & Assert
        NotFoundItemException exception = assertThrows(NotFoundItemException.class, () -> sockService.getAllSocks());
        assertEquals("No items found", exception.getMessage()); // Проверка сообщения исключения
        verify(sockRepository, times(1)).findAll(); // Проверка, что метод findAll был вызван один раз
    }

    @Test
    void getAllSocks_PersistenceExceptionThrown_ThrowsDatabaseException()
    {
        // Arrange
        when(sockRepository.findAll()).thenThrow(new PersistenceException("Database error")); // Настройка поведения моков

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> sockService.getAllSocks());
        assertEquals("Ошибка при выполнении запроса к базе данных", exception.getMessage()); // Проверка сообщения исключения
        verify(sockRepository, times(1)).findAll(); // Проверка, что метод findAll был вызван один раз
    }
}


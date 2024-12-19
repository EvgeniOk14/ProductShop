package com.example.sockshop.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.example.sockshop.exceptions.custom.DatabaseException;
import com.example.sockshop.exceptions.custom.NotFoundItemException;
import com.example.sockshop.models.Socks;
import com.example.sockshop.repository.CustomSocksRepository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GetSocksByCottonPercentageRangeMethodTest
{
    @InjectMocks
    private SockService sockService; // Замените на ваш класс, где находится метод getSocksByCottonPercentageRange

    @Mock
    private CustomSocksRepository customSocksRepository; // Замените на ваш репозиторий

    private Socks sock1;
    private Socks sock2;

    @BeforeEach
    public void setUp() {
        sock1 = new Socks(); // Создайте объект носок
        sock1.setColor("Red");
        sock1.setCottonPercentage(80);

        sock2 = new Socks(); // Создайте другой объект носок
        sock2.setColor("Blue");
        sock2.setCottonPercentage(90);
    }

    @Test
    public void testGetSocksByCottonPercentageRange_ValidRange_ReturnsSocksList() {
        // Arrange
        Integer minCottonPercentage = 70;
        Integer maxCottonPercentage = 90;

        List<Socks> expectedSocks = Arrays.asList(sock1, sock2);

        when(customSocksRepository.findSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage))
                .thenReturn(expectedSocks);

        // Act
        List<Socks> result = sockService.getSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(sock1));
        assertTrue(result.contains(sock2));
    }

    @Test
    public void testGetSocksByCottonPercentageRange_InvalidRange_ThrowsIllegalArgumentException() {
        // Arrange
        Integer minCottonPercentage = 90;
        Integer maxCottonPercentage = 80; // Некорректный диапазон

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sockService.getSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage);
        });

        assertEquals("Минимальный процент содержания хлопка не может быть больше максимального!", exception.getMessage());
    }

    @Test
    public void testGetSocksByCottonPercentageRange_NullValues_ThrowsIllegalArgumentException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sockService.getSocksByCottonPercentageRange(null, null);
        });

        assertEquals("Минимальный и максимальный процент содержания хлопка не могут быть null!", exception.getMessage());
    }

    @Test
    public void testGetSocksByCottonPercentageRange_NegativeValues_ThrowsIllegalArgumentException() {
        // Arrange
        Integer minCottonPercentage = -10;
        Integer maxCottonPercentage = 50;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sockService.getSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage);
        });

        assertEquals("Процент содержания хлопка не может быть отрицательным!", exception.getMessage());
    }

    @Test
    public void testGetSocksByCottonPercentageRange_NoSocksFound_ThrowsNotFoundItemException() {
        // Arrange
        Integer minCottonPercentage = 50;
        Integer maxCottonPercentage = 100;

        when(customSocksRepository.findSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage))
                .thenThrow(new NotFoundItemException("No socks found"));

        // Act & Assert
        Exception exception = assertThrows(NotFoundItemException.class, () -> {
            sockService.getSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage);
        });

        assertEquals("No socks found", exception.getMessage());
    }

    @Test
    public void testGetSocksByCottonPercentageRange_DatabaseError_ThrowsDatabaseException() {
        // Arrange
        Integer minCottonPercentage = 30;
        Integer maxCottonPercentage = 70;

        when(customSocksRepository.findSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage))
                .thenThrow(new PersistenceException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(DatabaseException.class, () -> {
            sockService.getSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage);
        });

        assertEquals("Ошибка при выполнении запроса к базе данных", exception.getMessage());
    }
}

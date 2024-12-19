package com.example.sockshop.service;

import com.example.sockshop.exceptions.custom.DatabaseException;
import com.example.sockshop.exceptions.custom.NullArgException;
import com.example.sockshop.models.Socks;
import com.example.sockshop.repository.SockRepository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSocksMethod
{
    @Mock
    private SockRepository sockRepository; // Мок репозитория

    @InjectMocks
    private SockService sockService; // Тестируемый сервис

    private Socks existingSock;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this); // Инициализация моков
        existingSock = new Socks("Red", 50, 10); // Создание объекта носков с использованием конструктора
        // Предположим, что идентификатор устанавливается в базе данных, поэтому не устанавливаем его здесь
    }

    @Test
    void updateSocks_ValidInput_UpdatesSuccessfully()
    {
        // Arrange
        Socks updatedSocks = new Socks("Blue", 70, 20); // Создание объекта носков с новыми значениями

        // Настройка поведения моков
        when(sockRepository.findById(anyLong())).thenReturn(java.util.Optional.of(existingSock));

        // Act
        sockService.updateSocks(1L, updatedSocks);

        // Assert
        verify(sockRepository, times(1)).findById(1L); // Проверка, что findById был вызван один раз
        verify(sockRepository, times(1)).save(existingSock); // Проверка, что save был вызван один раз
        assertEquals("Blue", existingSock.getColor()); // Проверка, что цвет обновлен
        assertEquals(70, existingSock.getCottonPercentage()); // Проверка, что процент хлопка обновлен
        assertEquals(20, existingSock.getQuantity()); // Проверка, что количество обновлено
    }

    @Test
    void updateSocks_NullSocks_ThrowsNullArgException()
    {
        // Act & Assert
        NullArgException exception = assertThrows(NullArgException.class, () -> sockService.updateSocks(1L, null));
        assertEquals("В метод передан аргумент равный null ! ", exception.getMessage()); // Проверка сообщения исключения
    }

    @Test
    void updateSocks_NegativeId_ThrowsNullArgException()
    {
        // Act & Assert
        NullArgException exception = assertThrows(NullArgException.class, () -> sockService.updateSocks(-1L, existingSock));
        assertEquals("Параметр id должен быть больше нуля! ! ", exception.getMessage()); // Проверка сообщения исключения
    }

    @Test
    void updateSocks_SockNotFound_ThrowsRuntimeException()
    {
        // Arrange
        Socks updatedSocks = new Socks("Blue", 70, 20); // Создание объекта носков с новыми значениями

        when(sockRepository.findById(1L)).thenReturn(java.util.Optional.empty()); // Настройка поведения моков

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> sockService.updateSocks(1L, updatedSocks));
        assertEquals("Носки не найдены", exception.getMessage()); // Проверка сообщения исключения
    }

    @Test
    void updateSocks_PersistenceExceptionThrown_ThrowsDatabaseException()
    {
        // Arrange
        Socks updatedSocks = new Socks("Blue", 70, 20); // Создание объекта носков с новыми значениями

        when(sockRepository.findById(1L)).thenReturn(java.util.Optional.of(existingSock)); // Настройка поведения моков
        doThrow(new PersistenceException("Database error")).when(sockRepository).save(existingSock); // Настройка поведения моков

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> sockService.updateSocks(1L, updatedSocks));
        assertEquals("Ошибка при выполнении запроса к базе данных", exception.getMessage()); // Проверка сообщения исключения
    }
}

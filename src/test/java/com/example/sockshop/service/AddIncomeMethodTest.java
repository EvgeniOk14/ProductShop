package com.example.sockshop.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.example.sockshop.exceptions.custom.NullArgException;
import com.example.sockshop.models.Socks;
import com.example.sockshop.repository.SockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class AddIncomeMethodTest
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
    void addIncome_NullSocks_ThrowsNullArgException()
    {
        // Arrange
        Socks socks = null;

        // Act & Assert
        Exception exception = assertThrows(NullArgException.class, () -> sockService.addIncome(socks));
        assertEquals("В метод передан аргумент равный null ! ", exception.getMessage());
    }

    @Test
    void addIncome_QuantityLessThanOrEqualToZero_ThrowsIllegalArgumentException()
    {
        // Arrange
        Socks socks = new Socks();
        socks.setQuantity(0); // Устанавливаем количество <= 0

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> sockService.addIncome(socks));
        assertEquals("Количество носков должно быть больше нуля! ", exception.getMessage());
    }

    @Test
    void addIncome_ExistingSocks_UpdatesQuantity()
    {
        // Arrange
        Socks existingSock = new Socks();
        existingSock.setColor("Red");
        existingSock.setCottonPercentage(80);
        existingSock.setQuantity(5);

        Socks incomingSock = new Socks();
        incomingSock.setColor("Red");
        incomingSock.setCottonPercentage(80);
        incomingSock.setQuantity(3);

        when(sockRepository.findByColorAndCottonPercentage("Red", 80)).thenReturn(Collections.singletonList(existingSock));

        // Act
        sockService.addIncome(incomingSock);

        // Assert
        assertEquals(8, existingSock.getQuantity()); // Проверяем, что количество обновлено
        verify(sockRepository, times(1)).save(existingSock); // Проверяем, что save() был вызван
    }

    @Test
    void addIncome_NewSocks_SavesNewEntry()
    {
        // Arrange
        Socks incomingSock = new Socks();
        incomingSock.setColor("Blue");
        incomingSock.setCottonPercentage(100);
        incomingSock.setQuantity(4);

        when(sockRepository.findByColorAndCottonPercentage("Blue", 100)).thenReturn(Collections.emptyList());

        // Act
        sockService.addIncome(incomingSock);

        // Assert
        verify(sockRepository, times(1)).save(incomingSock); // Проверяем, что save() был вызван для новых носков
    }

    // Добавьте дополнительные тесты для обработки исключений, если необходимо
}

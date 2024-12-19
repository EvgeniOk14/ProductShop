package com.example.sockshop.service;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddOutcomeMethodTest
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
    void addOutcome_NullSocks_ThrowsNullArgException()
    {
        // Arrange
        Socks socks = null;

        // Act & Assert
        Exception exception = assertThrows(NullArgException.class, () -> sockService.addOutcome(socks));
        assertEquals("В метод передан аргумент равный null ! ", exception.getMessage());
    }

    @Test
    void addOutcome_QuantityLessThanOrEqualToZero_ThrowsIllegalArgumentException()
    {
        // Arrange
        Socks socks = new Socks();
        socks.setQuantity(0); // Устанавливаем количество <= 0

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> sockService.addOutcome(socks));
        assertEquals("Количество носков должно быть больше нуля! ", exception.getMessage());
    }

    @Test
    void addOutcome_ExistingSocks_SufficientQuantity_UpdatesQuantity()
    {
        // Arrange
        Socks existingSock = new Socks();
        existingSock.setColor("Red");
        existingSock.setCottonPercentage(80);
        existingSock.setQuantity(5);

        Socks outgoingSock = new Socks();
        outgoingSock.setColor("Red");
        outgoingSock.setCottonPercentage(80);
        outgoingSock.setQuantity(3);

        when(sockRepository.findByColorAndCottonPercentage("Red", 80)).thenReturn(Collections.singletonList(existingSock));

        // Act
        sockService.addOutcome(outgoingSock);

        // Assert
        assertEquals(2, existingSock.getQuantity()); // Проверяем, что количество обновлено
        verify(sockRepository, times(1)).save(existingSock); // Проверяем, что save() был вызван
        verify(sockRepository, times(0)).delete(existingSock); // Проверяем, что delete() не был вызван
    }

    @Test
    void addOutcome_ExistingSocks_ExactQuantity_DeletesSock()
    {
        // Arrange
        Socks existingSock = new Socks();
        existingSock.setColor("Green");
        existingSock.setCottonPercentage(100);
        existingSock.setQuantity(3);

        Socks outgoingSock = new Socks();
        outgoingSock.setColor("Green");
        outgoingSock.setCottonPercentage(100);
        outgoingSock.setQuantity(3);

        when(sockRepository.findByColorAndCottonPercentage("Green", 100)).thenReturn(Collections.singletonList(existingSock));

        // Act
        sockService.addOutcome(outgoingSock);

        // Assert
        verify(sockRepository, times(1)).delete(existingSock); // Проверяем, что delete() был вызван
        verify(sockRepository, times(0)).save(existingSock); // Проверяем, что save() не был вызван
    }

    @Test
    void addOutcome_ExistingSocks_InsufficientQuantity_ThrowsRuntimeException()
    {
        // Arrange
        Socks existingSock = new Socks();
        existingSock.setColor("Yellow");
        existingSock.setCottonPercentage(50);
        existingSock.setQuantity(2);

        Socks outgoingSock = new Socks();
        outgoingSock.setColor("Yellow");
        outgoingSock.setCottonPercentage(50);
        outgoingSock.setQuantity(3); // Запрашиваемое количество больше доступного

        when(sockRepository.findByColorAndCottonPercentage("Yellow", 50)).thenReturn(Collections.singletonList(existingSock));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> sockService.addOutcome(outgoingSock));
        assertEquals("Not enough socks available.", exception.getMessage());
    }

    @Test
    void addOutcome_NonExistingSocks_ThrowsRuntimeException()
    {
        // Arrange
        Socks outgoingSock = new Socks();
        outgoingSock.setColor("Blue");
        outgoingSock.setCottonPercentage(100);
        outgoingSock.setQuantity(1);

        when(sockRepository.findByColorAndCottonPercentage("Blue", 100)).thenReturn(Collections.emptyList());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> sockService.addOutcome(outgoingSock));
        assertEquals("По вашему запросу носков не найдено.", exception.getMessage());
    }

    // Дополнительные тесты для обработки исключений, если необходимо
}

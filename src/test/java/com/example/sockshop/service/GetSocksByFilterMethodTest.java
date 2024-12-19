package com.example.sockshop.service;

import static org.junit.jupiter.api.Assertions.*;
import com.example.sockshop.exceptions.custom.NullArgException;
import com.example.sockshop.models.Socks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class GetSocksByFilterMethodTest
{

    @InjectMocks
    private SockService sockService; // Замените на ваш класс, где находится метод getSocksByFilter

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
    public void testGetSocksByFilter_NoFilters_ThrowsNullArgException()
    {
        // Act & Assert
        Exception exception = assertThrows(NullArgException.class, () -> {
            sockService.getSocksByFilter(null, null, null);
        });

        assertEquals("Необходимо указать хотя бы один фильтр: цвет, тип сравнения или процент содержания хлопка!", exception.getMessage());
    }
}


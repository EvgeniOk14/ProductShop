package com.example.sockshop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.sockshop.models.Socks;
import com.example.sockshop.repository.SockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SortSocksMethodTest
{
    @Mock
    private SockRepository sockRepository;

    @InjectMocks
    private SockService sockService;
    private Socks sock1;
    private Socks sock2;
    private Socks sock3;

    @BeforeEach
    public void setUp()
    {
        sock1 = new Socks("Red", 70, 10);
        sock2 = new Socks("Blue", 100, 20);
        sock3 = new Socks("Green", 50, 15);
    }

    @Test
    public void testSortSocks_InvalidSortBy_ThrowsIllegalArgumentException()
    {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            sockService.sortSocks("invalidField", true);
        });

        String expectedMessage = "Вы ввели не корректный аргумент invalidField";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testSortSocks_SortByColorAscending_ReturnsSortedSocks()
    {
        List<Socks> socksList = Arrays.asList(sock2, sock1, sock3);
        when(sockRepository.findAllByOrderByColorAsc()).thenReturn(Arrays.asList(sock1, sock2, sock3));

        List<Socks> result = sockService.sortSocks("color", true);

        assertEquals(3, result.size());
        assertEquals(sock1, result.get(0)); // Red
        assertEquals(sock2, result.get(1)); // Blue
        assertEquals(sock3, result.get(2)); // Green
    }

    @Test
    public void testSortSocks_SortByColorDescending_ReturnsSortedSocks()
    {
        when(sockRepository.findAllByOrderByColorDesc()).thenReturn(Arrays.asList(sock3, sock1, sock2));

        List<Socks> result = sockService.sortSocks("color", false);

        assertEquals(3, result.size());
        assertEquals(sock3, result.get(0)); // Green
        assertEquals(sock1, result.get(1)); // Red
        assertEquals(sock2, result.get(2)); // Blue
    }

    @Test
    public void testSortSocks_SortByCottonPercentageAscending_ReturnsSortedSocks()
    {
        when(sockRepository.findAllByOrderByCottonPercentageAsc()).thenReturn(Arrays.asList(sock1, sock3, sock2));

        List<Socks> result = sockService.sortSocks("cottonPercentage", true);

        assertEquals(3, result.size());
        assertEquals(sock1, result.get(0)); // 70
        assertEquals(sock3, result.get(1)); // 50
        assertEquals(sock2, result.get(2)); // 100
    }

    @Test
    public void testSortSocks_SortByCottonPercentageDescending_ReturnsSortedSocks()
    {
        when(sockRepository.findAllByOrderByCottonPercentageDesc()).thenReturn(Arrays.asList(sock2, sock1, sock3));

        List<Socks> result = sockService.sortSocks("cottonPercentage", false);

        assertEquals(3, result.size());
        assertEquals(sock2, result.get(0)); // 100
        assertEquals(sock1, result.get(1)); // 70
        assertEquals(sock3, result.get(2)); // 50
    }
}


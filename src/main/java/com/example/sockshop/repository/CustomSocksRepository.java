package com.example.sockshop.repository;

import com.example.sockshop.models.Socks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Интерфейс CustomSocksRepository:
 *
 * Определяет пользовательский репозиторий для работы с сущностью {@link Socks}.
 * Предоставляет методы для выполнения операций поиска носков с использованием различных фильтров.
 *
 * Основные особенности:
 * - Позволяет фильтровать носки по цвету и проценту хлопка.
 * - Поддерживает пагинацию результатов поиска.
 * - Находит носки по заданному диапазону содержания хлопка.
 *
 * Методы:
 * - {@link #findSocksByFilters(String, Integer, Pageable)}:
 *   Находит носки по заданным фильтрам и возвращает результат в виде страницы.
 * - {@link #findSocksByCottonPercentageRange(Integer minCottonPercentage, Integer maxCottonPercentage)}:
 *  *   Находит носки по заданному диапазону содержания хлопка.
 */
public interface CustomSocksRepository
{
    /**
     * Находит носки по заданным фильтрам.
     *
     * @param color цвет носков, по которому будет выполнен фильтр. Может быть null или пустым.
     * @param cottonPercentage процент хлопка в носках. Может быть null.
     * @param pageable объект для пагинации результатов.
     * @return страница носков, соответствующих заданным фильтрам.
     */
    Page<Socks> findSocksByFilters(String color, Integer cottonPercentage, Pageable pageable);

    /**
     * Находит носки по заданному диапазону содержания хлопка.
     *
     * @param minCottonPercentage минимальный процент хлопка.
     * @param maxCottonPercentage максимальный процент хлопка.
     * @return список носков, соответствующих заданному диапазону.
     */
    List<Socks> findSocksByCottonPercentageRange(Integer minCottonPercentage, Integer maxCottonPercentage);


}

package com.example.sockshop.repository;

import com.example.sockshop.models.Socks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Интерфейс SockRepository:
 *
 * Представляет репозиторий для работы с сущностью Socks (носки).
 * Позволяет выполнять операции CRUD и дополнительные запросы к базе данных.
 *
 * Методы:
 * - {@link #findByColorAndCottonPercentage(String color, int cottonPercentage)}:
 *   Находит список носков по заданному цвету и проценту содержания хлопка.
 *
 * - {@link #findAllByOrderByColorAsc()}:
 *   Возвращает список носков, отсортированных по цвету в порядке возрастания.
 *
 * - {@link #findAllByOrderByColorDesc()}:
 *   Возвращает список носков, отсортированных по цвету в порядке убывания.
 *
 * - {@link #findAllByOrderByCottonPercentageAsc()}:
 *   Возвращает список носков, отсортированных по проценту хлопка в порядке возрастания.
 *
 * - {@link #findAllByOrderByCottonPercentageDesc()}:
 *   Возвращает список носков, отсортированных по проценту хлопка в порядке убывания.
 *
 * Аннотации:
 * - @Repository: помечает интерфейс как компонент Spring, который взаимодействует с базой данных.
 *
 * Наследование:
 * - Расширяет интерфейс JpaRepository, который предоставляет стандартные методы для работы с JPA.
 */
@Repository // Помечает интерфейс как репозиторий Spring
public interface SockRepository extends JpaRepository<Socks, Long>, CustomSocksRepository {

    /**
     * Метод findByColorAndCottonPercentage:
     * Находит список носков по заданному цвету и проценту содержания хлопка.
     *
     * @param color           цвет носков. // Цвет, по которому будет выполнен поиск.
     * @param cottonPercentage процент содержания хлопка в носках. // Процент хлопка для фильтрации носков.
     * @return список носков, соответствующих заданным критериям. // Возвращает список найденных носков.
     */
    List<Socks> findByColorAndCottonPercentage(String color, int cottonPercentage); // Метод для поиска носков по цвету и проценту хлопка

    /**
     * Метод findAllByOrderByColorAsc:
     * Возвращает список носков, отсортированных по цвету в порядке возрастания
     *
     * @return список носков, отсортированных по цвету. // Возвращает отсортированный по возрастанию список носков.
     */
    List<Socks> findAllByOrderByColorAsc(); // Для сортировки по цвету по возрастанию

    /**
     * Метод findAllByOrderByColorDesc:
     * Возвращает список носков, отсортированных по цвету в порядке убывания.
     *
     * @return список носков, отсортированных по цвету. // Возвращает отсортированный по убыванию список носков.
     */
    List<Socks> findAllByOrderByColorDesc(); // Для сортировки по цвету по убыванию

    /**
     * Метод findAllByOrderByCottonPercentageAsc:
     * Возвращает список носков, отсортированных по проценту хлопка в порядке возрастания.
     *
     * @return список носков, отсортированных по проценту хлопка. // Возвращает отсортированный по возрастанию список носков.
     */
    List<Socks> findAllByOrderByCottonPercentageAsc(); // Для сортировки по проценту хлопка по возрастанию

    /**
     * Метод findAllByOrderByCottonPercentageDesc:
     * Возвращает список носков, отсортированных по проценту хлопка в порядке убывания.
     *
     * @return список носков, отсортированных по проценту хлопка. // Возвращает отсортированный по убыванию список носков.
     */
    List<Socks> findAllByOrderByCottonPercentageDesc(); // Для сортировки по проценту хлопка по убыванию
}

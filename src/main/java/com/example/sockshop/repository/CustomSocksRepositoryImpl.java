package com.example.sockshop.repository;

import com.example.sockshop.models.Socks;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Класс CustomSocksRepositoryImpl:
 *
 * Реализация пользовательского репозитория для работы с сущностью {@link Socks}.
 * Предоставляет метод для поиска носков с использованием различных фильтров и пагинации.
 *
 * Основные особенности:
 * - Использует JPA Criteria API для динамического построения запросов.
 * - Поддерживает фильтрацию по цвету и проценту хлопка.
 * - Возвращает результаты с учетом пагинации.
 *
 * Переопределённые методы:
 * - {@link #findSocksByFilters(String, Integer, Pageable)}:
 *   Находит носки по заданным фильтрам и возвращает результат в виде страницы.
 * - {@link #findSocksByCottonPercentageRange(Integer minCottonPercentage, Integer maxCottonPercentage)}:
 *  *   Находит носки по заданному диапазону содержания хлопка.
 *
 * Аннотация:
 * - {@link Primary}: Указывает, что эта реализация является основной для инъекции зависимостей.
 * - {@link Repository}: Обозначает класс как компонент репозитория Spring.
 * - {@link PersistenceContext} Аннотация, указывающая на то, что данный объект будет управляться контейнером JPA.
 *    Она позволяет внедрять EntityManager, который используется для взаимодействия с контекстом персистентности.
 *    Контекст персистентности управляет жизненным циклом сущностей и обеспечивает доступ к базе данных.
 */
@Primary
@Repository
public class CustomSocksRepositoryImpl implements CustomSocksRepository
{
    //region Fields
    @PersistenceContext
    private EntityManager entityManager; // Менеджер сущностей для работы с базой данных
    //endRegion

    //region Methods
    /**
     * Метод findSocksByFilters:
     * Находит носки по заданным фильтрам и возвращает результаты с учетом пагинации.
     *
     * @param color цвет носков, по которому будет выполнен фильтр. Может быть null.
     * @param cottonPercentage процент хлопка в носках. Может быть null.
     * @param pageable объект для пагинации результатов.
     * @return страница носков, соответствующих заданным фильтрам.
     */
    @Override
    public Page<Socks> findSocksByFilters(String color, Integer cottonPercentage, Pageable pageable)
    {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder(); // Получаем построитель критериев для создания запросов

        CriteriaQuery<Socks> query = cb.createQuery(Socks.class); // Создаем запрос для получения подмножества носков

        Root<Socks> socksRoot = query.from(Socks.class); // Определяем корень запроса (таблицу) для выборки носков

        Predicate predicate = cb.conjunction(); // Создаем начальное условие для фильтрации


        if (color != null) // Добавляем условие по цвету, если оно не null
        {
            predicate = cb.and(predicate, cb.equal(socksRoot.get("color"), color)); // Условие равенства цвета
        }

        if (cottonPercentage != null)  // Добавляем условие по проценту хлопка, если оно не null
        {
            predicate = cb.and(predicate, cb.equal(socksRoot.get("cottonPercentage"), cottonPercentage)); // Условие равенства процента хлопка
        }

        query.where(predicate); // Применяем условия к запросу

        // Получаем список носков с учетом пагинации:
        List<Socks> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset()) // Устанавливаем начальный индекс для пагинации
                .setMaxResults(pageable.getPageSize()) // Устанавливаем максимальное количество результатов
                .getResultList(); // Выполняем запрос и получаем результаты


        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class); // Создаем запрос для подсчета общего количества носков
        countQuery.select(cb.count(countQuery.from(Socks.class))) // Подсчитываем количество носков
                .where(predicate); // Применяем те же условия фильтрации

        long total = entityManager.createQuery(countQuery).getSingleResult(); // Получаем общее количество

        return new PageImpl<>(resultList, pageable, total); // Возвращаем результаты в виде страницы с учетом пагинации
    }

    @Override
    public List<Socks> findSocksByCottonPercentageRange(Integer minCottonPercentage, Integer maxCottonPercentage) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder(); // Получаем построитель критериев для создания запросов

        CriteriaQuery<Socks> query = cb.createQuery(Socks.class); // Создаем объект CriteriaQuery для сущности Socks

        Root<Socks> socksRoot = query.from(Socks.class); // Определяем корень запроса, откуда будет производиться выборка носков

        // Создаем условие (Predicate) для фильтрации по проценту хлопка
        Predicate predicate = cb.and(
                // Условие: процент хлопка должен быть больше или равен минимальному значению
                cb.greaterThanOrEqualTo(socksRoot.get("cottonPercentage"), minCottonPercentage),
                // Условие: процент хлопка должен быть меньше или равен максимальному значению
                cb.lessThanOrEqualTo(socksRoot.get("cottonPercentage"), maxCottonPercentage)
        );

        query.where(predicate); // Применяем условия к запросу

        return entityManager.createQuery(query).getResultList(); // Выполняем запрос и возвращаем список носков, соответствующих заданному диапазону
    }
    //endRegion
}

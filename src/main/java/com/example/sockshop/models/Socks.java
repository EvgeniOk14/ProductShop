package com.example.sockshop.models;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Класс Socks:
 *
 * Представляет сущность носков в системе.
 * Носки включают в себя такие атрибуты,такие как:
 * - Уникальный идентификатор (id).
 * - Цвет носков (color).
 * - Процент содержания хлопка (cottonPercentage) в носках.
 * - Количество носков на складе (quantity).
 *
 * Связь с базой данных:
 * - Таблица "table_socks" хранит данные о носках.
 *
 * Аннотации:
 * - @Entity: обозначает класс как сущность JPA.
 * - @Table: задает имя таблицы в базе данных.
 * - @Id: помечает поле как первичный ключ.
 * - @GeneratedValue: указывает, что значение поля будет автоматически сгенерировано.
 */
@Entity // Обозначает класс как сущность JPA
@Table(name = "table_socks") // Задает имя таблицы в базе данных
public class Socks
{
    // region Fields
    /**
     * Уникальный идентификатор носков.
     * Связан с первичным ключом таблицы "table_socks".
     */
    @Id // Помечает поле как первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Значение будет автоматически сгенерировано
    private Long id; // Уникальный идентификатор носка

    /**
     * Цвет носков.
     * Поле "color" в таблице "table_socks".
     */
    private String color; // Цвет носков

    /**
     * Процент содержания хлопка в носках.
     * Поле "cotton_percentage" в таблице "table_socks".
     */
    private int cottonPercentage; // Процент содержания хлопка в носках

    /**
     * Количество носков на складе.
     * Поле "quantity" в таблице "table_socks".
     */
    private int quantity; // Количество носков
    // endRegion

    // region Constructors
    /**
     * Конструктор для создания носков с заданными параметрами.
     *
     * @param color цвет носков.
     * @param cottonPercentage процент содержания хлопка в носках.
     * @param quantity количество носков.
     */
    public Socks(String color, int cottonPercentage, int quantity)
    {
        this.color = color; // Устанавливаем цвет носков
        this.cottonPercentage = cottonPercentage; // Устанавливаем процент хлопка в носках
        this.quantity = quantity; // Устанавливаем количество носков
    }

    /**
     * Конструктор без параметров.
     * Используется для создания носков без параметров.
     */
    public Socks()
    {
        // default constructor
    }
    // endRegion

    // region Getters
    /**
     * Получает уникальный идентификатор носков.
     *
     * @return уникальный идентификатор носков.
     */
    public Long getId()
    {
        return id; // Возвращаем уникальный идентификатор
    }

    /**
     * Получает цвет носков.
     *
     * @return цвет носков.
     */
    public String getColor()
    {
        return color; // Возвращаем цвет носков
    }

    /**
     * Получает процент содержания хлопка в носках.
     *
     * @return процент содержания хлопка в носках.
     */
    public int getCottonPercentage()
    {
        return cottonPercentage; // Возвращаем процент содержания хлопка в носках
    }

    /**
     * Получает количество носков на складе.
     *
     * @return количество носков.
     */
    public int getQuantity()
    {
        return quantity; // Возвращаем количество носков
    }
    // endRegion

    // region Setters
    /**
     * Устанавливает цвет носков.
     *
     * @param color цвет носков.
     */
    public void setColor(String color)
    {
        this.color = color; // Устанавливаем цвет носков
    }

    /**
     * Устанавливает процент содержания хлопка в носках.
     *
     * @param cottonPercentage процент содержания хлопка в носках.
     */
    public void setCottonPercentage(int cottonPercentage)
    {
        this.cottonPercentage = cottonPercentage; // Устанавливаем процент хлопка в носках
    }

    /**
     * Устанавливает количество носков на складе.
     *
     * @param quantity количество носков.
     */
    public void setQuantity(int quantity)
    {
        this.quantity = quantity; // Устанавливаем количество носков
    }
    // endRegion

    // region Methods
    /**
     * Метод для сравнения двух носков.
     *
     * @param o объект для сравнения.
     * @return true, если объекты равны, иначе false.
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true; // Если ссылки на один и тот же объект, возвращаем true
        if (!(o instanceof Socks)) return false; // Если объект не является экземпляром Socks, возвращаем false
        Socks socks = (Socks) o; // Приводим объект к типу Socks
        return cottonPercentage == socks.cottonPercentage && // Сравниваем процент хлопка
                quantity == socks.quantity && // Сравниваем количество носков
                Objects.equals(color, socks.color); // Сравниваем цвет носков
    }

    /**
     * Метод для получения хеш-кода носков.
     *
     * @return хеш-код объекта.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(color, cottonPercentage, quantity); // Возвращаем хеш-код на основе цвета, процента хлопка и количества
    }
    // endRegion
}

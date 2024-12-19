package com.example.sockshop.exceptions.custom;

/**
 * Метод - NotFoundItemException
 * Исключение, возникающее при не нахождении данных в базе данных
 */
public class NotFoundItemException extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения NotFoundItemException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public NotFoundItemException(String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}

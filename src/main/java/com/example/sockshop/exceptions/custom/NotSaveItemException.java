package com.example.sockshop.exceptions.custom;

/**
 * Метод - NotSaveItemException
 * Исключение, возникающее невозможноссти сохранения данных в базе данных
 */
public class NotSaveItemException extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения NotSaveItemException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public NotSaveItemException(String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}

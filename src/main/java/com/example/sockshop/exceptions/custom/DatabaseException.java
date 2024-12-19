package com.example.sockshop.exceptions.custom;

/**
 * Метод - DatabaseException
 * Исключение, возникающее при не невозможности подключения к базе данных
 **/
public class DatabaseException extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения DatabaseException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public DatabaseException(String message) // Передаёт сообщение родительскому классу RuntimeException
    {
        super(message);
    }
    //endRegion
}

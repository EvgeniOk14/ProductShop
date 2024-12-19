package com.example.sockshop.exceptions.custom;

/**
 * Метод - NotDeleteException
 * Исключение, возникающее при не невозможности удаления данных из базы данных
 */
public class NotDeleteException extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения NotDeleteException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public NotDeleteException(String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}



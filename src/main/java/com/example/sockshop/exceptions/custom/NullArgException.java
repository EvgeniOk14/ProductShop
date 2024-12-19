package com.example.sockshop.exceptions.custom;


/**
 * Метод - NullArgException
 * Исключение, возникающее при передаче в качестве аргумента nuull
 */
public class NullArgException extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения NullArgException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public NullArgException(String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}

package com.example.sockshop.exceptions.custom;

/**
 * Метод - ExcelFileProcessingException
 * Исключение, выводящее информацию о проблеме с обработкой файла Excel.
 */
public class ExcelFileProcessingException extends RuntimeException
{
    //region Constructor
    /**
     * Конструктор для создания исключения ExcelFileProcessingException.
     *
     * @param message сообщение, которое будет передано пользователю в качестве причины исключения
     */
    public ExcelFileProcessingException(String message)
    {
        super(message); // Передаёт сообщение родительскому классу RuntimeException
    }
    //endRegion
}


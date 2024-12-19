package com.example.sockshop.exceptions.global;

import com.example.sockshop.exceptions.custom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Класс GlobalExceptionHandler:
 *
 * Глобальный обработчик исключений, предназначенный для обработки различных исключений, возникающих
 * в приложении. Реализует централизованное управление ошибками, возвращая соответствующий HTTP-статус
 * и сообщение об ошибке в ответ клиенту.
 *
 * Основные особенности:
 * - Логирование ошибок с использованием {@link Logger}.
 * - Возвращает корректные HTTP-статусы, соответствующие типу возникшей ошибки.
 * - Упрощает отладку и обработку ошибок на стороне клиента.
 *
 * Аннотация:
 * - {@link ControllerAdvice}: Указывает, что этот класс обрабатывает исключения на глобальном уровне
 *   для всех контроллеров приложения.
 *
 * Методы:
 * - {@link #handleNotFoundItemException(NotFoundItemException)}:
 *   Обрабатывает исключение {@link NotFoundItemException}, возвращая статус BAD_REQUEST.
 *
 * - {@link #handleNotSaveItemException(NotSaveItemException)}:
 *   Обрабатывает исключение {@link NotSaveItemException}, возвращая статус BAD_REQUEST.
 *
 * - {@link #handleNullArgException(NullArgException)}:
 *   Обрабатывает исключение {@link NullArgException}, возвращая статус BAD_REQUEST.
 *
 * - {@link #handleNotDeleteException(NotDeleteException)}:
 *   Обрабатывает исключение {@link NotDeleteException}, возвращая статус CONFLICT.
 *
 * - {@link #handleDatabaseException(DatabaseException)}:
 *   Обрабатывает исключение {@link DatabaseException}, возвращая статус CONFLICT.
 *
 * - {@link #handleExcelFileProcessingException(ExcelFileProcessingException)}:
 *   Обрабатывает исключение {@link ExcelFileProcessingException}, возвращая статус CONFLICT.
 */
@ControllerAdvice
public class GlobalExceptionHandler
{

    //region Fields
    private static final Logger logger =  LoggerFactory.getLogger(GlobalExceptionHandler.class);
    //endRegion

    //region Methods
    /**
     * Обрабатывает исключение NotFoundItemException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом BAD_REQUEST
     */
    @ExceptionHandler(NotFoundItemException.class) // Аннотация указывает, что этот метод обрабатывает PasswordEmptyException
    public ResponseEntity<String> handleNotFoundItemException(NotFoundItemException ex)
    {
        logger.info("Ошибка: Невозможно найти товар в базе данных: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: Невозможно найти товар в базе данных: " + ex.getMessage()); // Возвращает статус BAD_REQUEST с сообщением
    }

    /**
     * Обрабатывает исключение NotSaveItemException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом BAD_REQUEST
     */
    @ExceptionHandler(NotSaveItemException.class) // Аннотация указывает, что этот метод обрабатывает PasswordEmptyException
    public ResponseEntity<String> handleNotSaveItemException(NotSaveItemException ex)
    {
        logger.info("Ошибка: Невозможно охранить товар в базе данных: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: Невозможно сохранить товар в базе данных: " + ex.getMessage()); // Возвращает статус BAD_REQUEST с сообщением
    }

    /**
     * Обрабатывает исключение NullArgException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом BAD_REQUEST
     */
    @ExceptionHandler(NullArgException.class) // Аннотация указывает, что этот метод обрабатывает PasswordEmptyException
    public ResponseEntity<String> handleNullArgException(NullArgException ex)
    {
        logger.info("Ошибка: Предан null в место аргумента: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка: Предан null в место аргумента: " + ex.getMessage()); // Возвращает статус BAD_REQUEST с сообщением
    }

    /**
     * Обрабатывает исключение NotDeleteException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(NotDeleteException.class) // Аннотация указывает, что этот метод обрабатывает NotDeleteException
    public ResponseEntity<String> handleNotDeleteException(NotDeleteException ex)
    {
        logger.info("Ошибка: Невозможно удалить товар из базы данных: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: Невозможно удалить товар из базы данных: " + ex.getMessage()); // Возвращает статус BAD_REQUEST с сообщением
    }

    /**
     * Обрабатывает исключение DatabaseException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(DatabaseException.class) // Аннотация указывает, что этот метод обрабатывает DatabaseException
    public ResponseEntity<String> handleDatabaseException(DatabaseException ex)
    {
        logger.info("Ошибка: Невозможно подключиться к базе данных: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка: Невозможно подключиться к базе данных: " + ex.getMessage()); // Возвращает статус BAD_REQUEST с сообщением
    }

    /**
     * Обрабатывает исключение ExcelFileProcessingException.
     *
     * @param ex исключение, которое содержит информацию об ошибке
     * @return ResponseEntity с сообщением об ошибке и статусом CONFLICT
     */
    @ExceptionHandler(ExcelFileProcessingException.class) // Аннотация указывает, что этот метод обрабатывает ExcelFileProcessingException
    public ResponseEntity<String> handleExcelFileProcessingException(ExcelFileProcessingException ex)
    {
        logger.info("Ошибка:  Проблема с обработкой файла Excel: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Ошибка:  Проблема с обработкой файла Excel: " + ex.getMessage()); // Возвращает статус BAD_REQUEST с сообщением
    }

    //endRegion
}

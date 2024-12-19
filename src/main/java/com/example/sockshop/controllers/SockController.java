package com.example.sockshop.controllers;

import com.example.sockshop.models.Socks;
import com.example.sockshop.service.SockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;


/**
 * Класс SockController:
 *
 * Реализует REST API для управления носками в системе.
 * Предоставляет endpoints для операций добавления, удаления, получения, обновления, фильтрации и пагинации носков, а также загрузки данных из файла.
 *
 * Основные функции:
 * - Добавление новых носков в систему.
 * - Удаление носков из системы.
 * - Получение списка всех носков.
 * - Фильтрация носков по цвету и проценту хлопка.
 * - Обновление информации о носках.
 * - Загрузка носков из файла.
 * - Получение списка носков с фильтрацией и пагинацией.
 * - Получения списка носков по диапазону содержания хлопка (к примеру от 30% до 70%).
 *
 * - Логирование результатов
 *
 * Зависимости:
 * - SockService: сервис, содержащий бизнес-логику, связанную с носками.
 *
 * Методы:
 * - {@link #addSocks(Socks)}: Добавляет новые носки в систему.
 * - {@link #deleteSocks(Socks)}: Удаляет носки из системы.
 * - {@link #getAllSocks()}: Возвращает список всех носков.
 * - {@link #getSocksByFilter(String, String, Integer)}: Возвращает список носков с возможностью фильтрации.
 * - {@link #updateSocks(Long, Socks)}: Обновляет информацию о носках по их идентификатору.
 * - {@link #uploadSocksBatch(MultipartFile)}: Загружает данные о носках из файла.
 * - {@link #getSocks(String, Integer, int, int)}: Возвращает список носков с возможностью фильтрации и пагинации.
 * - {@link №getSocksByCottonPercentageRange(@RequestParam Integer minCottonPercentage,@RequestParam Integer maxCottonPercentage)}: Служит для получения списка носков по диапазону содержания хлопка (к примеру от 30% до 70%).
 * - {@link #sortSocks(String, boolean)}: Возвращает  список носков, отсортированных по (возрастанию или убыванию) заданному полю (цвету или процентному содержанию хлопка).
 *
 * Конечные точки:
 * - {@code POST /api/socks/income}: Добавляет новые носки в систему.
 * - {@code POST /api/socks/outcome}: Удаляет носки из системы.
 * - {@code GET /api/socks/get/all/socks}: Возвращает список всех носков.
 * - {@code GET /api/socks/filter}: Фильтрует носки по цвету и проценту хлопка.
 * - {@code PUT /api/socks/{id}}: Обновляет данные носков по идентификатору.
 * - {@code POST /api/socks/batch}: Загружает данные о носках из файла.
 * - {@code GET /api/socks/pagination}: Возвращает список носков с фильтрацией и пагинацией.
 * - {@code GET /api/socks/filter/cotton}: Возвращает список носков, соответствующих заданному диапазону содержания хлопка от... и  до... (к примеру от 30% до 70%)
 * - {@code GET /api/socks/sort}: Возвращает  список носков, отсортированных по (возрастанию или убыванию) заданному полю (цвету или процентному содержанию хлопка).
 *
 * Архитектурные особенности:
 * - Контроллер взаимодействует с SockService для выполнения бизнес-логики.
 * - Возвращает HTTP-ответы клиенту в формате ResponseEntity.
 * - Поддерживает загрузку и обработку данных из файлов формата Excel через MultipartFile.
 *
 * HTTP-методы:
 * - POST: для добавления, удаления и загрузки носков.
 * - GET: для получения списка всех носков или с фильтрацией и пагинацией.
 * - PUT: для обновления данных о носках.
 *
 * Исключения:
 * - Ошибки обработки файлов и некорректных запросов возвращают статус 400 (Bad Request) с описанием ошибки.
 *
 * Аннотации:
 * - @RestController: определяет класс как REST контроллер.
 * - @RequestMapping("/api/socks"): задает базовый путь для всех endpoints.
 */
@RestController
@RequestMapping("/api/socks") // Устанавливаем базовый URL для всех методов контроллера
public class SockController
{
    //region Fields
    private static final Logger logger = LoggerFactory.getLogger(SockService.class); // Логгер для записи информации о выполнении операций
    private SockService sockService; // Объявляем сервис для работы с носками
    //endRegion

    //region Constructors
    public SockController(SockService sockService) // Конструктор контроллера
    {
        this.sockService = sockService; // Инициализируем сервис
    }
    //endRegion

    //region Methods
    /**
     * Метод addIncome:
     * Добавляет новые носки в систему (усиливает инвентарь носков).
     * @param socks - объект типа Socks, представляющий добавляемые носки.
     * @return ResponseEntity<Void> - ответ с статусом OK.
     */
    @PostMapping("/income") // Настраиваем endpoint для добавления носков
    public ResponseEntity<String> addSocks(@RequestBody Socks socks)
    {
        sockService.addIncome(socks); // Вызываем метод сервиса для добавления носков
        logger.info("Товар (носки) успешно добавлен в базу данных В количестве: " + socks.getQuantity() + " штук");
        return ResponseEntity.status(HttpStatus.OK).body("Товар (носки) успешно добавлен в базу данных В количестве: " + socks.getQuantity() + " штук"); // Возвращаем ответ с статусом 200 OK
    }

    /**
     * Метод addOutcome:
     * Удаляет носки из системы (уменьшает инвентарь носков).
     * @param socks - объект типа Socks, представляющий удаляемые носки.
     * @return ResponseEntity<Void> - ответ с статусом OK.
     */
    @PostMapping("/outcome") // Настраиваем endpoint для удаления носков
    public ResponseEntity<String> deleteSocks(@RequestBody Socks socks)
    {
        sockService.addOutcome(socks); // Вызываем метод сервиса для удаления носков
        logger.info("Товар (носки) успешно удалены из базы данных В количестве: " + socks.getQuantity() + " штук");
        return ResponseEntity.status(HttpStatus.OK).body("Товар (носки) успешно удалены из базы данных В количестве: " + socks.getQuantity() + " штук");
    }

    /**
     * Метод getSocks:
     * Получает список носков с возможностью фильтрации по цвету и проценту хлопка.
     * @param color - цвет носков (необязательный параметр для фильтрации).
     * @param comparison - оператор сравнения для фильтрации по проценту хлопка.
     * @param cottonPercentage - процент хлопка для фильтрации (необязательный параметр).
     * @return ResponseEntity<List<Socks>> - ответ с статусом OK и списком носков.
     */
    @GetMapping("/filter") // Настраиваем endpoint для получения носков
    public ResponseEntity<List<Socks>> getSocksByFilter(@RequestParam(required = false) String color,
                                                @RequestParam(required = false) String comparison,
                                                @RequestParam(required = false) Integer cottonPercentage)
    {
        List<Socks> socks = sockService.getSocksByFilter(color, comparison, cottonPercentage); // Получаем список носков через сервис
        logger.info("Список носков успешно получен из базы данных: " + socks);
        return ResponseEntity.ok(socks); // Возвращаем ответ с статусом 200 OK и списком носков
    }

    /**
     * Метод getAllSocks:
     * Получает список всех носков
     * @return ResponseEntity<List<Socks>> - ответ с статусом OK и списком носков.
     */
    @GetMapping ("/get/all/socks")// Настраиваем endpoint для получения носков
    public ResponseEntity<List<Socks>> getAllSocks()
    {
        List<Socks> socks = sockService.getAllSocks(); // Получаем список носков через сервис
        logger.info("Успешно получен список всех носков из базы данных: " + socks);
        return ResponseEntity.ok(socks); // Возвращаем ответ с статусом 200 OK и списком носков
    }

    /**
     * Метод updateSocks:
     * Обновляет информацию о носках по идентификатору.
     * @param id - идентификатор носков, которые необходимо обновить.
     * @param socks - объект типа Socks с новыми данными для обновления.
     * @return ResponseEntity<Void> - ответ с статусом OK.
     */
    @PutMapping("/{id}") // Настраиваем endpoint для обновления носков по идентификатору
    public ResponseEntity<String> updateSocks(@PathVariable Long id, @RequestBody Socks socks)
    {
        sockService.updateSocks(id, socks); // Вызываем метод сервиса для обновления носков
        logger.info("Товар (носки) " + socks + " успешно обновлён в базе данных: ");
        return ResponseEntity.status(HttpStatus.OK).body("Товар (носки) " + socks + " успешно обновлён в базе данных: "); // Возвращаем ответ с статусом 200 OK
    }

    /**
     * Метод uploadSocksBatch:
     * Загружает информацию из Excel файла.
     * @param file - файл, содержащий данные о носках.
     * @return ResponseEntity<String> - ответ с результатом загрузки.
     */
    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadSocksBatch(@RequestParam("file") MultipartFile file)
    {
        try
        {
            sockService.processSocksBatch(file);
            logger.info("Excel - файл загружен и обработан успешно.");
            return ResponseEntity.status(HttpStatus.OK).body("Excel - файл загружен и обработан успешно.");
        }
        catch (Exception e)
        {
            logger.info("обработка файла не удалась: ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("обработка файла не удалась: " + e.getMessage());
        }
    }

    /**
     * Метод getSocks:
     * Получает список носков с возможностью фильтрации и пагинации.
     * @param color - цвет носка (необязательный параметр для фильтрации).
     * @param cottonPercentage - процент хлопка для фильтрации (необязательный параметр).
     * @param page - номер страницы для пагинации (по умолчанию 0).
     * @param size - размер страницы для пагинации (по умолчанию 10).
     * @return ResponseEntity<Page<Socks>> - ответ с статусом OK и списком носков с учетом фильтров и пагинации.
     */
    @GetMapping("/pagination")
    public Page<Socks> getSocks(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Integer cottonPercentage,
            @RequestParam(defaultValue = "0") int page, // Номер страницы
            @RequestParam(defaultValue = "10") int size // Размер страницы
    )
    {
        logger.info("Успешно обработан запрос с фильтрацией и пагинацией: " + sockService.getSocksByFilters(color, cottonPercentage, page, size));
        return sockService.getSocksByFilters(color, cottonPercentage, page, size);
    }


    /**
     * Метод getSocksByCottonPercentageRange:
     * Служит для получения списка носков по диапазону содержания хлопка.
     *
     * @param minCottonPercentage минимальный процент хлопка (включительно).
     * @param maxCottonPercentage максимальный процент хлопка (включительно).
     * @return список носков, соответствующих заданному диапазону.
     */
    @GetMapping("/filter/cotton") // HTTP GET запрос по указанному пути
    public ResponseEntity<List<Socks>> getSocksByCottonPercentageRange(
            @RequestParam Integer minCottonPercentage,
            @RequestParam Integer maxCottonPercentage)
    {
        // Вызываем метод сервиса для получения списка носков
        List<Socks> socksList = sockService.getSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage);

        logger.info("Успешно обработан запрос для получения списка носков: {}, по заданному диапазону содержания хлопка от: {}% и до {}% : ", socksList, minCottonPercentage, maxCottonPercentage);
        // Возвращаем список носков с HTTP статусом 200 OK
        return ResponseEntity.ok(socksList);
    }


    /**
     * Метод sortSocks:
     * Служит для получения списка носков, отсортированных по (возрастанию или убыванию) заданному полю (цвету или процентному содержанию хлопка).
     *
     * @param sortBy поле, по которому необходимо сортировать (color или cottonPercentage).
     * @param ascending направление сортировки (true - по возрастанию, false - по убыванию).
     * @return список отсортированных носков.
     */
    @GetMapping("/sort") // HTTP GET запрос по указанному пути
    public ResponseEntity<List<Socks>> sortSocks(
            @RequestParam String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending)
    {
        List<Socks> sortedSocks = sockService.sortSocks(sortBy, ascending); // Вызываем метод сервиса для получения отсортированного списка носков

        logger.info("Успешно обработан запрос для получения отсортированного списка носков: {} по заданному полю: {} и направлению сортировки {}: ", sortedSocks, sortBy, ascending);

        return ResponseEntity.ok(sortedSocks); // Возвращаем отсортированный список носков с HTTP статусом 200 OK
    }

    //endRegion
}


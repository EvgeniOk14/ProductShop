package com.example.sockshop.service;

import com.example.sockshop.exceptions.custom.*;
import com.example.sockshop.models.Socks;
import com.example.sockshop.repository.CustomSocksRepository;
import com.example.sockshop.repository.SockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс SockService:
 *
 * Реализует бизнес-логику для управления запасами носков в приложении.
 * Отвечает за добавление, удаление, обновление и получение носков, а также за обработку данных из загружаемых файлов.
 *
 * Ключевые функции:
 * - Добавление новых носков на склад или увеличение количества существующих.
 * - Удаление носков со склада с проверкой на наличие достаточного количества.
 * - Получение списка носков с поддержкой фильтрации по цвету и проценту содержания хлопка.
 * - Получение носков с фильтрацией и пагинацией
 * - Обработка партий носков из загружаемых файлов Excel.
 * - фильтрация товара по заданному диапазону (от 30% до 70%) содержания хлопка
 *
 * Зависимости:
 * - {@link SockRepository}: репозиторий для доступа к данным носков.
 * - {@link CustomSocksRepository}: кастомный репозиторий для выполнения специализированных запросов к носкам.
 * - {@link EntityManager}: объект для управления сущностями и выполнения операций с базой данных.
 *
 * Методы:
 * - {@link #addIncome(Socks)}: Добавляет новое количество носков на склад.
 * - {@link #addOutcome(Socks)}: Удаляет указанное количество носков со склада.
 * - {@link #getSocksByFilter(String, String, Integer)}: Получает список носков по заданным фильтрам.
 * - {@link #getAllSocks()}: Получает список всех носков.
 * - {@link #updateSocks(Long, Socks)}: Обновляет информацию о существующих носках.
 * - {@link #processSocksBatch(MultipartFile)}: Обрабатывает партию носков из загружаемого файла.
 * - {@link #getSocksByFilters(String, Integer, int, int)}: Получает список носков с фильтрацией и пагинацией.
 * - {@link №getSocksByCottonPercentageRange(Integer minCottonPercentage, Integer maxCottonPercentage)}: Служит для получения списка носков, отфильтрованных по диапазону содержания хлопка.
 * - {@link #sortSocks(String sortBy, boolean ascending)}: Возвращает  список носков, отсортированных по (возрастанию или убыванию) заданному полю (цвету или процентному содержанию хлопка).
 *
 * Исключения:
 * - {@link NullArgException}: кастомное исключение выбрасывается, если передан аргумент равный null.
 * - {@link IllegalArgumentException}: системное выбрасывается, если количество носков меньше или равно нулю.
 * - {@link RuntimeException}:  системное исключение, выбрасывается, если недостаточно носков для удаления или если носки не найдены.
 * - {@link DatabaseException}: кастомное исключение, выбрасывается при ошибках выполнения запросов к базе данных.
 * - {@link PersistenceException}: системное исключение, выбрасывается, при проблемах, связанных с работой с базами данных
 * - {@link NotFoundItemException}: кастомное исключение, выбрасывается, если товар не найден в базе данных
 * - {@link NotSaveItemException}: кастомное исключение, выбрасывается, если товар не сохранён в базе данных
 *
 * Логирование:
 * Для логирования используется SLF4J (Simple Logging Facade for Java),
 * который предоставляет абстракцию для различных библиотек логирования.
 * Это позволяет легко менять реализацию логирования без изменения кода.
 *
 * Аннотации:
 * - @Service: указывает, что класс является сервисным компонентом в Spring.
 * - @Transactional: указывает, что метод должен выполняться в контексте транзакции.
 *   Это означает, что все операции внутри метода будут атомарными: если одна из операций завершится неудачно,
 *   все изменения будут отменены, что обеспечивает целостность данных.
 */
@Service // Помечает класс как сервисный компонент Spring
public class SockService
{
    // region Fields
    private static final Logger logger = LoggerFactory.getLogger(SockService.class); // Логгер для записи информации о выполнении операций
    private SockRepository sockRepository; // Репозиторий для работы с носками
    private CustomSocksRepository customSocksRepository; // Кастомный репозиторий для работы с носками
    @PersistenceContext
    private EntityManager entityManager; // Внедряем EntityManager через аннотацию @PersistenceContext
    // endRegion

    // region Constructors
    /**
     * Конструктор класса SockService.
     *
     * @param sockRepository репозиторий для работы с носками.
     */
    public SockService(SockRepository sockRepository, CustomSocksRepository customSocksRepository)
    {
        this.sockRepository = sockRepository; // Инициализируем репозиторий
        this.customSocksRepository = customSocksRepository; // Инициализируем репозиторий
    }
    // endRegion

    // region Methods
    /**
     * Метод addIncome:
     * Служит для добавления новых носков или обновления количества существующих носков в базе данных.
     * Проверяет входящий параметр на null и валидирует количество носков.
     * Если носки с таким цветом и процентом хлопка уже существуют, их количество увеличивается.
     * В противном случае создается новая запись о носках и сохраняется в базе данных.
     * В случае ошибки при поиске, сохранении или выполнении запроса к базе данных выбрасываются соответствующие исключения.
     *
     * @param socks объект носков, содержащий информацию о цвете, проценте хлопка и количестве.
     *              Должен быть не null и иметь положительное количество.
     * @exception NullArgException если переданный объект носков равен null.
     * @exception IllegalArgumentException если количество носков меньше или равно нулю.
     * @exception NotFoundItemException если не удается найти товар (носки) в базе данных.
     * @exception NotSaveItemException если не удается сохранить товар (носки) в базе данных.
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     */
    @Transactional
    public void addIncome(Socks socks)
    {
        if (socks == null ) // проверяем входящий параметр на null
        {
            logger.error("В метод передан аргумент {} равный null ", socks); // логируем ошибку
            throw new NullArgException("В метод передан аргумент равный null ! "); // обрабатываем исключение
        }
        if (socks.getQuantity() <= 0) // проверяем, количество товара должно быть больше нуля, если нет то:
        {
            logger.error(" Количество носков {}, должно быть больше нуля! ", socks.getQuantity()); // логируем ошибку
            throw new IllegalArgumentException("Количество носков должно быть больше нуля! "); // обрабатываем исключение
        }
        try
        {
            // Ищем носки с таким же цветом и процентом хлопка
            List<Socks> existingSocks = sockRepository.findByColorAndCottonPercentage(socks.getColor(), socks.getCottonPercentage());

            // Если такие носки уже есть, увеличиваем их количество
            if (!existingSocks.isEmpty())
            {
                Socks existingSock = existingSocks.get(0); // Предполагаем, что все носки с таким цветом и процентом хлопка одинаковы
                existingSock.setQuantity(existingSock.getQuantity() + socks.getQuantity()); // Увеличиваем количество носков
                sockRepository.save(existingSock); // Сохраняем обновленный носок
                logger.info("Обновленные носки в количестве: {}, цвета: {} с процентным содержанием хлопка {}% . ",
                        existingSock.getQuantity(), existingSock.getColor(), existingSock.getCottonPercentage());  // логируем ошибку
            }
            else
            {
                // Если таких носков нет, создаем новый  товар (носки) и устанавливаем ему количество равным переданному
                socks.setQuantity(socks.getQuantity()); // Устанавливаем количество равным переданному
                sockRepository.save(socks); // Сохраняем новый товар (носки)
                logger.info("Добавили новые носки в количестве: {}, цвета: {} с процентным содержанием хлопка: {}% .",
                        socks.getQuantity(), socks.getColor(), socks.getCottonPercentage()); // логируем ошибку
            }
        }
        catch (NotFoundItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно найти товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw ex;
        }
        catch (NotSaveItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно сохранить товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw  ex;
        }
        catch (PersistenceException ex) // обрабатываем кастомное исключение DatabaseException на глобальном уровне
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ошибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных");
        }
    }

    /**
     * Метод addOutcome:
     * Служит для уменьшения количества носков в базе данных.
     * Проверяет входящий параметр на null и валидирует количество носков.
     * Если носки с таким цветом и процентом хлопка существуют, их количество уменьшается.
     * Если после уменьшения количество носков становится равным нулю, запись удаляется из базы данных.
     * Если количество носков на складе недостаточно, выбрасывается соответствующее исключение.
     * В случае ошибки при поиске, сохранении или выполнении запроса к базе данных выбрасываются соответствующие исключения.
     *
     * @param socks объект носков, содержащий информацию о цвете, проценте хлопка и количестве.
     *              Должен быть не null и иметь положительное количество.
     * @exception NullArgException если переданный объект носков равен null.
     * @exception IllegalArgumentException если количество носков меньше или равно нулю.
     * @exception RuntimeException если запрашиваемое количество носков превышает доступное на складе.
     * @exception NotFoundItemException если не удается найти товар (носки) в базе данных.
     * @exception NotSaveItemException если не удается сохранить товар (носки) в базе данных.
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     */
    @Transactional
    public void addOutcome(Socks socks)
    {
        if (socks == null ) // проверяем условие на передачу аргумента равного null
        {
            logger.error("В метод передан аргумент {} равный null ", socks); // логируем ошибку
            throw new NullArgException("В метод передан аргумент равный null ! "); // обрабатываем кастомное исключение
        }
        if (socks.getQuantity() <= 0) // проверяем условие, что количество товара должно быть больше нуля
        {
            logger.error(" Количество носков {}, должно быть больше нуля! ", socks.getQuantity()); // логируем ошибку
            throw new IllegalArgumentException("Количество носков должно быть больше нуля! "); // обрабатываем системное исключение
        }
        try
            {
                List<Socks> existingSocks = sockRepository.findByColorAndCottonPercentage(socks.getColor(), socks.getCottonPercentage()); // Ищем существующие носки

                if (!existingSocks.isEmpty()) // если такие есть в наличие, то:
                {
                    Socks sock = existingSocks.get(0); // берём первый

                if (sock.getQuantity() >= socks.getQuantity()) // проверяем условие количество у товара в базе данных должно быть больше чем количество товара переданое в запросе
                {
                    sock.setQuantity(sock.getQuantity() - socks.getQuantity()); // Уменьшаем количество носков

                if (sock.getQuantity() == 0) // если количество товара доходит до нуля, то:
                {
                    sockRepository.delete(sock);  // Если количество стало нулевым, удаляем запись
                    logger.info("Удалены носки в количестве: {}, цвета: {} с процентным содержанием хлопка: {}% . Товар (носки) будут удалены, если количество достигнет нуля.",
                            socks.getQuantity(), socks.getColor(), socks.getCottonPercentage()); // логируем ошибку
                }
                else
                {
                    sockRepository.save(sock); // Сохраняем обновленные носки
                    logger.info("Удалены носки в количестве: {}, цвета: {} с процентным содержанием хлопка: {}%.",
                            socks.getQuantity(), socks.getColor(), socks.getCottonPercentage()); // логируем ошибку
                }

                }
                else
                {
                    logger.error("Недостаточное количество носков цвета: {}, с процентным содержанием хлопка: {}. Доступное количество на сладе: {}, запрашиваемое количество: {}.",
                            socks.getColor(), socks.getCottonPercentage(), sock.getQuantity(), socks.getQuantity()); // логируем ошибку
                throw new RuntimeException("Not enough socks available.");
                }
                }
                else
                {
                    logger.error("Не найдено носков цвета: {},   с процентным содержанием хлопка: {}.",
                    socks.getColor(), socks.getCottonPercentage()); // логируем ошибку
                    throw new RuntimeException("По вашему запросу носков не найдено.");
                }

            }
        catch (NotFoundItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно найти товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw ex;
        }
        catch (NotSaveItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно сохранить товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw  ex;
        }
        catch (PersistenceException ex) // обрабатываем кастомное исключение DatabaseException на глобальном уровне
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ошибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных");
        }
    }


    /**
     * Метод getSocksByFilter:
     * Служит для получения списка носков по заданным фильтрам: цвет, тип сравнения и процент содержания хлопка.
     * Если не указан ни один фильтр, выбрасывается исключение.
     * Использует Criteria API для построения динамического запроса на основе заданных параметров.
     * В случае ошибки при выполнении запроса к базе данных выбрасывается соответствующее исключение.
     *
     * @param color цвет носков, по которому будет производиться фильтрация. Может быть null или пустой строкой.
     * @param comparison тип сравнения для процента хлопка. Должен быть одним из следующих значений:
     *                  "moreThan", "lessThan", "equal". Не может быть null.
     * @param cottonPercentage процент содержания хлопка, по которому будет производиться фильтрация. Может быть null.
     * @return возвращает список носков, соответствующих заданным фильтрам.
     * @exception NullArgException если не указан ни один фильтр.
     * @exception IllegalArgumentException если задан некорректный тип сравнения.
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     */
    @Transactional
    public List<Socks> getSocksByFilter(String color, String comparison, Integer cottonPercentage)
    {
        if (color == null && comparison == null && cottonPercentage == null)
        {
            logger.error("Необходимо указать хотя бы один фильтр: цвет: {}, тип сравнения {}, или процент содержания хлопка{}!", color, comparison, cottonPercentage); // логируем ошибку
            throw new NullArgException("Необходимо указать хотя бы один фильтр: цвет, тип сравнения или процент содержания хлопка!"); // обрабатываем исключение
        }
        try
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder(); // Инициализируем построитель запросов

            CriteriaQuery<Socks> query = cb.createQuery(Socks.class); // Создаем объект CriteriaQuery для сущности Socks

            Root<Socks> root = query.from(Socks.class); // Определяем корень запроса (таблицу) для выборки из сущности Socks

            List<Predicate> predicates = new ArrayList<>(); // Создаем список условий для фильтрации

            if (color != null && !color.isEmpty()) // Фильтр по цвету: добавляем условие, если цвет не null и не пустой
            {
                predicates.add(cb.equal(root.get("color"), color)); // Добавляем условие равенства цвета
            }

            if (cottonPercentage != null) // Фильтр по проценту хлопка
            {
                // Проверяем тип сравнения и добавляем соответствующее условие:
                switch (comparison)
                {
                    case "moreThan":
                        predicates.add(cb.greaterThan(root.get("cottonPercentage"), cottonPercentage)); // Условие больше
                        break;
                    case "lessThan":
                        predicates.add(cb.lessThan(root.get("cottonPercentage"), cottonPercentage)); // Условие меньше
                        break;
                    case "equal":
                        predicates.add(cb.equal(root.get("cottonPercentage"), cottonPercentage)); // Условие равенства
                        break;
                    default:
                        logger.error("Некорректный тип сравнения: {} ", comparison); // Логируем ошибку, если тип сравнения некорректный
                        throw new IllegalArgumentException("Некорректный тип сравнения: " + comparison); // Генерируем исключение с сообщением об ошибке
                }
            }

            query.select(root).where(predicates.toArray(new Predicate[0])); // Добавляем условия в запрос

            return entityManager.createQuery(query).getResultList(); // Выполняем запрос и возвращаем результат в виде списка
        }

        catch (PersistenceException ex) // обрабатываем кастомное исключение DatabaseException на глобальном уровне
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ошибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных"); // обрабатываем исключение
        }
    }


    /**
     * Метод getAllSocks:
     * Служит для получения списка всех носков из базы данных.
     * Выполняет запрос к репозиторию для получения всех записей о носках.
     * В случае ошибки при выполнении запроса к базе данных выбрасывается соответствующее исключение.
     *
     * @return возвращает список всех носков, хранящихся в базе данных.
     * @exception NotFoundItemException если не удается найти товар (носки) в базе данных.
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     */
    @Transactional
    public List<Socks> getAllSocks()
    {
        try
        {
            return sockRepository.findAll(); // Возвращаем отфильтрованный список
        }
        catch (NotFoundItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно найти товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw ex;
        }
        catch (PersistenceException ex) // обрабатываем кастомное исключение DatabaseException на глобальном уровне
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ошибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных"); // обрабатываем исключение
        }
    }

    /**
     * Метод updateSocks:
     * Служит для обновления информации о носках в базе данных.
     * Проверяет входные параметры на null и валидирует идентификатор носков.
     * Если носки с указанным ID существуют, обновляются их цвет, процент хлопка и количество.
     * В случае ошибки при поиске, сохранении или выполнении запроса к базе данных выбрасываются соответствующие исключения.
     *
     * @param id идентификатор носков, которые необходимо обновить. Должен быть больше нуля.
     * @param socks объект носков, содержащий новую информацию о цвете, проценте хлопка и количестве.
     *              Должен быть не null.
     * @exception NullArgException если переданный объект носков равен null или идентификатор меньше или равен нулю.
     * @exception RuntimeException если носки с указанным идентификатором не найдены.
     * @exception NotFoundItemException если не удается найти товар (носки) в базе данных.
     * @exception NotSaveItemException если не удается сохранить товар (носки) в базе данных.
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     */
    @Transactional
    public void updateSocks(Long id, Socks socks)
    {
        if (socks == null )
        {
            logger.error("В метод передан аргумент {} равный null ", socks); // логируем ошибку
            throw new NullArgException("В метод передан аргумент равный null ! "); // обрабатываем исключение
        }
        if (id <= 0 )
        {
            logger.error("Параметр {} должен быть больше нуля! ", id); // логируем ошибку
            throw new NullArgException("Параметр id должен быть больше нуля! ! "); // обрабатываем исключение
        }
        try
        {
            Socks existingSock = sockRepository.findById(id).orElseThrow(() -> new RuntimeException("Носки не найдены")); // Находим существующий носок
            existingSock.setColor(socks.getColor()); // Обновляем цвет носка
            existingSock.setCottonPercentage(socks.getCottonPercentage()); // Обновляем процент хлопка
            existingSock.setQuantity(socks.getQuantity()); // Обновляем количество носков
            sockRepository.save(existingSock); // Сохраняем обновленный носок
            logger.info("Обновлены носки с ID {}", id); // Логируем обновление носка
        }
        catch (NotFoundItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно найти товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw ex;
        }
        catch (NotSaveItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно сохранить товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw  ex;
        }
        catch (PersistenceException ex) // обрабатываем кастомное исключение DatabaseException на глобальном уровне
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ошибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных");
        }
    }


    /**
     * Метод processSocksBatch:
     * Служит для обработки пакетной загрузки носков из Excel-файла.
     * Проверяет, что файл не пустой и имеет корректный формат (Excel).
     * Читает данные из файла и обновляет существующие записи носков или создает новые,
     * в зависимости от наличия носков с указанным цветом и процентом хлопка.
     * В случае возникновения ошибок при обработке файла или взаимодействии с базой данных выбрасываются соответствующие исключения.
     *
     * @param file файл, содержащий данные о носках в формате Excel. Не должен быть пустым.
     *
     * @exception NullArgException если переданный файл пустой.
     * @exception IllegalArgumentException если файл имеет некорректный формат (не Excel).
     * @exception ExcelFileProcessingException если возникает ошибка при обработке файла Excel.
     * @exception RuntimeException если возникает ошибка ввода-вывода при работе с файлом.
     * @exception NotSaveItemException если не удается сохранить товар (носки) в базе данных.
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     */
    @Transactional
    public void processSocksBatch(MultipartFile file)
    {
        if (file.isEmpty()) // проверяем что файл не пустой, если да, то:
        {
            logger.error("В метод передан файл {} с пустым содержанием null ", file.getName()); // логируем ошибку
            throw new NullArgException("В метод передан файл с пустым содержанием ! "); // обрабатываем исключение
        }

        if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) // Проверяем, что файл является Excel, если нет, то:
        {
            logger.error("Некорректный формат файла. Пожалуйста, загрузите файл формата - Excel!"); // логируем ошибку
            throw new IllegalArgumentException("Некорректный формат файла. Пожалуйста, загрузите файл формата - Excel!"); // обрабатываем исключение
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) // Открываем файл для чтения
        {
            Sheet sheet = workbook.getSheetAt(0); // Получаем первый лист из рабочей книги

            for (Row row : sheet) // Итерируем по всем строкам в листе
            {
                if (row.getRowNum() == 0) continue; // Пропускаем заголовок (первая строка)

                String color = row.getCell(0).getStringCellValue(); // Получаем значение цвета из первой ячейки

                int cottonPercentage = (int) row.getCell(1).getNumericCellValue(); // Получаем процент содержания хлопка из второй ячейки и приводим к целому числу

                int quantity = (int) row.getCell(2).getNumericCellValue(); // Получаем количество из третьей ячейки и приводим к целому числу


                List<Socks> existingSocks = sockRepository.findByColorAndCottonPercentage(color, cottonPercentage); // получаем список носков по фильтру

                // Проверяем существование носков с заданным цветом и процентом хлопка:

                if (!existingSocks.isEmpty()) // Если носки с такими параметрами существуют, то:
                {
                    Socks sock = existingSocks.get(0); // Получаем первую найденную запись носков

                    sock.setQuantity(sock.getQuantity() + quantity); // Увеличиваем количество носков на значение из файла

                    sockRepository.save(sock); // Сохраняем обновленную запись носка в репозитории
                }
                else
                {
                    // Создаем новую запись в базе данных:
                    Socks newSock = new Socks(); // создаём новый экземпляр класса Socks (носков)
                    newSock.setColor(color); // устанавливаем цвет
                    newSock.setCottonPercentage(cottonPercentage); // устанавливаем процент хлопка
                    newSock.setQuantity(quantity); // устанавливаем количество
                    sockRepository.save(newSock); // сохраняем в базе данных
                }
            }
        }
        catch (ExcelFileProcessingException ex) // обрабатываем кастомное исключение на глобальном уровне
        {   logger.error("Ошибка при обработке файла Excel: {} ", ex.getMessage()); // логируем ошибку
            throw ex;
        }
        catch (IOException e) // обрабатываем системное исключение
        {
            logger.error("Ошибка ввода-вывода при работе с файлом: {}", e.getMessage()); // логируем ошибку
            throw new RuntimeException(e);
        }
        catch (NotSaveItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно сохранить товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw  ex;
        }
        catch (PersistenceException ex) // обрабатываем кастомное исключение DatabaseException на глобальном уровне
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ошибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных"); // обрабатываем кастомное исключение на глобальном уровне
        }
    }

    /**
     * Метод getSocksByFilters:
     * Служит для получения страницы носков, отфильтрованных по заданным параметрам: цвет и процент хлопка.
     * Если не указано ни одного фильтра, выбрасывается исключение.
     * В случае, если носки с заданными параметрами не найдены, возвращается пустая страница.
     *
     * @param color цвет носков для фильтрации. Может быть null.
     * @param cottonPercentage процент содержания хлопка для фильтрации. Может быть null.
     * @param page номер страницы для получения (начиная с 0).
     * @param size размер страницы (количество элементов на странице).
     *
     * @return возвращает страницу носков, соответствующих заданным фильтрам.
     * @exception NullArgException если не указано ни одного фильтра (цвет, процент хлопка, страница, размер).
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     */
    @Transactional
    public Page<Socks> getSocksByFilters(String color, Integer cottonPercentage, int page, int size)
    {
        if (color == null && cottonPercentage == null && page == 0 && size == 0) // проверяем что фильтр не пустой, если пустой, то:
        {
            logger.error("Необходимо указать хотя бы один фильтр: цвет: {}, процент содержания хлопка: {}, страница: {}, размер: {}!", color, cottonPercentage, page, size); // логируем ошибку
            throw new NullArgException("Необходимо указать хотя бы один фильтр: цвет; процент содержания хлопка; страница; размер !"); // обрабатываем исключение
        }
        try
        {
            // Создаем объект Pageable для управления параметрами пагинации, используя номер страницы и размер страницы
            Pageable pageable = PageRequest.of(page, size);

            // Запрашиваем носки из репозитория с заданными фильтрами цвета и процента хлопка, передавая параметры пагинации
            return customSocksRepository.findSocksByFilters(color, cottonPercentage, pageable);
        }
        catch (NotFoundItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.warn("Предупреждение: данного товара с такими параметрами нет в базе данных: {}", ex.getMessage());
            return Page.empty(); // Возвращаем пустую страницу
        }
        catch (PersistenceException ex) // обрабатываем кастомное исключение DatabaseException на глобальном уровне
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ощибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных"); // обрабатываем исключение
        }
    }


    /**
     * Метод getSocksByCottonPercentageRange:
     * Служит для получения списка носков, отфильтрованных по диапазону содержания хлопка.
     * Возвращает носки, содержание хлопка которых находится в заданном диапазоне (включительно).
     * Если диапазон некорректный (нижняя граница больше верхней), выбрасывается исключение.
     * В случае ошибки при выполнении запроса к базе данных выбрасывается соответствующее исключение.
     *
     * @param minCottonPercentage минимальный процент содержания хлопка (включительно). Должен быть не null и больше или равен 0.
     * @param maxCottonPercentage максимальный процент содержания хлопка (включительно). Должен быть не null и больше или равен minCottonPercentage.
     * @return возвращает список носков, соответствующих заданному диапазону содержания хлопка.
     * @exception IllegalArgumentException если диапазон содержания хлопка некорректен.
     * @exception NotFoundItemException если не удается найти товар (носки) в базе данных.
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     *
     */
    @Transactional
    public List<Socks> getSocksByCottonPercentageRange(Integer minCottonPercentage, Integer maxCottonPercentage)
    {
        if (minCottonPercentage == null || maxCottonPercentage == null)
        {
            logger.error("Минимальный и максимальный процент содержания хлопка не могут быть null!"); // логируем ошибку
            throw new IllegalArgumentException("Минимальный и максимальный процент содержания хлопка не могут быть null!"); // обрабатываем исключение
        }
        if (minCottonPercentage < 0 || maxCottonPercentage < 0)
        {
            logger.error("Процент содержания хлопка не может быть отрицательным!"); // логируем ошибку
            throw new IllegalArgumentException("Процент содержания хлопка не может быть отрицательным!"); // обрабатываем исключение
        }
        if (minCottonPercentage > maxCottonPercentage)
        {
            logger.error("Минимальный процент содержания хлопка не может быть больше максимального!"); // логируем ошибку
            throw new IllegalArgumentException("Минимальный процент содержания хлопка не может быть больше максимального!"); // обрабатываем исключение
        }

        try
        {
            // Получаем список носков по диапазону содержания хлопка
            return customSocksRepository.findSocksByCottonPercentageRange(minCottonPercentage, maxCottonPercentage);
        }
        catch (NotFoundItemException ex) // обрабатываем кастомное исключение на глобальном уровне
        {
            logger.error("Ошибка: невозможно найти товар (носки) в базе данных: {}", ex.getMessage()); // логируем ошибку
            throw ex;
        }
        catch (PersistenceException ex)
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ошибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных"); // обрабатываем исключение
        }
    }

    /**
     * Метод sortSocks:
     * Служит для получения списка носков, отсортированных по заданному полю (цвету или проценту хлопка).
     * Если передано некорректное значение для поля сортировки, выбрасывается исключение.
     *
     * @param sortBy поле, по которому необходимо сортировать. Может быть "color" или "cottonPercentage".
     * @param ascending направление сортировки. Если true, сортировка будет по возрастанию, если false - по убыванию.
     * @return возвращает отсортированный список носков.
     * @exception IllegalArgumentException если передано некорректное значение для поля сортировки.
     * @exception DatabaseException если возникает ошибка при выполнении запроса к базе данных.
     */
    @Transactional
    public List<Socks> sortSocks(String sortBy, boolean ascending)
    {
        if (!sortBy.equals("color") && !sortBy.equals("cottonPercentage")) // обрабатываем случай, когда были переданы не корректные значения по фильтру
        {
            logger.error("Вы ввели не корректный аргумент: {}", sortBy); // логируем ошибку
            throw new IllegalArgumentException("Вы ввели не корректный аргумент " + sortBy); // выбрасываем исключение
        }
        try
        {
            if ("color".equalsIgnoreCase(sortBy)) // Проверяем, если параметр sortBy равен "color" (игнорируя регистр)
            {
                // Если ascending истинно, вызываем метод для получения носков, отсортированных по цвету по возрастанию,
                // иначе вызываем метод для получения носков, отсортированных по цвету по убыванию.
                return ascending ? sockRepository.findAllByOrderByColorAsc() : sockRepository.findAllByOrderByColorDesc();
            }
            // Проверяем, если параметр sortBy равен "cottonPercentage" (игнорируя регистр)
            else if ("cottonPercentage".equalsIgnoreCase(sortBy))
            {
                // Если ascending истинно, вызываем метод для получения носков, отсортированных по проценту хлопка по возрастанию,
                // иначе вызываем метод для получения носков, отсортированных по проценту хлопка по убыванию.
                return ascending ? sockRepository.findAllByOrderByCottonPercentageAsc() : sockRepository.findAllByOrderByCottonPercentageDesc();
            }
            else
            {
                throw new IllegalArgumentException("Некорректное значение для поля сортировки: " + sortBy); // обрабатываем исключение
            }
        }
        catch (PersistenceException ex) // обрабатываем кастомное исключение DatabaseException на глобальном уровне
        {
            logger.error("Ошибка при выполнении запроса к базе данных: {}", ex.getMessage()); // логируем ошибку
            throw new DatabaseException("Ошибка при выполнении запроса к базе данных");
        }
    }
    // endRegion
}

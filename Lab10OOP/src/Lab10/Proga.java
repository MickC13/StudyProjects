package Lab10;

import org.apache.log4j.Logger;

/**
 * Главный класс приложения "Почта России - Управление клиентами"
 * Демонстрирует работу протоколирования с использованием Log4j
 * 
 * @author Mikhail
 * @version 10.0
 */
public class Proga {
    
    /**
     * Логгер для протоколирования работы приложения
     * DEBUG: детальная отладочная информация
     * INFO: основные события приложения
     * WARN: предупреждения и некритичные ошибки
     * ERROR: критические ошибки
     */
    private static final Logger logger = Logger.getLogger(Proga.class);
    
    /**
     * Основной метод приложения, демонстрирующий работу протоколирования
     * Имитирует основные этапы работы приложения "Почта России"
     */
    public void runApplication() {
        try {
            logger.info("=== ЗАПУСК ПРИЛОЖЕНИЯ 'ПОЧТА РОССИИ' ===");
            
            // Этап 1: Инициализация приложения
            logger.info("Этап 1: Инициализация приложения...");
            Thread.sleep(300);
            logger.debug("Подробности инициализации: созданы основные компоненты GUI");
            
            // Этап 2: Загрузка данных клиентов
            logger.info("Этап 2: Загрузка данных клиентов...");
            Thread.sleep(500);
            logger.debug("Подробности загрузки: прочитано 15 записей из XML-файла");
            logger.warn("Найдены 2 записи с неполными данными, значения заменены по умолчанию");
            
            // Этап 3: Обработка данных
            logger.info("Этап 3: Обработка данных клиентов...");
            Thread.sleep(400);
            logger.debug("Обработано 15 клиентов, сгенерировано 5 категорий подписок");
            
            // Этап 4: Многопоточная обработка
            logger.info("Этап 4: Запуск многопоточной обработки...");
            simulateMultiThreading();
            
            // Этап 5: Генерация отчетов
            logger.info("Этап 5: Формирование отчетов...");
            Thread.sleep(600);
            logger.debug("Создан PDF-отчет: postal_report.pdf");
            logger.debug("Создан HTML-отчет: postal_report.html");
            
            logger.info("Работа приложения успешно завершена!");
            
        } catch (Exception e) {
            logger.error("Критическая ошибка выполнения приложения", e);
        }
    }
    
    /**
     * Имитирует многопоточную обработку данных с логированием
     * Демонстрирует работу нескольких потоков с протоколированием
     */
    private void simulateMultiThreading() {
        Thread loadThread = new Thread(() -> {
            logger.debug("[DataLoadThread] Начало загрузки данных...");
            try {
                Thread.sleep(200);
                logger.debug("[DataLoadThread] Данные успешно загружены в память");
            } catch (InterruptedException e) {
                logger.error("[DataLoadThread] Ошибка загрузки данных", e);
            }
        });
        
        Thread editThread = new Thread(() -> {
            logger.debug("[DataEditThread] Начало редактирования данных...");
            try {
                Thread.sleep(300);
                logger.debug("[DataEditThread] Данные отредактированы, добавлены префиксы");
            } catch (InterruptedException e) {
                logger.error("[DataEditThread] Ошибка редактирования данных", e);
            }
        });
        
        Thread reportThread = new Thread(() -> {
            logger.debug("[ReportThread] Начало генерации отчета...");
            try {
                Thread.sleep(400);
                logger.debug("[ReportThread] HTML-отчет успешно сгенерирован");
            } catch (InterruptedException e) {
                logger.error("[ReportThread] Ошибка генерации отчета", e);
            }
        });
        
        // Запуск потоков
        loadThread.start();
        editThread.start();
        reportThread.start();
        
        // Ожидание завершения потоков
        try {
            loadThread.join();
            editThread.join();
            reportThread.join();
            logger.info("Многопоточная обработка завершена успешно");
        } catch (InterruptedException e) {
            logger.error("Ошибка ожидания завершения потоков", e);
        }
    }
    
    /**
     * Демонстрирует различные уровни логирования
     * Генерирует сообщения всех уровней для тестирования конфигурации
     */
    public void demonstrateLoggingLevels() {
        logger.trace("TRACE: Очень детальная отладочная информация");
        logger.debug("DEBUG: Отладочная информация о внутреннем состоянии");
        logger.info("INFO: Информационное сообщение о ходе работы");
        logger.warn("WARN: Предупреждение о потенциальной проблеме");
        logger.error("ERROR: Сообщение об ошибке выполнения");
        
        // Логирование с исключением
        try {
            // Имитация ошибки для демонстрации
            throw new RuntimeException("Тестовое исключение для демонстрации логирования");
        } catch (Exception e) {
            logger.error("ERROR: Исключение в процессе работы приложения", e);
        }
    }
    
    /**
     * Точка входа в приложение
     * Инициализирует логирование и запускает демонстрацию работы
     * 
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Настройка Log4j
        Log4jConfig.configure("log4j.properties");
        
        logger.info("Лабораторная работа №10 запущена. Конфигурация Log4j загружена.");
        
        // Создание и запуск приложения
        Proga app = new Proga();
        
        // Демонстрация уровней логирования
        app.demonstrateLoggingLevels();
        
        // Запуск основного сценария приложения
        app.runApplication();
        
        logger.info("Программа завершена. Проверьте файл postal_app.log для просмотра логов.");
    }
}
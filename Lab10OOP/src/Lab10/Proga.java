package Lab10;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;

/**
 * Главный класс приложения "Почта России - Управление клиентами".
 * Предоставляет графический интерфейс для управления клиентской базой данных,
 * включая добавление, редактирование, удаление, поиск клиентов,
 * а также экспорт данных в XML и генерацию отчетов в PDF/HTML форматах.
 * 
 * @author Mikhail
 * @version 10.0
 */
public class Proga {
    /**
     * Логгер для протоколирования работы приложения.
     * DEBUG: детальная отладочная информация о процессе выполнения
     * INFO: основные события приложения и действия пользователя
     * WARN: предупреждения и некритичные ошибки
     * ERROR: критические ошибки с stack trace
     */
    static {
        try {
            // Создаем папку logs если её нет
            new File("logs").mkdirs();
            
            // Явно загружаем конфигурацию Log4j
            PropertyConfigurator.configure("src/log4j.properties");
            
            // Проверяем, что конфигурация загружена
            Logger rootLogger = Logger.getRootLogger();
            if (!rootLogger.getAllAppenders().hasMoreElements()) {
                // Если конфигурация не загрузилась, используем базовую настройку
                BasicConfigurator.configure();
                rootLogger.setLevel(Level.DEBUG);
            }
        } catch (Exception e) {
            System.err.println("Ошибка инициализации Log4j: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static final Logger log = Logger.getLogger(Proga.class);

    
    // Объявления графических компонентов
    private JFrame mainFrame;
    private DefaultTableModel clientsModel;
    private JButton save;
    private JButton addClient;
    private JButton editClient;
    private JButton deleteClient;
    private JButton searchButton;
    private JButton resetButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton generateReportButton;
    private JButton multiThreadingButton;
    private JToolBar toolBar;
    private JScrollPane clientsScroll;
    private JTable clientsTable;
    private JComboBox<String> surnameComboBox;
    private JTextField clientPhone;
    private JTextField clientAddress;
    private JTextField clientNewspaper;
    
    // Исходные данные клиентов
    private List<String[]> originalClientsData;
    private static String flag = "";
    
    // Объекты для синхронизации потоков
    private final Object loadMonitor = new Object();
    private final Object editMonitor = new Object();
    private volatile boolean dataLoaded = false;
    private volatile boolean dataEdited = false;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    /**
     * Пользовательское исключение для обработки ошибок при добавлении клиентов.
     * WARN: генерируется при нарушении правил валидации данных клиента
     */
    private class AddClientException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public AddClientException(String message) {
            super(message);
        }
    }

    /**
     * Пользовательское исключение для обработки ошибок при поиске клиентов.
     * WARN: генерируется когда не указаны критерии поиска
     */
    private class SearchClientException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public SearchClientException(String message) {
            super(message);
        }
    }
    

    /**
     * Поток для загрузки данных из XML-файла.
     * DEBUG: детали процесса загрузки и прогресс
     * INFO: основные этапы загрузки
     * ERROR: ошибки загрузки данных
     */
    private class DataLoadThread extends Thread {
        private String filename;
        
        public DataLoadThread(String filename) {
            super("DataLoadThread");
            this.filename = filename;
            log.debug("Создан поток загрузки данных для файла: " + filename);
        }
        
        @Override
        public void run() {
            log.info("Поток загрузки данных начал выполнение");
            try {
                log.debug("Обновление статуса и прогресс-бара");
                updateStatus("Загрузка данных из XML...");
                progressBar.setValue(0);
                
                // DEBUG: Имитация длительной загрузки с логированием прогресса
                for (int i = 0; i <= 100; i += 20) {
                    Thread.sleep(200);
                    progressBar.setValue(i);
                    log.debug("Прогресс загрузки данных: " + i + "%");
                }
                
                // Загрузка данных в GUI
                SwingUtilities.invokeLater(() -> {
                    try {
                        log.debug("Загрузка данных в GUI потоке");
                        loadDataFromXML(filename);
                        log.info("Данные успешно загружены из файла: " + filename + ", клиентов: " + originalClientsData.size());
                        updateStatus("Данные успешно загружены");
                        progressBar.setValue(100);
                    } catch (Exception e) {
                        log.error("Ошибка загрузки данных из XML: " + e.getMessage(), e);
                        JOptionPane.showMessageDialog(mainFrame, 
                            "Ошибка загрузки: " + e.getMessage(), 
                            "Ошибка", 
                            JOptionPane.ERROR_MESSAGE);
                        updateStatus("Ошибка загрузки данных");
                    }
                });
                
                synchronized (loadMonitor) {
                    dataLoaded = true;
                    loadMonitor.notifyAll();
                    log.debug("Установлен флаг dataLoaded и уведомлены ожидающие потоки");
                }
                
            } catch (Exception e) {
                log.error("Критическая ошибка в потоке загрузки данных: " + e.getMessage(), e);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Ошибка загрузки: " + e.getMessage(), 
                        "Ошибка", 
                        JOptionPane.ERROR_MESSAGE);
                });
                updateStatus("Ошибка загрузки данных");
            }
            log.info("Поток загрузки данных завершил выполнение");
        }
    }
    
    /**
     * Поток для редактирования данных и формирования XML-файла.
     * DEBUG: детали процесса редактирования и синхронизации
     * INFO: основные этапы редактирования
     * ERROR: ошибки редактирования данных
     */
    private class DataEditThread extends Thread {
        private String filename;
        
        public DataEditThread(String filename) {
            super("DataEditThread");
            this.filename = filename;
            log.debug("Создан поток редактирования данных для файла: " + filename);
        }
        
        @Override
        public void run() {
            log.info("Поток редактирования данных начал выполнение");
            try {
                // DEBUG: Ожидаем загрузки данных
                synchronized (loadMonitor) {
                    log.debug("Ожидание загрузки данных...");
                    while (!dataLoaded) {
                        updateStatus("Ожидание загрузки данных...");
                        loadMonitor.wait();
                    }
                    log.debug("Данные загружены, продолжение работы потока редактирования");
                }
                
                updateStatus("Редактирование данных...");
                progressBar.setValue(0);
                
                // DEBUG: Имитация редактирования данных с логированием прогресса
                for (int i = 0; i <= 100; i += 25) {
                    Thread.sleep(300);
                    progressBar.setValue(i);
                    log.debug("Прогресс редактирования данных: " + i + "%");
                }
                
                // Загружаем текущие данные
                List<String[]> currentData = loadCurrentDataFromXML(filename);
                log.debug("Загружено клиентов для редактирования: " + currentData.size());
                
                // DEBUG: Добавляем префикс к фамилиям для демонстрации редактирования
                List<String[]> editedData = new ArrayList<>();
                for (String[] client : currentData) {
                    String[] editedClient = client.clone();
                    String name = editedClient[0].replace("\"", "");
                    editedClient[0] = "Edited_" + name;
                    editedData.add(editedClient);
                    log.debug("Отредактирован клиент: " + name + " -> " + editedClient[0]);
                }
                
                // Сохраняем отредактированные данные
                saveEditedDataToXML(editedData, filename);
                log.info("Отредактированные данные сохранены в файл: " + filename);
                
                synchronized (editMonitor) {
                    dataEdited = true;
                    editMonitor.notifyAll();
                    log.debug("Установлен флаг dataEdited и уведомлены ожидающие потоки");
                }
                
                updateStatus("Данные отредактированы и сохранены");
                progressBar.setValue(100);
                
            } catch (Exception e) {
                log.error("Ошибка в потоке редактирования данных: " + e.getMessage(), e);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Ошибка редактирования: " + e.getMessage(), 
                        "Ошибка", 
                        JOptionPane.ERROR_MESSAGE);
                });
                updateStatus("Ошибка редактирования данных");
            }
            log.info("Поток редактирования данных завершил выполнение");
        }
        
        /**
         * Загружает текущие данные из XML файла.
         * DEBUG: детали загрузки каждого клиента
         * INFO: общее количество загруженных клиентов
         * ERROR: ошибки чтения XML файла
         */
        private List<String[]> loadCurrentDataFromXML(String filename) {
            log.debug("Загрузка текущих данных из XML: " + filename);
            List<String[]> data = new ArrayList<>();
            try {
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = dBuilder.parse(new File(filename));
                doc.getDocumentElement().normalize();

                NodeList nlClients = doc.getElementsByTagName("client");
                log.debug("Найдено клиентов в XML: " + nlClients.getLength());

                for (int i = 0; i < nlClients.getLength(); i++) {
                    Node elem = nlClients.item(i);
                    
                    if (elem.getNodeType() == Node.ELEMENT_NODE) {
                        Element clientElement = (Element) elem;
                        
                        String name = getElementText(clientElement, "name");
                        String phone = getElementText(clientElement, "phone");
                        String address = getElementText(clientElement, "address");
                        String newspaper = getElementText(clientElement, "newspaper");

                        String[] client = {name, phone, address, newspaper};
                        data.add(client);
                        log.trace("Загружен клиент: " + name + ", " + phone);
                    }
                }
            } catch (Exception e) {
                log.error("Ошибка загрузки данных из XML: " + e.getMessage(), e);
                throw new RuntimeException("Ошибка загрузки данных из XML", e);
            }
            return data;
        }
    }
    
    /**
     * Поток для построения отчета в HTML-формате.
     * DEBUG: прогресс генерации отчета и синхронизация
     * INFO: успешная генерация отчета
     * ERROR: ошибки генерации отчета
     */
    private class ReportGenerationThread extends Thread {
        private String dataFilename;
        private String template;
        
        public ReportGenerationThread(String dataFilename, String template) {
            super("ReportGenerationThread");
            this.dataFilename = dataFilename;
            this.template = template;
            log.debug("Создан поток генерации отчета, данные: " + dataFilename + ", шаблон: " + template);
        }
        
        @Override
        public void run() {
            log.info("Поток генерации отчета начал выполнение");
            try {
                // DEBUG: Ожидаем завершения редактирования данных
                synchronized (editMonitor) {
                    log.debug("Ожидание завершения редактирования данных...");
                    while (!dataEdited) {
                        updateStatus("Ожидание редактирования данных...");
                        editMonitor.wait();
                    }
                    log.debug("Данные отредактированы, продолжение работы потока генерации отчета");
                }
                
                updateStatus("Генерация отчета...");
                progressBar.setValue(0);
                
                // DEBUG: Имитация генерации отчета с логированием прогресса
                for (int i = 0; i <= 100; i += 33) {
                    Thread.sleep(400);
                    progressBar.setValue(i);
                    log.debug("Прогресс генерации отчета: " + i + "%");
                }
                
                // Генерируем отчет
                generateHTMLReport(dataFilename, template, "multithread_report.html");
                log.info("HTML отчет успешно сгенерирован: multithread_report.html");
                
                updateStatus("Отчет успешно сгенерирован");
                progressBar.setValue(100);
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Многопоточная обработка завершена!\n" +
                        "Файл " + dataFilename + " перезаписан с отредактированными данными.\n" +
                        "Отчет сохранен как: multithread_report.html", 
                        "Успех", 
                        JOptionPane.INFORMATION_MESSAGE);
                });
                
            } catch (Exception e) {
                log.error("Ошибка генерации отчета: " + e.getMessage(), e);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Ошибка генерации отчета: " + e.getMessage(), 
                        "Ошибка", 
                        JOptionPane.ERROR_MESSAGE);
                });
                updateStatus("Ошибка генерации отчета");
            }
            log.info("Поток генерации отчета завершил выполнение");
        }
    }
    
    /**
     * Обновляет статус в GUI.
     * DEBUG: логирование изменений статуса для отладки
     */
    private void updateStatus(String message) {
        log.debug("Обновление статуса: " + message);
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
        });
    }
    
    /**
     * Создает кнопку с иконкой.
     * DEBUG: процесс создания кнопки и загрузки иконки
     * WARN: предупреждения при проблемах с загрузкой иконок
     */
    private JButton createButtonWithIcon(String iconPath, String tooltip) {
        log.debug("Создание кнопки: " + tooltip);
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                Image scaledImage = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
                log.trace("Иконка загружена для кнопки: " + tooltip);
            } else {
                button.setText(tooltip);
                log.warn("Не удалось загрузить иконку для кнопки: " + tooltip + ", используется текст");
            }
        } catch (Exception e) {
            button.setText(tooltip);
            log.warn("Ошибка загрузки иконки для кнопки " + tooltip + ": " + e.getMessage());
        }
        
        button.setPreferredSize(new Dimension(40, 40));
        button.setMargin(new Insets(5, 5, 5, 5));
        
        return button;
    }
    
    /**
     * Создает и отображает главное окно приложения.
     * INFO: запуск приложения и успешное отображение окна
     * DEBUG: процесс создания графических компонентов
     */
    public void show() {
        log.info("Запуск приложения 'Почта России - Управление клиентами'");
        
        // DEBUG: Создание окна
        log.debug("Инициализация главного окна с размером 1000x650");
        mainFrame = new JFrame("Почта России - Управление клиентами (Многопоточная версия)");
        mainFrame.setSize(1000, 650);
        mainFrame.setLocation(300, 150);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // DEBUG: Создание кнопок
        log.debug("Создание графических компонентов");
        save = createButtonWithIcon("./images/SAVE.png", "Сохранить данные клиентов");
        addClient = createButtonWithIcon("./images/ADD.png", "Добавить нового клиента");
        editClient = createButtonWithIcon("./images/EDIT.png", "Изменить данные клиента");
        deleteClient = createButtonWithIcon("./images/Recycle.jpg", "Удалить клиента");
        saveButton = createButtonWithIcon("./images/SAVE.png", "Сохранить в XML");
        loadButton = createButtonWithIcon("./images/LOAD.png", "Загрузить из XML");
        generateReportButton = createButtonWithIcon("./images/REPORT.png", "Сгенерировать отчет в PDF/HTML");
        multiThreadingButton = new JButton("Многопоточная обработка");

        // Кнопки без иконок
        searchButton = new JButton("Поиск");
        resetButton = new JButton("Сброс");

        // DEBUG: Добавление кнопок на панель инструментов
        log.debug("Создание панели инструментов");
        toolBar = new JToolBar("Панель инструментов");
        toolBar.add(save);
        toolBar.addSeparator();
        toolBar.add(addClient);
        toolBar.add(editClient);
        toolBar.add(deleteClient);
        toolBar.addSeparator();
        toolBar.add(loadButton);
        toolBar.add(saveButton);
        toolBar.add(generateReportButton);
        toolBar.addSeparator();
        toolBar.add(multiThreadingButton);

        // Размещение панели инструментов
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(toolBar, BorderLayout.NORTH);

        // DEBUG: Панель статуса и прогресса
        log.debug("Создание панели статуса и прогресса");
        JPanel statusPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        statusLabel = new JLabel("Готов к работе");
        statusPanel.add(progressBar, BorderLayout.NORTH);
        statusPanel.add(statusLabel, BorderLayout.SOUTH);
        mainFrame.add(statusPanel, BorderLayout.SOUTH);

        // DEBUG: Панель клиентов
        log.debug("Создание панели клиентов с таблицей");
        JPanel clientsPanel = new JPanel(new BorderLayout());
        
        // Создание таблицы с данными клиентов
        String[] clientColumns = {"Surname", "Phone", "Address", "Newspaper"};
        String[][] clientData = {
            {"Ivanov", "451-50-70", "Kommunarskaya 22", "Rastishka"},
            {"Petrov", "225-25-52", "Sodovaya 13", "Modelist"},
            {"Sidorov", "789-63-45", "Lesnaya 44", "Basketbolist"},
            {"Kozlov", "111-22-33", "Popova 38", "Elektrik"},
            {"Frolov", "555-66-77", "Sadovaya 5", "Futbolist"}
        };
        
        // Сохраняем исходные данные
        originalClientsData = new ArrayList<>();
        for (String[] client : clientData) {
            originalClientsData.add(client.clone());
        }
        
        log.debug("Инициализация таблицы с " + originalClientsData.size() + " клиентами");
        clientsModel = new DefaultTableModel(clientData, clientColumns);
        clientsTable = new JTable(clientsModel);

        clientsScroll = new JScrollPane(clientsTable);
        clientsPanel.add(clientsScroll, BorderLayout.CENTER);

        // DEBUG: Панель формы для клиентов
        log.debug("Создание панели формы для клиентов");
        JPanel clientFormPanel = new JPanel();
        
        clientFormPanel.add(new JLabel("Фамилия:"));
        surnameComboBox = new JComboBox<>();
        updateSurnameComboBox();
        clientFormPanel.add(surnameComboBox);
        
        clientFormPanel.add(Box.createHorizontalStrut(20));
        
        clientFormPanel.add(new JLabel("Телефон:"));
        clientPhone = new JTextField(10);
        clientFormPanel.add(clientPhone);
        
        clientFormPanel.add(new JLabel("Адрес:"));
        clientAddress = new JTextField(15);
        clientFormPanel.add(clientAddress);
        
        clientFormPanel.add(new JLabel("Газета:"));
        clientNewspaper = new JTextField(10);
        clientFormPanel.add(clientNewspaper);
        
        clientFormPanel.add(searchButton);
        clientFormPanel.add(resetButton);
      
        clientsPanel.add(clientFormPanel, BorderLayout.SOUTH);
        mainFrame.add(clientsPanel, BorderLayout.CENTER);

        addEventHandlers();
        
        log.debug("Отображение главного окна");
        mainFrame.setVisible(true);
        log.info("Главное окно приложения успешно отображено");
    }

    /**
     * Проверяет корректность данных клиента.
     * DEBUG: процесс валидации данных
     * WARN: нарушения правил валидации
     * INFO: успешная валидация данных
     */
    private void ValidateClients(String name, String phone, String address, String newspaper) throws AddClientException {
        log.debug("Валидация данных клиента: name='" + name + "', phone='" + phone + 
                 "', address='" + address + "', newspaper='" + newspaper + "'");
        
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || newspaper.isEmpty()) {
            log.warn("Попытка добавления клиента с пустыми полями");
            throw new AddClientException("Все поля должны быть заполнены!!!");
        }

        if (name.length() < 2) {
            log.warn("Некорректная фамилия клиента: " + name);
            throw new AddClientException("Фамилия не может состоять из 1 символа!");
        }

        if (!phone.matches("\\d{3}-\\d{2}-\\d{2}")) {
            log.warn("Некорректный формат телефона: " + phone);
            throw new AddClientException("Телефон должен быть в формате XXX-XX-XX!");
        }

        if (address.length() < 5) {
            log.warn("Слишком короткий адрес: " + address);
            throw new AddClientException("Адрес слишком короткий!");
        }
        
        log.debug("Данные клиента прошли валидацию успешно");
    }
    
    /**
     * Проверяет корректность параметров поиска.
     * DEBUG: процесс валидации параметров поиска
     * WARN: отсутствие критериев поиска
     */
    private void ValidateSearch(String name, String phone, String address, String newspaper) throws SearchClientException {
        log.debug("Валидация параметров поиска: name='" + name + "', phone='" + phone + 
                 "', address='" + address + "', newspaper='" + newspaper + "'");
        
        if (name.isEmpty() && phone.isEmpty() && address.isEmpty() && newspaper.isEmpty()) {
            log.warn("Попытка поиска без указания критериев");
            throw new SearchClientException("Хотя бы одно поле должно быть заполнено!!!");
        }
        
        log.debug("Параметры поиска прошли валидацию успешно");
    }

    /**
     * Добавляет обработчики событий для всех кнопок.
     * DEBUG: процесс добавления обработчиков
     * INFO: основные действия пользователя
     */
    private void addEventHandlers() {
        log.debug("Добавление обработчиков событий");
    	
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Поиск'");
                performSearch();
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Сброс'");
                resetFilters();
            }
        });
        
        surnameComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) surnameComboBox.getSelectedItem();
                log.debug("Пользователь выбрал фамилию в комбобоксе: " + selected);
                filterBySurname();
            }
        });
        
        addClient.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    log.info("Пользователь нажал кнопку 'Добавить клиента'");
        		addClients();
        	}
        });

        editClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Редактировать клиента'");
                editSelectedClient();
            }
        });

        deleteClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Удалить клиента'");
                deleteSelectedClient();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Сохранить в XML'");
                saveDataToXML();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Загрузить из XML'");
                loadDataFromXML();
            }
        });
        
        generateReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Сгенерировать отчет'");
                generatePDFandHTMLReports();
            }
        });
        
        multiThreadingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Многопоточная обработка'");
                startMultiThreadingProcess();
            }
        });

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.info("Пользователь нажал кнопку 'Сохранить данные клиентов'");
                JOptionPane.showMessageDialog(mainFrame, 
                    "Данные сохранены!", 
                    "Сохранение", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        log.debug("Все обработчики событий добавлены");
    }
    
    /**
     * Запускает многопоточную обработку данных.
     * INFO: основные этапы многопоточной обработки
     * DEBUG: детали запуска потоков
     * WARN: отмена пользователем выбора файлов
     */
    private void startMultiThreadingProcess() {
        log.info("Запуск многопоточной обработки данных");
        
        // Сбрасываем флаги
        dataLoaded = false;
        dataEdited = false;
        progressBar.setValue(0);
        
        // DEBUG: Запрашиваем XML файл для загрузки
        log.debug("Открытие диалога выбора XML файла");
        FileDialog load = new FileDialog(mainFrame, "Выберите XML файл для загрузки и перезаписи", FileDialog.LOAD);
        load.setFile("*.xml");
        load.setVisible(true);

        String directory = load.getDirectory();
        String file = load.getFile();

        if (directory == null || file == null) {
            log.warn("Пользователь отменил выбор XML файла");
            return;
        }
        String inputFile = directory + file;
        log.debug("Выбран XML файл для загрузки: " + inputFile);
        
        // DEBUG: Запрашиваем шаблон для отчета
        log.debug("Открытие диалога выбора шаблона отчета");
        FileDialog templateDialog = new FileDialog(mainFrame, "Выберите шаблон отчета (.jrxml)", FileDialog.LOAD);
        templateDialog.setFile("*.jrxml");
        templateDialog.setVisible(true);

        directory = templateDialog.getDirectory();
        file = templateDialog.getFile();

        if (directory == null || file == null) {
            log.warn("Пользователь отменил выбор шаблона отчета");
            return;
        }
        String templateFile = directory + file;
        log.debug("Выбран шаблон отчета: " + templateFile);
        
        // Запускаем потоки
        DataLoadThread loadThread = new DataLoadThread(inputFile);
        DataEditThread editThread = new DataEditThread(inputFile);
        ReportGenerationThread reportThread = new ReportGenerationThread(inputFile, templateFile);
        
        log.debug("Запуск трех потоков многопоточной обработки");
        loadThread.start();
        editThread.start();
        reportThread.start();
        
        log.info("Все три потока многопоточной обработки запущены");
    }
    
    /**
     * Сохраняет отредактированные данные в XML файл.
     * DEBUG: процесс сохранения каждого клиента
     * INFO: успешное сохранение данных
     * ERROR: ошибки сохранения XML
     */
    private void saveEditedDataToXML(List<String[]> data, String filename) {
        log.info("Сохранение отредактированных данных в XML: " + filename);
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            Element postalClients = doc.createElement("postal_clients");
            doc.appendChild(postalClients);
            
            for (int i = 0; i < data.size(); i++) {
                String[] client = data.get(i);
                Element clientElement = doc.createElement("client");
                postalClients.appendChild(clientElement);
                
                clientElement.setAttribute("id", String.valueOf(i + 1));
                
                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode(client[0]));
                clientElement.appendChild(name);
                
                Element phone = doc.createElement("phone");
                phone.appendChild(doc.createTextNode(client[1]));
                clientElement.appendChild(phone);
                
                Element address = doc.createElement("address");
                address.appendChild(doc.createTextNode(client[2]));
                clientElement.appendChild(address);
                
                Element newspaper = doc.createElement("newspaper");
                newspaper.appendChild(doc.createTextNode(client[3]));
                clientElement.appendChild(newspaper);
                
                log.trace("Сохранен клиент в XML: " + client[0] + ", " + client[1]);
            }
            
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            try (FileWriter fw = new FileWriter(filename)) {
                trans.transform(new DOMSource(doc), new StreamResult(fw));
            }
            
            log.info("Отредактированные данные успешно сохранены в XML, клиентов: " + data.size());
            
        } catch (Exception e) {
            log.error("Ошибка сохранения отредактированных данных в XML: " + e.getMessage(), e);
            throw new RuntimeException("Ошибка сохранения XML", e);
        }
    }
    
    /**
     * Генерирует HTML отчет.
     * DEBUG: процесс генерации отчета
     * INFO: успешная генерация HTML отчета
     * ERROR: ошибки генерации отчета
     */
    private void generateHTMLReport(String datasource, String template, String resultpath) {
        log.info("Генерация HTML отчета, данные: " + datasource + ", шаблон: " + template);
        try {
            JRDataSource ds = new JRXmlDataSource(datasource, "/postal_clients/client");
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), ds);
            
            JasperExportManager.exportReportToHtmlFile(print, resultpath);
            log.info("HTML отчет успешно сгенерирован: " + resultpath);
            
        } catch (Exception ex) {
            log.error("Ошибка генерации HTML отчета: " + ex.getMessage(), ex);
            throw new RuntimeException("Ошибка генерации отчета", ex);
        }
    }

    /**
     * Редактирует выбранного клиента.
     * DEBUG: процесс редактирования клиента
     * INFO: успешное редактирование
     * WARN: ошибки валидации при редактировании
     */
    private void editSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            log.warn("Попытка редактирования без выбранного клиента");
            JOptionPane.showMessageDialog(mainFrame, 
                "Пожалуйста, выберите клиента для редактирования", 
                "Ошибка", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentName = (String) clientsModel.getValueAt(selectedRow, 0);
        String currentPhone = (String) clientsModel.getValueAt(selectedRow, 1);
        String currentAddress = (String) clientsModel.getValueAt(selectedRow, 2);
        String currentNewspaper = (String) clientsModel.getValueAt(selectedRow, 3);

        log.info("Редактирование клиента: " + currentName + ", " + currentPhone);

        JDialog editDialog = new JDialog(mainFrame, "Редактирование клиента", true);
        editDialog.setSize(450, 350);
        editDialog.setLocationRelativeTo(mainFrame);
        editDialog.setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField(currentName);
        JTextField phoneField = new JTextField(currentPhone);
        JTextField addressField = new JTextField(currentAddress);
        JTextField newspaperField = new JTextField(currentNewspaper);
        
        inputPanel.add(new JLabel("Фамилия:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Телефон (XXX-XX-XX):"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Адрес:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Газета:"));
        inputPanel.add(newspaperField);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        editDialog.add(inputPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String newspaper = newspaperField.getText().trim();
                
                log.debug("Сохранение отредактированных данных клиента: " + name);
                
                try {
                    ValidateClients(name, phone, address, newspaper);

                    clientsModel.setValueAt(name, selectedRow, 0);
                    clientsModel.setValueAt(phone, selectedRow, 1);
                    clientsModel.setValueAt(address, selectedRow, 2);
                    clientsModel.setValueAt(newspaper, selectedRow, 3);
                    
                    for (String[] client : originalClientsData) {
                        if (client[0].equals(currentName) && client[1].equals(currentPhone)) {
                            client[0] = name;
                            client[1] = phone;
                            client[2] = address;
                            client[3] = newspaper;
                            break;
                        }
                    }
                    
                    updateSurnameComboBox();
                    editDialog.dispose();
                    
                    log.info("Клиент успешно отредактирован: " + name + ", " + phone);
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Данные клиента успешно обновлены!", 
                        "Успех", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                catch (AddClientException ex) {
                    log.warn("Ошибка редактирования клиента: " + ex.getMessage());
                    JOptionPane.showMessageDialog(editDialog, ex.getMessage(), "Ошибка редактирования", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.debug("Отмена редактирования клиента");
                editDialog.dispose();
            }
        });
        
        editDialog.setVisible(true);
    }

    /**
     * Удаляет выбранного клиента.
     * DEBUG: процесс удаления клиента
     * INFO: успешное удаление
     * WARN: отмена удаления пользователем
     */
    private void deleteSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            log.warn("Попытка удаления без выбранного клиента");
            JOptionPane.showMessageDialog(mainFrame, 
                "Пожалуйста, выберите клиента для удаления", 
                "Ошибка", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String clientName = (String) clientsModel.getValueAt(selectedRow, 0);
        String clientPhone = (String) clientsModel.getValueAt(selectedRow, 1);
        
        log.info("Подтверждение удаления клиента: " + clientName + ", " + clientPhone);
        
        int confirm = JOptionPane.showConfirmDialog(mainFrame, 
            "Вы уверены, что хотите удалить клиента " + clientName + "?", 
            "Подтверждение удаления", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            clientsModel.removeRow(selectedRow);
            
            String[] clientToRemove = null;
            for (String[] client : originalClientsData) {
                if (client[0].equals(clientName) && client[1].equals(clientPhone)) {
                    clientToRemove = client;
                    break;
                }
            }
            if (clientToRemove != null) {
                originalClientsData.remove(clientToRemove);
            }
            
            updateSurnameComboBox();
            
            log.info("Клиент успешно удален: " + clientName + ", " + clientPhone);
            JOptionPane.showMessageDialog(mainFrame, 
                "Клиент успешно удален!", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            log.debug("Удаление клиента отменено пользователем");
        }
    }

    /**
     * Генерирует отчеты в PDF и HTML форматах.
     * DEBUG: процесс выбора формата отчета
     * INFO: успешная генерация отчетов
     * ERROR: ошибки генерации отчетов
     */
    private void generatePDFandHTMLReports() {
        log.info("Открытие диалога выбора формата отчета");
        try {
            JDialog chooseDialog = new JDialog(mainFrame, "Выберите вариант отчёта", true);
            chooseDialog.setSize(300, 400);
            chooseDialog.setLocationRelativeTo(mainFrame);
            chooseDialog.setLayout(new BorderLayout());

            JButton saveToPDF = new JButton("PDF");
            JButton saveToHtml = new JButton("HTML");

            saveToPDF.setPreferredSize(new Dimension(100, 70));
            saveToHtml.setPreferredSize(new Dimension(100, 70));

            java.awt.Font buttonFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 16);
            saveToPDF.setFont(buttonFont);
            saveToHtml.setFont(buttonFont);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(55, 20, 20, 20));

            buttonPanel.add(saveToPDF);
            buttonPanel.add(saveToHtml);

            chooseDialog.add(buttonPanel, BorderLayout.CENTER);

            saveToPDF.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    log.info("Пользователь выбрал формат PDF для отчета");
                    flag = "CHOOSE_PDF";
                    chooseDialog.dispose();
                    generateReportWithFlag();
                }
            });

            saveToHtml.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    log.info("Пользователь выбрал формат HTML для отчета");
                    flag = "CHOOSE_HTML";
                    chooseDialog.dispose();
                    generateReportWithFlag();
                }
            });

            chooseDialog.setVisible(true);
        } 
        catch (Exception ex) {
            log.error("Ошибка при открытии диалога выбора формата отчета: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при генерации отчета: " + ex.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Генерирует отчет с выбранным флагом формата.
     * DEBUG: процесс генерации отчета с временными файлами
     * INFO: успешная генерация отчета
     * ERROR: ошибки генерации отчета
     */
    private void generateReportWithFlag() {
        log.info("Генерация отчета с флагом: " + flag);
        try {
            File tempFile = File.createTempFile("clients", ".xml");
            saveDataToXML(tempFile.getAbsolutePath());
            log.debug("Создан временный XML файл: " + tempFile.getAbsolutePath());
            
            FileDialog openFile2 = new FileDialog(mainFrame, "Укажите путь .jrxml к шаблону", FileDialog.LOAD);
            openFile2.setFile("*.jrxml");
            openFile2.setVisible(true);

            String directory = openFile2.getDirectory();
            String file_name = openFile2.getFile();

            if (directory == null || file_name == null) {
                log.warn("Пользователь отменил выбор шаблона отчета");
                return;
            }

            String template = directory + file_name;
            log.debug("Выбран шаблон отчета: " + template);

            FileDialog openFile3 = new FileDialog(mainFrame, "Укажите место сохранения", FileDialog.SAVE);
            
            if ("CHOOSE_PDF".equals(flag)) {
                openFile3.setFile("*.pdf");
            } else {
                openFile3.setFile("*.html");
            }
            
            openFile3.setVisible(true);

            directory = openFile3.getDirectory();
            file_name = openFile3.getFile();

            if (directory == null || file_name == null) {
                log.warn("Пользователь отменил выбор места сохранения");
                return;
            }

            String resultpath = directory + file_name;
            log.debug("Выбран путь сохранения отчета: " + resultpath);

            generateReport(tempFile.getAbsolutePath(), "/postal_clients/client", template, resultpath);
            
            tempFile.delete();
            log.debug("Временный файл удален: " + tempFile.getAbsolutePath());
        } 
        catch (Exception ex) {
            log.error("Ошибка при генерации отчета: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при генерации отчета: " + ex.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Генерирует отчет в выбранном формате.
     * DEBUG: процесс компиляции и заполнения отчета
     * INFO: успешная генерация отчета в выбранном формате
     * ERROR: ошибки при работе с JasperReports
     */
    private void generateReport(String datasource, String xpath, String template, String resultpath) {
        log.info("Генерация отчета, данные: " + datasource + ", шаблон: " + template);
        try {
            JRDataSource ds = new JRXmlDataSource(datasource, xpath);
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), ds);
            
            if ("CHOOSE_PDF".equals(flag)) {
                JasperExportManager.exportReportToPdfFile(print, resultpath);
                log.info("PDF отчет успешно создан: " + resultpath);
                JOptionPane.showMessageDialog(mainFrame, "PDF отчёт успешно создан!\n" + resultpath, "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else if ("CHOOSE_HTML".equals(flag)) {
                JasperExportManager.exportReportToHtmlFile(print, resultpath);
                log.info("HTML отчет успешно создан: " + resultpath);
                JOptionPane.showMessageDialog(mainFrame, "HTML отчёт успешно создан!\n" + resultpath, "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        } 
        catch (Exception ex) {
            log.error("Ошибка при генерации отчета: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при генерации отчета: " + ex.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Выполняет поиск клиентов по заданным критериям.
     * DEBUG: параметры поиска и процесс фильтрации
     * INFO: результаты поиска
     * WARN: клиенты не найдены
     */
    private void performSearch() {
        String selectedSurname = (String) surnameComboBox.getSelectedItem();
        String phone = clientPhone.getText().trim();
        String address = clientAddress.getText().toLowerCase().trim();
        String newspaper = clientNewspaper.getText().toLowerCase().trim();

        log.debug("Выполнение поиска с параметрами: фамилия=" + selectedSurname + 
                 ", телефон=" + phone + ", адрес=" + address + ", газета=" + newspaper);

        try {
            ValidateSearch(selectedSurname.equals("Все") ? "" : selectedSurname, phone, address, newspaper);

            clientsModel.setRowCount(0);

            int foundCount = 0;
            for (String[] client : originalClientsData) {
                boolean matches = true;
                
                if (!"Все".equals(selectedSurname) && !client[0].equals(selectedSurname)) {
                    matches = false;
                }
                
                if (!phone.isEmpty() && !client[1].equals(phone)) {
                    matches = false;
                }
                
                if (!address.isEmpty() && !client[2].toLowerCase().contains(address)) {
                    matches = false;
                }
                
                if (!newspaper.isEmpty() && !client[3].toLowerCase().contains(newspaper)) {
                    matches = false;
                }
                
                if (matches) {
                    clientsModel.addRow(client);
                    foundCount++;
                    log.trace("Найден клиент: " + Arrays.toString(client));
                }
            }
            
            log.info("Поиск завершен, найдено клиентов: " + foundCount);
            
            if (clientsModel.getRowCount() == 0) {
                log.debug("Клиенты по заданным критериям не найдены");
                JOptionPane.showMessageDialog(mainFrame, 
                    "Клиенты по заданным критериям не найдены", 
                    "Результат поиска", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (SearchClientException ex) {
            log.warn("Ошибка поиска клиентов: " + ex.getMessage());
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Ошибка поиска", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Фильтрует клиентов по фамилии.
     * DEBUG: процесс фильтрации и количество отфильтрованных клиентов
     */
    private void filterBySurname() {
        String selectedSurname = (String) surnameComboBox.getSelectedItem();
        
        log.debug("Фильтрация по фамилии: " + selectedSurname);
        
        clientsModel.setRowCount(0);
        
        if ("Все".equals(selectedSurname)) {
            for (String[] client : originalClientsData) {
                clientsModel.addRow(client);
            }
            log.debug("Показаны все клиенты: " + originalClientsData.size());
        } else {
            int count = 0;
            for (String[] client : originalClientsData) {
                if (client[0].equals(selectedSurname)) {
                    clientsModel.addRow(client);
                    count++;
                }
            }
            log.debug("Показаны клиенты с фамилией " + selectedSurname + ": " + count);
        }
    }
    
    /**
     * Сбрасывает все фильтры поиска.
     * DEBUG: сброс полей фильтрации и восстановление полного списка
     */
    private void resetFilters() {
        log.debug("Сброс всех фильтров");
        clientPhone.setText("");
        clientAddress.setText("");
        clientNewspaper.setText("");
        surnameComboBox.setSelectedIndex(0);
        
        clientsModel.setRowCount(0);
        for (String[] client : originalClientsData) {
            clientsModel.addRow(client);
        }
        log.debug("Все фильтры сброшены, показаны все клиенты: " + originalClientsData.size());
    }
    
    /**
     * Обновляет комбобокс фамилий.
     * DEBUG: процесс обновления комбобокса и количество уникальных фамилий
     * TRACE: детали добавления каждой фамилии
     */
    private void updateSurnameComboBox() {
        log.trace("Начало обновления комбобокса фамилий");
        String selected = (String) surnameComboBox.getSelectedItem();
        
        surnameComboBox.removeAllItems();
        surnameComboBox.addItem("Все");
        
        Set<String> uniqueSurnames = new TreeSet<>();
        for (String[] client : originalClientsData) {
            uniqueSurnames.add(client[0]);
            log.trace("Добавлена фамилия в список: " + client[0]);
        }
        
        for (String surname : uniqueSurnames) {
            surnameComboBox.addItem(surname);
        }
        
        if (selected != null) {
            for (int i = 0; i < surnameComboBox.getItemCount(); i++) {
                if (selected.equals(surnameComboBox.getItemAt(i))) {
                    surnameComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        log.debug("Обновлен комбобокс фамилий, уникальных фамилий: " + uniqueSurnames.size());
    }
    
    /**
     * Добавляет нового клиента.
     * DEBUG: процесс добавления клиента и валидации данных
     * INFO: успешное добавление клиента
     * WARN: ошибки валидации при добавлении
     */
    private void addClients() {
    	JDialog addDialog = new JDialog(mainFrame, "Добавление нового клиента", true);
    	addDialog.setSize(450, 350);
    	addDialog.setLocationRelativeTo(mainFrame);
    	addDialog.setLayout(new BorderLayout());
    	
    	JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
    	inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    	
    	JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField newspaperField = new JTextField();
        
        inputPanel.add(new JLabel("Фамилия:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Телефон (XXX-XX-XX):"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Адрес:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Газета:"));
        inputPanel.add(newspaperField);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        addDialog.add(inputPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String newspaper = newspaperField.getText().trim();
                
                log.debug("Попытка добавления клиента: " + name + ", " + phone);
                
                try {
                    ValidateClients(name, phone, address, newspaper);

                    String[] newClient = {name, phone, address, newspaper};
                    clientsModel.addRow(newClient);
                    originalClientsData.add(newClient);
                    updateSurnameComboBox();
                    
                    addDialog.dispose();
                    
                    log.info("Клиент успешно добавлен: " + name + ", " + phone);
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Клиент успешно добавлен!", 
                        "Успех", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                catch (AddClientException ex) {
                    log.warn("Ошибка добавления клиента: " + ex.getMessage());
                    JOptionPane.showMessageDialog(addDialog, ex.getMessage(), "Ошибка добавления", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                log.debug("Отмена добавления клиента");
                addDialog.dispose();
            }
        });
        
        addDialog.setVisible(true);
    }

    /**
     * Сохраняет данные в XML файл через диалог выбора файла.
     * DEBUG: процесс сохранения через диалог
     * INFO: успешное сохранение данных
     * WARN: отмена сохранения пользователем
     * ERROR: ошибки сохранения XML
     */
    private void saveDataToXML() {
        log.info("Сохранение данных в XML файл через диалог");
        try {
            FileDialog load = new FileDialog(mainFrame, "Выгрузка в XML", FileDialog.SAVE);
            load.setFile("clients_data.xml");
            load.setVisible(true);

            String directory = load.getDirectory();
            String file = load.getFile();

            if (directory == null || file == null) {
                log.warn("Пользователь отменил сохранение XML");
                return;
            }

            String filename = directory + file;
            saveDataToXML(filename);
        }
        catch (Exception e) {
            log.error("Ошибка при сохранении XML: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при сохранении XML: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Сохраняет данные в XML файл.
     * DEBUG: процесс сохранения каждого клиента в XML
     * INFO: успешное сохранение данных с количеством клиентов
     * ERROR: ошибки сохранения XML
     */
    private void saveDataToXML(String filename) {
        log.info("Сохранение данных в XML: " + filename);
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.newDocument();
            Element postalClients = doc.createElement("postal_clients");
            doc.appendChild(postalClients);
            
            for (int i = 0; i < clientsModel.getRowCount(); i++) {
                Element client = doc.createElement("client");
                postalClients.appendChild(client);
                
                client.setAttribute("id", String.valueOf(i + 1));
                
                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode((String)clientsModel.getValueAt(i, 0)));
                client.appendChild(name);
                
                Element phone = doc.createElement("phone");
                phone.appendChild(doc.createTextNode((String)clientsModel.getValueAt(i, 1)));
                client.appendChild(phone);
                
                Element address = doc.createElement("address");
                address.appendChild(doc.createTextNode((String)clientsModel.getValueAt(i, 2)));
                client.appendChild(address);
                
                Element newspaper = doc.createElement("newspaper");
                newspaper.appendChild(doc.createTextNode((String)clientsModel.getValueAt(i, 3)));
                client.appendChild(newspaper);
                
                log.trace("Сохранен клиент в XML: " + clientsModel.getValueAt(i, 0) + ", " + clientsModel.getValueAt(i, 1));
            }
            
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            try (FileWriter fw = new FileWriter(filename)) {
                trans.transform(new DOMSource(doc), new StreamResult(fw));
            }
            
            log.info("Данные успешно сохранены в XML, клиентов: " + clientsModel.getRowCount());
            JOptionPane.showMessageDialog(mainFrame, 
                "Данные успешно сохранены в XML!", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e) {
            log.error("Ошибка сохранения XML: " + e.getMessage(), e);
            throw new RuntimeException("Ошибка сохранения XML", e);
        }
    }

    /**
     * Загружает данные из XML файла через диалог выбора файла.
     * DEBUG: процесс загрузки через диалог
     * INFO: успешная загрузка данных
     * WARN: отмена загрузки пользователем
     * ERROR: ошибки загрузки XML
     */
    private void loadDataFromXML() {
        log.info("Загрузка данных из XML файла через диалог");
        FileDialog load = new FileDialog(mainFrame, "Загрузка из XML", FileDialog.LOAD);
        load.setFile("*.xml");
        load.setVisible(true);

        String directory = load.getDirectory();
        String file = load.getFile();

        if (directory == null || file == null) {
            log.warn("Пользователь отменил загрузку XML");
            return;
        }

        String filename = directory + file;
        log.debug("Выбран файл для загрузки: " + filename);

        try {
            clientsModel.setRowCount(0);
            originalClientsData.clear();

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filename));
            doc.getDocumentElement().normalize();

            NodeList nlClients = doc.getElementsByTagName("client");
            log.debug("Найдено клиентов в XML: " + nlClients.getLength());

            for (int i = 0; i < nlClients.getLength(); i++) {
                Node elem = nlClients.item(i);
                
                if (elem.getNodeType() == Node.ELEMENT_NODE) {
                    Element clientElement = (Element) elem;
                    
                    String name = getElementText(clientElement, "name");
                    String phone = getElementText(clientElement, "phone");
                    String address = getElementText(clientElement, "address");
                    String newspaper = getElementText(clientElement, "newspaper");

                    String[] client = {name, phone, address, newspaper};
                    clientsModel.addRow(client);
                    originalClientsData.add(client);
                    log.trace("Загружен клиент: " + name + ", " + phone);
                }
            }
            
            updateSurnameComboBox();
            
            log.info("Данные успешно загружены из XML, клиентов: " + originalClientsData.size());
            JOptionPane.showMessageDialog(mainFrame, 
                "Данные успешно загружены из XML!", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);            
        }
        catch (Exception e) {
            log.error("Ошибка при загрузке XML: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при загрузке XML: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Загружает данные из указанного XML файла.
     * DEBUG: процесс загрузки данных из файла
     * INFO: успешная загрузка с количеством клиентов
     * ERROR: ошибки загрузки XML
     */
    private void loadDataFromXML(String filename) {
        log.info("Загрузка данных из XML: " + filename);
        try {
            clientsModel.setRowCount(0);
            originalClientsData.clear();

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filename));
            doc.getDocumentElement().normalize();

            NodeList nlClients = doc.getElementsByTagName("client");
            log.debug("Найдено клиентов в XML: " + nlClients.getLength());

            for (int i = 0; i < nlClients.getLength(); i++) {
                Node elem = nlClients.item(i);
                
                if (elem.getNodeType() == Node.ELEMENT_NODE) {
                    Element clientElement = (Element) elem;
                    
                    String name = getElementText(clientElement, "name");
                    String phone = getElementText(clientElement, "phone");
                    String address = getElementText(clientElement, "address");
                    String newspaper = getElementText(clientElement, "newspaper");

                    String[] client = {name, phone, address, newspaper};
                    clientsModel.addRow(client);
                    originalClientsData.add(client);
                    log.trace("Загружен клиент: " + name + ", " + phone);
                }
            }
            
            updateSurnameComboBox();
            log.info("Данные успешно загружены из XML, клиентов: " + originalClientsData.size());
            
        }
        catch (Exception e) {
            log.error("Ошибка загрузки XML: " + e.getMessage(), e);
            throw new RuntimeException("Ошибка загрузки XML", e);
        }
    }
    
    /**
     * Получает текстовое содержимое элемента по имени тега.
     * DEBUG: получение значения элемента XML
     * @param parent родительский элемент
     * @param tagName имя тега
     * @return текстовое содержимое элемента
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent() != null ? node.getTextContent().trim() : "";
        }
        return "";
    }

    /**
     * Точка входа в приложение.
     * INFO: запуск приложения
     * DEBUG: дополнительная информация о запуске
     */
    public static void main(String[] args) {
        log.info("=== ЗАПУСК ПРИЛОЖЕНИЯ ПОЧТА РОССИИ ===");
        log.debug("Текущее время запуска: " + new java.util.Date());
        
        // Проверка работы логирования

        log.debug("DEBUG сообщение - отладочная информация");
        log.info("INFO сообщение - основная информация");
        log.warn("WARN сообщение - предупреждение");
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.debug("Создание и отображение GUI в Event Dispatch Thread");
                new Proga().show();
            }
        });
    }
}
package Laba08;

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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import org.w3c.dom.Document; 
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException; 
import javax.xml.transform.TransformerFactory; 
import javax.xml.transform.dom.DOMSource; 
import javax.xml.transform.stream.StreamResult; 
import java.io.File; 
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.ParserConfigurationException;

// Импорты для JasperReports
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

/**
 * Главный класс приложения "Почта России - Управление клиентами".
 * Предоставляет графический интерфейс для управления клиентской базой данных,
 * включая добавление, редактирование, удаление, поиск клиентов,
 * а также экспорт данных в XML и генерацию отчетов в PDF/HTML форматах.
 * 
 * @author Mikhail
 * @version 8.0
 */
public class Proga {
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
     */
    private class AddClientException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public AddClientException(String message) {
            super(message);
        }
    }

    /**
     * Пользовательское исключение для обработки ошибок при поиске клиентов.
     */
    private class SearchClientException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public SearchClientException(String message) {
            super(message);
        }
    }
    
    /**
     * Поток для загрузки данных из XML-файла
     */
    private class DataLoadThread extends Thread {
        private String filename;
        
        public DataLoadThread(String filename) {
            super("DataLoadThread");
            this.filename = filename;
        }
        
        @Override
        public void run() {
            try {
                updateStatus("Загрузка данных из XML...");
                progressBar.setValue(0);
                
                // Имитация длительной загрузки
                for (int i = 0; i <= 100; i += 20) {
                    Thread.sleep(200);
                    progressBar.setValue(i);
                }
                
                // Загрузка данных в GUI
                SwingUtilities.invokeLater(() -> {
                    try {
                        loadDataFromXML(filename);
                        updateStatus("Данные успешно загружены");
                        progressBar.setValue(100);
                    } catch (Exception e) {
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
                }
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Ошибка загрузки: " + e.getMessage(), 
                        "Ошибка", 
                        JOptionPane.ERROR_MESSAGE);
                });
                updateStatus("Ошибка загрузки данных");
            }
        }
    }
    
    /**
     * Поток для редактирования данных и формирования XML-файла
     */
    private class DataEditThread extends Thread {
        private String filename;
        
        public DataEditThread(String filename) {
            super("DataEditThread");
            this.filename = filename;
        }
        
        @Override
        public void run() {
            try {
                // Ожидаем загрузки данных
                synchronized (loadMonitor) {
                    while (!dataLoaded) {
                        updateStatus("Ожидание загрузки данных...");
                        loadMonitor.wait();
                    }
                }
                
                updateStatus("Редактирование данных...");
                progressBar.setValue(0);
                
                // Имитация редактирования данных
                for (int i = 0; i <= 100; i += 25) {
                    Thread.sleep(300);
                    progressBar.setValue(i);
                }
                
                // Загружаем текущие данные
                List<String[]> currentData = loadCurrentDataFromXML(filename);
                
                // Добавляем префикс к фамилиям для демонстрации редактирования
                List<String[]> editedData = new ArrayList<>();
                for (String[] client : currentData) {
                    String[] editedClient = client.clone();
                    // Убираем кавычки если они есть
                    String name = editedClient[0].replace("\"", "");
                    editedClient[0] = "Edited_" + name;
                    editedData.add(editedClient);
                }
                
                // Сохраняем отредактированные данные (перезаписываем файл)
                saveEditedDataToXML(editedData, filename);
                
                synchronized (editMonitor) {
                    dataEdited = true;
                    editMonitor.notifyAll();
                }
                
                updateStatus("Данные отредактированы и сохранены");
                progressBar.setValue(100);
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Ошибка редактирования: " + e.getMessage(), 
                        "Ошибка", 
                        JOptionPane.ERROR_MESSAGE);
                });
                updateStatus("Ошибка редактирования данных");
            }
        }
        
        /**
         * Загружает текущие данные из XML файла
         */
        private List<String[]> loadCurrentDataFromXML(String filename) {
            List<String[]> data = new ArrayList<>();
            try {
                DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = dBuilder.parse(new File(filename));
                doc.getDocumentElement().normalize();

                NodeList nlClients = doc.getElementsByTagName("client");

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
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка загрузки данных из XML", e);
            }
            return data;
        }
    }
    
    /**
     * Поток для построения отчета в HTML-формате
     */
    private class ReportGenerationThread extends Thread {
        private String dataFilename;
        private String template;
        
        public ReportGenerationThread(String dataFilename, String template) {
            super("ReportGenerationThread");
            this.dataFilename = dataFilename;
            this.template = template;
        }
        
        @Override
        public void run() {
            try {
                // Ожидаем завершения редактирования данных
                synchronized (editMonitor) {
                    while (!dataEdited) {
                        updateStatus("Ожидание редактирования данных...");
                        editMonitor.wait();
                    }
                }
                
                updateStatus("Генерация отчета...");
                progressBar.setValue(0);
                
                // Имитация генерации отчета
                for (int i = 0; i <= 100; i += 33) {
                    Thread.sleep(400);
                    progressBar.setValue(i);
                }
                
                // Генерируем отчет
                generateHTMLReport(dataFilename, template, "multithread_report.html");
                
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
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Ошибка генерации отчета: " + e.getMessage(), 
                        "Ошибка", 
                        JOptionPane.ERROR_MESSAGE);
                });
                updateStatus("Ошибка генерации отчета");
            }
        }
    }
    
    /**
     * Обновляет статус в GUI
     */
    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
        });
    }
    
    /**
     * Создает кнопку с иконкой.
     */
    private JButton createButtonWithIcon(String iconPath, String tooltip) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                Image scaledImage = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            } else {
                button.setText(tooltip);
            }
        } catch (Exception e) {
            button.setText(tooltip);
        }
        
        button.setPreferredSize(new Dimension(40, 40));
        button.setMargin(new Insets(5, 5, 5, 5));
        
        return button;
    }
    
    /**
     * Создает и отображает главное окно приложения.
     */
    public void show() {
        // Создание окна
        mainFrame = new JFrame("Почта России - Управление клиентами (Многопоточная версия)");
        mainFrame.setSize(1000, 650);
        mainFrame.setLocation(300, 150);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создание кнопок
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

        // Добавление кнопок на панель инструментов
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

        // Панель статуса и прогресса
        JPanel statusPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        statusLabel = new JLabel("Готов к работе");
        statusPanel.add(progressBar, BorderLayout.NORTH);
        statusPanel.add(statusLabel, BorderLayout.SOUTH);
        mainFrame.add(statusPanel, BorderLayout.SOUTH);

        // Панель клиентов
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
        
        clientsModel = new DefaultTableModel(clientData, clientColumns);
        clientsTable = new JTable(clientsModel);

        clientsScroll = new JScrollPane(clientsTable);
        clientsPanel.add(clientsScroll, BorderLayout.CENTER);

        // Панель формы для клиентов
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
        mainFrame.setVisible(true);
    }

    /**
     * Проверяет корректность данных клиента
     */
    private void ValidateClients(String name, String phone, String address, String newspaper) throws AddClientException {
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || newspaper.isEmpty()) {
            throw new AddClientException("Все поля должны быть заполнены!!!");
        }

        if (name.length() < 2) throw new AddClientException("Фамилия не может состоять из 1 символа!");

        if (!phone.matches("\\d{3}-\\d{2}-\\d{2}")) {
            throw new AddClientException("Телефон должен быть в формате XXX-XX-XX!");
        }

        if (address.length() < 5) throw new AddClientException("Адрес слишком короткий!");
    }
    
    /**
     * Проверяет корректность параметров поиска.
     */
    private void ValidateSearch(String name, String phone, String address, String newspaper) throws SearchClientException {
        if (name.isEmpty() && phone.isEmpty() && address.isEmpty() && newspaper.isEmpty()) {
            throw new SearchClientException("Хотя бы одно поле должно быть заполнено!!!");
        }
    }

    /**
     * Добавляет обработчики событий
     */
    private void addEventHandlers() {
    	
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetFilters();
            }
        });
        
        surnameComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterBySurname();
            }
        });
        
        addClient.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		addClients();
        	}
        });

        editClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedClient();
            }
        });

        deleteClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedClient();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDataToXML();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadDataFromXML();
            }
        });
        
        generateReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generatePDFandHTMLReports();
            }
        });
        
        multiThreadingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startMultiThreadingProcess();
            }
        });

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Данные сохранены!", 
                    "Сохранение", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
    
    /**
     * Запускает многопоточную обработку данных
     */
    private void startMultiThreadingProcess() {
        // Сбрасываем флаги
        dataLoaded = false;
        dataEdited = false;
        progressBar.setValue(0);
        
        // Запрашиваем XML файл для загрузки
        FileDialog load = new FileDialog(mainFrame, "Выберите XML файл для загрузки и перезаписи", FileDialog.LOAD);
        load.setFile("*.xml");
        load.setVisible(true);

        String directory = load.getDirectory();
        String file = load.getFile();

        if (directory == null || file == null) return;
        String inputFile = directory + file;
        
        // Запрашиваем шаблон для отчета
        FileDialog templateDialog = new FileDialog(mainFrame, "Выберите шаблон отчета (.jrxml)", FileDialog.LOAD);
        templateDialog.setFile("*.jrxml");
        templateDialog.setVisible(true);

        directory = templateDialog.getDirectory();
        file = templateDialog.getFile();

        if (directory == null || file == null) return;
        String templateFile = directory + file;
        
        // Запускаем потоки - используем тот же файл для ввода и вывода (перезапись)
        DataLoadThread loadThread = new DataLoadThread(inputFile);
        DataEditThread editThread = new DataEditThread(inputFile); // Перезаписываем исходный файл
        ReportGenerationThread reportThread = new ReportGenerationThread(inputFile, templateFile);
        
        loadThread.start();
        editThread.start();
        reportThread.start();
    }
    
    /**
     * Сохраняет отредактированные данные в XML файл
     */
    private void saveEditedDataToXML(List<String[]> data, String filename) {
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
            }
            
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            try (FileWriter fw = new FileWriter(filename)) {
                trans.transform(new DOMSource(doc), new StreamResult(fw));
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения XML", e);
        }
    }
    
    /**
     * Генерирует HTML отчет
     */
    private void generateHTMLReport(String datasource, String template, String resultpath) {
        try {
            JRDataSource ds = new JRXmlDataSource(datasource, "/postal_clients/client");
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), ds);
            
            JasperExportManager.exportReportToHtmlFile(print, resultpath);
            
        } catch (Exception ex) {
            throw new RuntimeException("Ошибка генерации отчета", ex);
        }
    }

    // Остальные методы...
    
    private void editSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
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
                    
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Данные клиента успешно обновлены!", 
                        "Успех", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                catch (AddClientException ex) {
                    JOptionPane.showMessageDialog(editDialog, ex.getMessage(), "Ошибка редактирования", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editDialog.dispose();
            }
        });
        
        editDialog.setVisible(true);
    }

    private void deleteSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Пожалуйста, выберите клиента для удаления", 
                "Ошибка", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String clientName = (String) clientsModel.getValueAt(selectedRow, 0);
        String clientPhone = (String) clientsModel.getValueAt(selectedRow, 1);
        
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
            
            JOptionPane.showMessageDialog(mainFrame, 
                "Клиент успешно удален!", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void generatePDFandHTMLReports() {
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
                    flag = "CHOOSE_PDF";
                    chooseDialog.dispose();
                    generateReportWithFlag();
                }
            });

            saveToHtml.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    flag = "CHOOSE_HTML";
                    chooseDialog.dispose();
                    generateReportWithFlag();
                }
            });

            chooseDialog.setVisible(true);
        } 
        catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при генерации отчета: " + ex.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateReportWithFlag() {
        try {
            File tempFile = File.createTempFile("clients", ".xml");
            saveDataToXML(tempFile.getAbsolutePath());
            
            FileDialog openFile2 = new FileDialog(mainFrame, "Укажите путь .jrxml к шаблону", FileDialog.LOAD);
            openFile2.setFile("*.jrxml");
            openFile2.setVisible(true);

            String directory = openFile2.getDirectory();
            String file_name = openFile2.getFile();

            if (directory == null || file_name == null) return;

            String template = directory + file_name;

            FileDialog openFile3 = new FileDialog(mainFrame, "Укажите место сохранения", FileDialog.SAVE);
            
            if ("CHOOSE_PDF".equals(flag)) {
                openFile3.setFile("*.pdf");
            } else {
                openFile3.setFile("*.html");
            }
            
            openFile3.setVisible(true);

            directory = openFile3.getDirectory();
            file_name = openFile3.getFile();

            if (directory == null || file_name == null) return;

            String resultpath = directory + file_name;

            generateReport(tempFile.getAbsolutePath(), "/postal_clients/client", template, resultpath);
            
            tempFile.delete();
        } 
        catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при генерации отчета: " + ex.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateReport(String datasource, String xpath, String template, String resultpath) {
        try {
            JRDataSource ds = new JRXmlDataSource(datasource, xpath);
            JasperReport report = JasperCompileManager.compileReport(template);
            JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), ds);
            
            if ("CHOOSE_PDF".equals(flag)) {
                JasperExportManager.exportReportToPdfFile(print, resultpath);
                JOptionPane.showMessageDialog(mainFrame, "PDF отчёт успешно создан!\n" + resultpath, "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else if ("CHOOSE_HTML".equals(flag)) {
                JasperExportManager.exportReportToHtmlFile(print, resultpath);
                JOptionPane.showMessageDialog(mainFrame, "HTML отчёт успешно создан!\n" + resultpath, "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        } 
        catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при генерации отчета: " + ex.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performSearch() {
        String selectedSurname = (String) surnameComboBox.getSelectedItem();
        String phone = clientPhone.getText().trim();
        String address = clientAddress.getText().toLowerCase().trim();
        String newspaper = clientNewspaper.getText().toLowerCase().trim();

        try {
            ValidateSearch(selectedSurname.equals("Все") ? "" : selectedSurname, phone, address, newspaper);

            clientsModel.setRowCount(0);

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
                }
            }
            
            if (clientsModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Клиенты по заданным критериям не найдены", 
                    "Результат поиска", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (SearchClientException ex) {
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Ошибка поиска", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void filterBySurname() {
        String selectedSurname = (String) surnameComboBox.getSelectedItem();
        
        clientsModel.setRowCount(0);
        
        if ("Все".equals(selectedSurname)) {
            for (String[] client : originalClientsData) {
                clientsModel.addRow(client);
            }
        } else {
            for (String[] client : originalClientsData) {
                if (client[0].equals(selectedSurname)) {
                    clientsModel.addRow(client);
                }
            }
        }
    }
    
    private void resetFilters() {
        clientPhone.setText("");
        clientAddress.setText("");
        clientNewspaper.setText("");
        surnameComboBox.setSelectedIndex(0);
        
        clientsModel.setRowCount(0);
        for (String[] client : originalClientsData) {
            clientsModel.addRow(client);
        }
    }
    
    private void updateSurnameComboBox() {
        String selected = (String) surnameComboBox.getSelectedItem();
        
        surnameComboBox.removeAllItems();
        surnameComboBox.addItem("Все");
        
        Set<String> uniqueSurnames = new TreeSet<>();
        for (String[] client : originalClientsData) {
            uniqueSurnames.add(client[0]);
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
    }
    
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
                
                try {
                    ValidateClients(name, phone, address, newspaper);

                    String[] newClient = {name, phone, address, newspaper};
                    clientsModel.addRow(newClient);
                    originalClientsData.add(newClient);
                    updateSurnameComboBox();
                    
                    addDialog.dispose();
                    
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Клиент успешно добавлен!", 
                        "Успех", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                catch (AddClientException ex) {
                    JOptionPane.showMessageDialog(addDialog, ex.getMessage(), "Ошибка добавления", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDialog.dispose();
            }
        });
        
        addDialog.setVisible(true);
    }

    private void saveDataToXML() {
        try {
            FileDialog load = new FileDialog(mainFrame, "Выгрузка в XML", FileDialog.SAVE);
            load.setFile("clients_data.xml");
            load.setVisible(true);

            String directory = load.getDirectory();
            String file = load.getFile();

            if (directory == null || file == null) return;

            saveDataToXML(directory + file);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при сохранении XML: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveDataToXML(String filename) {
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
            }
            
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            try (FileWriter fw = new FileWriter(filename)) {
                trans.transform(new DOMSource(doc), new StreamResult(fw));
            }
            
            JOptionPane.showMessageDialog(mainFrame, 
                "Данные успешно сохранены в XML!", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения XML", e);
        }
    }

    private void loadDataFromXML() {
        FileDialog load = new FileDialog(mainFrame, "Загрузка из XML", FileDialog.LOAD);
        load.setFile("*.xml");
        load.setVisible(true);

        String directory = load.getDirectory();
        String file = load.getFile();

        if (directory == null || file == null) return;

        String filename = directory + file;

        try {
            clientsModel.setRowCount(0);
            originalClientsData.clear();

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filename));
            doc.getDocumentElement().normalize();

            NodeList nlClients = doc.getElementsByTagName("client");

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
                }
            }
            
            updateSurnameComboBox();
            
            JOptionPane.showMessageDialog(mainFrame, 
                "Данные успешно загружены из XML!", 
                "Успех", 
                JOptionPane.INFORMATION_MESSAGE);            
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Ошибка при загрузке XML: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDataFromXML(String filename) {
        try {
            clientsModel.setRowCount(0);
            originalClientsData.clear();

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(new File(filename));
            doc.getDocumentElement().normalize();

            NodeList nlClients = doc.getElementsByTagName("client");

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
                }
            }
            
            updateSurnameComboBox();
            
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки XML", e);
        }
    }
    
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            return node.getTextContent() != null ? node.getTextContent().trim() : "";
        }
        return "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Proga().show();
            }
        });
    }
}
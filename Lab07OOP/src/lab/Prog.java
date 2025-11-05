package lab;

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

public class Prog {
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

    private class AddClientException extends Exception {
        private static final long serialVersionUID = 1L;
        public AddClientException(String message) {
            super(message);
        }
    }

    private class SearchClientException extends Exception {
        private static final long serialVersionUID = 1L;
        public SearchClientException(String message) {
            super(message);
        }
    }
    
    /**
     * Создает кнопку с иконкой
     */
    private JButton createButtonWithIcon(String iconPath, String tooltip) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        
        try {
            // Пытаемся загрузить иконку
            ImageIcon icon = new ImageIcon(iconPath);
            if (icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                // Масштабируем иконку до нужного размера
                Image scaledImage = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            } else {
                // Если иконка не загружена, используем текст
                button.setText(tooltip);
                System.out.println("Не удалось загрузить иконку: " + iconPath);
            }
        } catch (Exception e) {
            // В случае ошибки используем текст вместо иконки
            button.setText(tooltip);
            System.out.println("Ошибка загрузки иконки " + iconPath + ": " + e.getMessage());
        }
        
        // Настройка внешнего вида кнопки
        button.setPreferredSize(new Dimension(40, 40));
        button.setMargin(new Insets(5, 5, 5, 5));
        
        return button;
    }
    
    /**
     * @param show функция создания окна
     */
    public void show() {
        // Создание окна
        mainFrame = new JFrame("Почта России - Управление клиентами");
        mainFrame.setSize(1000, 600);
        mainFrame.setLocation(300, 150);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создание кнопок с иконками
        save = createButtonWithIcon("./images/SAVE.png", "Сохранить данные клиентов");
        addClient = createButtonWithIcon("./images/ADD.png", "Добавить нового клиента");
        editClient = createButtonWithIcon("./images/EDIT.png", "Изменить данные клиента");
        deleteClient = createButtonWithIcon("./images/Recycle.jpg", "Удалить клиента");
        saveButton = createButtonWithIcon("./images/SAVE.png", "Сохранить в XML");
        loadButton = createButtonWithIcon("./images/LOAD.png", "Загрузить из XML");
        generateReportButton = createButtonWithIcon("./images/REPORT.png", "Сгенерировать отчет в PDF/HTML");

        // Кнопки без иконок (если для них нет иконок)
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

        // Размещение панели инструментов
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(toolBar, BorderLayout.NORTH);

        // Панель клиентов
        JPanel clientsPanel = new JPanel(new BorderLayout());
        
        // Создание таблицы с данными клиентов
        String[] clientColumns = {"Фамилия", "Телефон", "Адрес", "Газета"};
        String[][] clientData = {
            {"Иванов", "451-50-70", "Коммунарская 22", "Растишка"},
            {"Петров", "225-25-52", "Содовая 13", "Моделист"},
            {"Сидоров", "789-63-45", "Лесная 44", "Баскетболист"},
            {"Козлов", "111-22-33", "Попова 38", "Электрик"},
            {"Фролов", "555-66-77", "Садовая 5", "Футболист"}
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
        // Создаем комбобокс с фамилиями для фильтрации
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
        
        // Добавляем кнопку поиска
        clientFormPanel.add(searchButton);
        
        // Добавляем кнопку сброса фильтра
        clientFormPanel.add(resetButton);
      
        clientsPanel.add(clientFormPanel, BorderLayout.SOUTH);

        // Размещение основной панели
        mainFrame.add(clientsPanel, BorderLayout.CENTER);

        // Добавляем обработчики событий
        addEventHandlers();

        // Визуализация экранной формы
        mainFrame.setVisible(true);
    }

    // Обработчик исключений для кнопки "добавить клиента"
    private void ValidateClients(String name, String phone, String address, String newspaper) throws AddClientException {
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || newspaper.isEmpty()) {
            throw new AddClientException("Все поля должны быть заполнены!!!");
        }

        if (name.length() < 2) throw new AddClientException("Фамилия не может состоять из 1 символа!");

        // Проверка формата телефона (XXX-XX-XX)
        if (!phone.matches("\\d{3}-\\d{2}-\\d{2}")) {
            throw new AddClientException("Телефон должен быть в формате XXX-XX-XX!");
        }

        if (address.length() < 5) throw new AddClientException("Адрес слишком короткий!");
    }
    
    // Обработчик исключений для кнопки "поиск"
    private void ValidateSearch(String name, String phone, String address, String newspaper) throws SearchClientException {
        if (name.isEmpty() && phone.isEmpty() && address.isEmpty() && newspaper.isEmpty()) {
            throw new SearchClientException("Хотя бы одно поле должно быть заполнено!!!");
        }
    }

    /**
     * Метод для добавления обработчиков событий
     */
    private void addEventHandlers() {
    	
        // Обработчик для кнопки поиска
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        // Обработчик для кнопки сброса
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetFilters();
            }
        });
        
        // Обработчик для выпадающего списка фамилий
        surnameComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterBySurname();
            }
        });
        
        // Кнопка добавления клиента
        addClient.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		addClients();
        	}
        });

        // Кнопка редактирования клиента
        editClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelectedClient();
            }
        });

        // Кнопка удаления клиента
        deleteClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedClient();
            }
        });

        // Кнопка сохранения данных в файл
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDataToXML();
            }
        });

        // Кнопка загрузки данных из файла
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadDataFromXML();
            }
        });
        
        // Кнопка генерации отчета
        generateReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generatePDFandHTMLReports();
            }
        });

        // Кнопка сохранения (базовая)
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
     * Метод для редактирования выбранного клиента
     */
    private void editSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Пожалуйста, выберите клиента для редактирования", 
                "Ошибка", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Получаем текущие данные клиента
        String currentName = (String) clientsModel.getValueAt(selectedRow, 0);
        String currentPhone = (String) clientsModel.getValueAt(selectedRow, 1);
        String currentAddress = (String) clientsModel.getValueAt(selectedRow, 2);
        String currentNewspaper = (String) clientsModel.getValueAt(selectedRow, 3);

        // Создаем диалоговое окно для редактирования
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

                    // Обновляем данные в таблице
                    clientsModel.setValueAt(name, selectedRow, 0);
                    clientsModel.setValueAt(phone, selectedRow, 1);
                    clientsModel.setValueAt(address, selectedRow, 2);
                    clientsModel.setValueAt(newspaper, selectedRow, 3);
                    
                    // Обновляем исходные данные
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

    /**
     * Метод для удаления выбранного клиента
     */
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
            // Удаляем из таблицы
            clientsModel.removeRow(selectedRow);
            
            // Удаляем из исходных данных
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

    /**
     * @generateReport Метод для генерации отчета
     */
    private void generatePDFandHTMLReports() {
        try {
            JDialog chooseDialog = new JDialog(mainFrame, "Выберите вариант отчёта", true);
            chooseDialog.setSize(400, 300);
            chooseDialog.setLocationRelativeTo(mainFrame);
            chooseDialog.setLayout(new BorderLayout());

            JButton saveToPDF = new JButton("PDF");
            JButton saveToHtml = new JButton("HTML");

            saveToPDF.setPreferredSize(new Dimension(150, 90));
            saveToHtml.setPreferredSize(new Dimension(150, 90));

            java.awt.Font buttonFont = new java.awt.Font("Arial", java.awt.Font.BOLD, 25);
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
            // Сначала сохраняем данные во временный XML
            File tempFile = File.createTempFile("clients_temp", ".xml");
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
                openFile3.setFile("clients_report.pdf");
            } else {
                openFile3.setFile("clients_report.html");
            }
            
            openFile3.setVisible(true);

            directory = openFile3.getDirectory();
            file_name = openFile3.getFile();

            if (directory == null || file_name == null) return;

            String resultpath = directory + file_name;

            generateReport(tempFile.getAbsolutePath(), "/postal_clients/client", template, resultpath);
            
            // Удаляем временный файл
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
    
    /**
     * @performSearch Метод для выполнения поиска по фамилии, телефону, адресу и газете
     */
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
    
    /**
     * @filterBySurname Метод для фильтрации по фамилии
     */
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
    
    /**
     * @resetFilters Метод для сброса всех фильтров
     */
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
    
    /**
     * Метод для обновления комбобокса с фамилиями
     */
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
    
    /**
     * @addClients Метод добавления клиентов
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

    /**
     * @saveDataToXML функция сохранения данных в файл
     */
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

    /**
     * @loadDataFromXML функция загрузки данных из файла 
     */
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
    
    /**
     * Вспомогательный метод для получения текста из XML элемента
     */
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
                new Prog().show();
            }
        });
    }
}
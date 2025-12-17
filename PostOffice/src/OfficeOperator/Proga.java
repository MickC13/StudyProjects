package OfficeOperator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import Postman.Postman;
import Districts.District;
import PostalSystem.IntegratedDataManager;

public class Proga extends JPanel {
    private IntegratedDataManager dataManager;
    private DefaultTableModel clientsModel;
    private JTable clientsTable;
    
    // Компоненты управления
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton showPostmanChainButton;
    private JButton showNewspaperChainButton;
    
    // Поля поиска
    private JTextField surnameField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField newspaperField;
    private JTextField districtField;
    
    public Proga(IntegratedDataManager dataManager) {
        this.dataManager = dataManager;
        initializeUI();
        loadClientsData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Панель поиска
        JPanel searchPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Поиск клиентов"));
        
        searchPanel.add(new JLabel("Фамилия:"));
        surnameField = new JTextField();
        searchPanel.add(surnameField);
        
        searchPanel.add(new JLabel("Телефон:"));
        phoneField = new JTextField();
        searchPanel.add(phoneField);
        
        searchPanel.add(new JLabel("Адрес:"));
        addressField = new JTextField();
        searchPanel.add(addressField);
        
        searchPanel.add(new JLabel("Газета:"));
        newspaperField = new JTextField();
        searchPanel.add(newspaperField);
        
        searchPanel.add(new JLabel("Район:"));
        districtField = new JTextField();
        searchPanel.add(districtField);
        
        searchButton = new JButton("Поиск");
        JButton clearButton = new JButton("Сброс");
        
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Таблица клиентов
        String[] columns = {"ID", "Фамилия", "Телефон", "Адрес", "Газета", "Район", "Почтальон ID"};
        clientsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        clientsTable = new JTable(clientsModel);
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Настройка ширины столбцов
        clientsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        clientsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        clientsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        clientsTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        clientsTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        clientsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        clientsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель управления
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление клиентами"));
        
        addButton = new JButton("Добавить клиента");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        refreshButton = new JButton("Обновить");
        showPostmanChainButton = new JButton("Цепочка почтальона");
        showNewspaperChainButton = new JButton("Цепочка газеты");
        
        addButton.addActionListener(e -> showAddClientDialog());
        editButton.addActionListener(e -> showEditClientDialog());
        deleteButton.addActionListener(e -> deleteSelectedClient());
        refreshButton.addActionListener(e -> {
            loadClientsData();
            JOptionPane.showMessageDialog(Proga.this, 
                "Данные обновлены!", "Информация", JOptionPane.INFORMATION_MESSAGE);
        });
        showPostmanChainButton.addActionListener(e -> showPostmanChain());
        showNewspaperChainButton.addActionListener(e -> showNewspaperChain());
        
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        controlPanel.add(showPostmanChainButton);
        controlPanel.add(showNewspaperChainButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void loadClientsData() {
        loadClientsData(null, null, null, null, null);
    }
    
    private void loadClientsData(String surname, String phone, String address, 
                               String newspaper, String district) {
        clientsModel.setRowCount(0);
        List<Client> allClients = dataManager.getClients();
        
        for (Client client : allClients) {
            // Применяем фильтры поиска
            boolean matches = true;
            
            if (surname != null && !surname.isEmpty() && 
                !client.getSurname().toLowerCase().contains(surname.toLowerCase())) {
                matches = false;
            }
            
            if (phone != null && !phone.isEmpty() && 
                !client.getPhone().toLowerCase().contains(phone.toLowerCase())) {
                matches = false;
            }
            
            if (address != null && !address.isEmpty() && 
                !client.getAddress().toLowerCase().contains(address.toLowerCase())) {
                matches = false;
            }
            
            if (newspaper != null && !newspaper.isEmpty() && 
                !client.getNewspaper().toLowerCase().contains(newspaper.toLowerCase())) {
                matches = false;
            }
            
            if (district != null && !district.isEmpty() && 
                (client.getDistrict() == null || 
                 !client.getDistrict().toLowerCase().contains(district.toLowerCase()))) {
                matches = false;
            }
            
            if (matches) {
                clientsModel.addRow(new Object[]{
                    client.getId(),
                    client.getSurname(),
                    client.getPhone(),
                    client.getAddress(),
                    client.getNewspaper(),
                    client.getDistrict(),
                    client.getPostmanId()
                });
            }
        }
    }
    
    private void performSearch() {
        String surname = surnameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String newspaper = newspaperField.getText().trim();
        String district = districtField.getText().trim();
        
        loadClientsData(surname, phone, address, newspaper, district);
    }
    
    private void clearSearch() {
        surnameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        newspaperField.setText("");
        districtField.setText("");
        loadClientsData();
    }
    
    private void showAddClientDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Добавить нового клиента", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField surnameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField newspaperField = new JTextField();
        
        // Выпадающий список для районов (динамический)
        JComboBox<String> districtCombo = new JComboBox<>();
        districtCombo.addItem(""); // Пустая строка
        // Получаем районы из dataManager
        for (District district : dataManager.getDistricts()) {
            districtCombo.addItem(district.getName());
        }
        
        formPanel.add(new JLabel("Фамилия*:"));
        formPanel.add(surnameField);
        formPanel.add(new JLabel("Телефон*:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Адрес*:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Газета:"));
        formPanel.add(newspaperField);
        formPanel.add(new JLabel("Район:"));
        formPanel.add(districtCombo);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String surname = surnameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String address = addressField.getText().trim();
                    String newspaper = newspaperField.getText().trim();
                    String district = (String) districtCombo.getSelectedItem();
                    
                    if (surname.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Заполните обязательные поля (помечены *)!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    Client client = new Client();
                    client.setSurname(surname);
                    client.setPhone(phone);
                    client.setAddress(address);
                    client.setNewspaper(newspaper);
                    client.setDistrict(district != null && !district.isEmpty() ? district : null);
                    
                    dataManager.addClient(client);
                    
                    // Если указан район, автоматически назначаем почтальона
                    if (client.getDistrict() != null && !client.getDistrict().isEmpty()) {
                        // Находим почтальонов в этом районе
                        List<Postman> postmenInDistrict = dataManager.getPostmenByDistrict(client.getDistrict());
                        if (!postmenInDistrict.isEmpty()) {
                            // Выбираем почтальона с наименьшим количеством клиентов
                            Postman selectedPostman = postmenInDistrict.get(0);
                            for (Postman postman : postmenInDistrict) {
                                if (dataManager.getClientCount(postman.getId()) < 
                                    dataManager.getClientCount(selectedPostman.getId())) {
                                    selectedPostman = postman;
                                }
                            }
                            client.setPostmanId(selectedPostman.getId());
                            dataManager.updateClient(client);
                        }
                    }
                    
                    loadClientsData();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(Proga.this, 
                        "Клиент успешно добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditClientDialog() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите клиента для редактирования!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int clientId = (Integer) clientsModel.getValueAt(selectedRow, 0);
        Client client = dataManager.getClientById(clientId);
        
        if (client == null) {
            JOptionPane.showMessageDialog(this, "Клиент не найден!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Редактировать клиента", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField surnameField = new JTextField(client.getSurname());
        JTextField phoneField = new JTextField(client.getPhone());
        JTextField addressField = new JTextField(client.getAddress());
        JTextField newspaperField = new JTextField(client.getNewspaper());
        
        // Выпадающий список для районов (динамический)
        JComboBox<String> districtCombo = new JComboBox<>();
        districtCombo.addItem(""); // Пустая строка
        // Получаем районы из dataManager
        for (District district : dataManager.getDistricts()) {
            districtCombo.addItem(district.getName());
        }
        if (client.getDistrict() != null) {
            districtCombo.setSelectedItem(client.getDistrict());
        }
        
        formPanel.add(new JLabel("Фамилия*:"));
        formPanel.add(surnameField);
        formPanel.add(new JLabel("Телефон*:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Адрес*:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Газета:"));
        formPanel.add(newspaperField);
        formPanel.add(new JLabel("Район:"));
        formPanel.add(districtCombo);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String surname = surnameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String address = addressField.getText().trim();
                    String newspaper = newspaperField.getText().trim();
                    String district = (String) districtCombo.getSelectedItem();
                    
                    if (surname.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Заполните обязательные поля (помечены *)!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    client.setSurname(surname);
                    client.setPhone(phone);
                    client.setAddress(address);
                    client.setNewspaper(newspaper);
                    String oldDistrict = client.getDistrict();
                    client.setDistrict(district != null && !district.isEmpty() ? district : null);
                    
                    // Если изменился район, обновляем связь с почтальоном
                    if (client.getDistrict() != null && !client.getDistrict().isEmpty() &&
                        (oldDistrict == null || !oldDistrict.equals(client.getDistrict()))) {
                        // Находим почтальонов в новом районе
                        List<Postman> postmenInDistrict = dataManager.getPostmenByDistrict(client.getDistrict());
                        if (!postmenInDistrict.isEmpty()) {
                            // Выбираем почтальона с наименьшим количеством клиентов
                            Postman selectedPostman = postmenInDistrict.get(0);
                            for (Postman postman : postmenInDistrict) {
                                if (dataManager.getClientCount(postman.getId()) < 
                                    dataManager.getClientCount(selectedPostman.getId())) {
                                    selectedPostman = postman;
                                }
                            }
                            client.setPostmanId(selectedPostman.getId());
                        } else {
                            client.setPostmanId(null);
                        }
                    } else if (client.getDistrict() == null || client.getDistrict().isEmpty()) {
                        client.setPostmanId(null);
                    }
                    
                    dataManager.updateClient(client);
                    loadClientsData();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(Proga.this, 
                        "Данные клиента обновлены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите клиента для удаления!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int clientId = (Integer) clientsModel.getValueAt(selectedRow, 0);
        String clientName = (String) clientsModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Вы уверены, что хотите удалить клиента " + clientName + "?", 
            "Подтверждение удаления", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataManager.deleteClient(clientId);
                loadClientsData();
                
                JOptionPane.showMessageDialog(this, 
                    "Клиент удален!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Ошибка при удалении: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showPostmanChain() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите клиента для просмотра цепочки его почтальона!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer postmanId = (Integer) clientsModel.getValueAt(selectedRow, 6);
        if (postmanId == null) {
            JOptionPane.showMessageDialog(this, 
                "У этого клиента не назначен почтальон!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Получаем цепочку почтальона
        java.util.Map<String, Object> chain = dataManager.getPostmanChain(postmanId);
        
        StringBuilder report = new StringBuilder();
        report.append("ЦЕПОЧКА ПОЧТАЛЬОНА\n");
        report.append("=================\n\n");
        
        if (chain.containsKey("postman")) {
            Postman postman = (Postman) chain.get("postman");
            report.append("Почтальон: ").append(postman.getFullName()).append("\n");
            report.append("Телефон: ").append(postman.getPhone()).append("\n");
            report.append("Район: ").append(postman.getDistrict()).append("\n");
            report.append("Стаж: ").append(postman.getExperience()).append(" лет\n");
            report.append("График: ").append(postman.getWorkSchedule()).append("\n\n");
        }
        
        if (chain.containsKey("clients")) {
            List<Client> clients = (List<Client>) chain.get("clients");
            report.append("Обслуживает клиентов: ").append(clients.size()).append("\n");
            for (Client client : clients) {
                report.append("  - ").append(client.getSurname())
                     .append(", ").append(client.getAddress())
                     .append(", Газета: ").append(client.getNewspaper()).append("\n");
            }
            report.append("\n");
        }
        
        if (chain.containsKey("newspaperDistribution")) {
            java.util.Map<String, Integer> distribution = 
                (java.util.Map<String, Integer>) chain.get("newspaperDistribution");
            report.append("Распределение газет:\n");
            for (java.util.Map.Entry<String, Integer> entry : distribution.entrySet()) {
                report.append("  - ").append(entry.getKey())
                     .append(": ").append(entry.getValue()).append(" клиентов\n");
            }
        }
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Цепочка почтальона", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showNewspaperChain() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите клиента для просмотра цепочки его газеты!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String newspaper = (String) clientsModel.getValueAt(selectedRow, 4);
        if (newspaper == null || newspaper.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "У этого клиента не указана газета!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Получаем цепочку газеты
        java.util.Map<String, Object> chain = dataManager.getNewspaperChain(newspaper);
        
        StringBuilder report = new StringBuilder();
        report.append("ЦЕПОЧКА ГАЗЕТЫ: ").append(newspaper).append("\n");
        report.append("======================\n\n");
        
        if (chain.containsKey("subscribers")) {
            List<Client> subscribers = (List<Client>) chain.get("subscribers");
            report.append("Подписчиков: ").append(subscribers.size()).append("\n\n");
            
            // Группируем по районам
            java.util.Map<String, java.util.List<Client>> byDistrict = new java.util.HashMap<>();
            for (Client client : subscribers) {
                String district = client.getDistrict() != null ? client.getDistrict() : "Не указан";
                byDistrict.computeIfAbsent(district, k -> new java.util.ArrayList<>()).add(client);
            }
            
            for (java.util.Map.Entry<String, java.util.List<Client>> entry : byDistrict.entrySet()) {
                report.append("Район: ").append(entry.getKey()).append("\n");
                report.append("  Клиенты:\n");
                for (Client client : entry.getValue()) {
                    report.append("    - ").append(client.getSurname())
                         .append(", ").append(client.getAddress());
                    if (client.getPostmanId() != null) {
                        Postman postman = dataManager.getPostmanById(client.getPostmanId());
                        if (postman != null) {
                            report.append(", Почтальон: ").append(postman.getFullName());
                        }
                    }
                    report.append("\n");
                }
                report.append("\n");
            }
        }
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Цепочка газеты", JOptionPane.INFORMATION_MESSAGE);
    }
}
package Postman;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import OfficeOperator.Client;
import Districts.District;
import PostalSystem.IntegratedDataManager;

public class Program extends JPanel {
    private IntegratedDataManager dataManager;
    private JTable postmanTable;
    private DefaultTableModel tableModel;
    private JLabel infoLabel;
    
    // Компоненты управления
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton showChainButton;
    private JButton showClientsButton;
    
    // Фильтры
    private JComboBox<String> districtFilter;
    private JButton filterButton;
    private JButton clearFilterButton;
    
    // Список районов для выпадающих списков
    private DefaultComboBoxModel<String> districtComboBoxModel;
    
    public Program(IntegratedDataManager dataManager) {
        this.dataManager = dataManager;
        initializeUI();
        loadPostmenData();
        updateDistrictComboBox();
        updateDistrictFilter();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Инициализация модели для выпадающего списка районов
        districtComboBoxModel = new DefaultComboBoxModel<>();
        
        // Верхняя панель с фильтрами
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Фильтры по району"));
        
        filterPanel.add(new JLabel("Район:"));
        
        districtFilter = new JComboBox<>(districtComboBoxModel);
        districtFilter.setPreferredSize(new Dimension(150, 25));
        filterPanel.add(districtFilter);
        
        filterButton = new JButton("Применить фильтр");
        filterButton.addActionListener(e -> applyFilter());
        filterPanel.add(filterButton);
        
        clearFilterButton = new JButton("Сбросить фильтр");
        clearFilterButton.addActionListener(e -> clearFilter());
        filterPanel.add(clearFilterButton);
        
        add(filterPanel, BorderLayout.NORTH);
        
        // Таблица почтальонов
        String[] columns = {"ID", "ФИО", "Телефон", "Район", "Стаж (лет)", "График работы", "Клиентов"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        postmanTable = new JTable(tableModel);
        postmanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        postmanTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateInfoLabel();
            }
        });
        
        // Настройка ширины столбцов
        postmanTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        postmanTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        postmanTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        postmanTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        postmanTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        postmanTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        postmanTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(postmanTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель информации
        infoLabel = new JLabel("Выберите почтальона для просмотра информации");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(infoLabel, BorderLayout.SOUTH);
        
        // Панель управления
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление почтальонами"));
        
        addButton = new JButton("Добавить почтальона");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        refreshButton = new JButton("Обновить");
        showChainButton = new JButton("Показать цепочку");
        showClientsButton = new JButton("Клиенты почтальона");
        
        // Настройка размеров кнопок
        Dimension buttonSize = new Dimension(150, 30);
        addButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        refreshButton.setPreferredSize(buttonSize);
        showChainButton.setPreferredSize(buttonSize);
        showClientsButton.setPreferredSize(buttonSize);
        
        addButton.addActionListener(e -> showAddPostmanDialog());
        editButton.addActionListener(e -> showEditPostmanDialog());
        deleteButton.addActionListener(e -> deleteSelectedPostman());
        refreshButton.addActionListener(e -> {
            loadPostmenData();
            updateDistrictComboBox();
            updateDistrictFilter();
            JOptionPane.showMessageDialog(Program.this, 
                "Данные обновлены!", "Информация", JOptionPane.INFORMATION_MESSAGE);
        });
        showChainButton.addActionListener(e -> showPostmanChain());
        showClientsButton.addActionListener(e -> showPostmanClients());
        
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        controlPanel.add(showChainButton);
        controlPanel.add(showClientsButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void updateDistrictComboBox() {
        // Очищаем модель
        districtComboBoxModel.removeAllElements();
        districtComboBoxModel.addElement(""); // Пустая строка для "Не назначен"
        
        // Получаем все районы из dataManager
        List<District> districts = dataManager.getDistricts();
        for (District district : districts) {
            districtComboBoxModel.addElement(district.getName());
        }
        
        // Добавляем опцию "Не назначен"
        districtComboBoxModel.addElement("Не назначен");
    }
    
    private void updateDistrictFilter() {
        String selected = (String) districtFilter.getSelectedItem();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) districtFilter.getModel();
        model.removeAllElements();
        model.addElement("Все районы");
        
        // Добавляем все существующие районы
        List<District> districts = dataManager.getDistricts();
        for (District district : districts) {
            model.addElement(district.getName());
        }
        
        // Добавляем "Не назначен" для почтальонов без района
        model.addElement("Не назначен");
        
        // Восстанавливаем выбранный элемент
        if (selected != null && model.getIndexOf(selected) >= 0) {
            districtFilter.setSelectedItem(selected);
        }
    }
    
    private void loadPostmenData() {
        tableModel.setRowCount(0);
        List<Postman> postmen = dataManager.getPostmen();
        
        for (Postman postman : postmen) {
            int clientCount = dataManager.getClientCount(postman.getId());
            
            tableModel.addRow(new Object[]{
                postman.getId(),
                postman.getFullName(),
                postman.getPhone(),
                postman.getDistrict() != null ? postman.getDistrict() : "Не назначен",
                postman.getExperience(),
                postman.getWorkSchedule(),
                clientCount
            });
        }
    }
    
    private void showAddPostmanDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Добавить нового почтальона", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField surnameField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField patronymicField = new JTextField();
        JTextField phoneField = new JTextField();
        
        // Выпадающий список для районов (динамический)
        JComboBox<String> districtCombo = new JComboBox<>(districtComboBoxModel);
        
        JTextField experienceField = new JTextField();
        
        // Выпадающий список для графика работы
        JComboBox<String> scheduleCombo = new JComboBox<>();
        scheduleCombo.addItem("5/2 (пн-пт 8:00-17:00)");
        scheduleCombo.addItem("Сменный график");
        scheduleCombo.addItem("6/1 (вс - выходной)");
        scheduleCombo.addItem("Гибкий график");
        
        formPanel.add(new JLabel("Фамилия*:"));
        formPanel.add(surnameField);
        formPanel.add(new JLabel("Имя*:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Отчество:"));
        formPanel.add(patronymicField);
        formPanel.add(new JLabel("Телефон*:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Район:"));
        formPanel.add(districtCombo);
        formPanel.add(new JLabel("Стаж (лет):"));
        formPanel.add(experienceField);
        formPanel.add(new JLabel("График работы:"));
        formPanel.add(scheduleCombo);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Валидация данных
                    if (surnameField.getText().trim().isEmpty() || 
                        firstNameField.getText().trim().isEmpty() ||
                        phoneField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Заполните обязательные поля (помечены *)!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    Postman postman = new Postman();
                    postman.setSurname(surnameField.getText().trim());
                    postman.setFirstName(firstNameField.getText().trim());
                    postman.setPatronymic(patronymicField.getText().trim());
                    postman.setPhone(phoneField.getText().trim());
                    
                    String district = (String) districtCombo.getSelectedItem();
                    if (district != null && !district.isEmpty() && !district.equals("Не назначен")) {
                        postman.setDistrict(district);
                    } else {
                        postman.setDistrict("");
                    }
                    
                    try {
                        postman.setExperience(Integer.parseInt(experienceField.getText().trim()));
                    } catch (NumberFormatException ex) {
                        postman.setExperience(0);
                    }
                    
                    postman.setWorkSchedule((String) scheduleCombo.getSelectedItem());
                    
                    dataManager.addPostman(postman);
                    
                    // Обновляем UI
                    loadPostmenData();
                    updateDistrictFilter();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(Program.this, 
                        "Почтальон успешно добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    
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
    
    private void showEditPostmanDialog() {
        int selectedRow = postmanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите почтальона для редактирования!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int postmanId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Postman postman = dataManager.getPostmanById(postmanId);
        
        if (postman == null) {
            JOptionPane.showMessageDialog(this, "Почтальон не найден!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Редактировать почтальона", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField surnameField = new JTextField(postman.getSurname());
        JTextField firstNameField = new JTextField(postman.getFirstName());
        JTextField patronymicField = new JTextField(postman.getPatronymic());
        JTextField phoneField = new JTextField(postman.getPhone());
        
        // Выпадающий список для районов (динамический)
        JComboBox<String> districtCombo = new JComboBox<>(districtComboBoxModel);
        if (postman.getDistrict() != null && !postman.getDistrict().isEmpty()) {
            districtCombo.setSelectedItem(postman.getDistrict());
        } else {
            districtCombo.setSelectedItem("Не назначен");
        }
        
        JTextField experienceField = new JTextField(String.valueOf(postman.getExperience()));
        
        // Выпадающий список для графика работы
        JComboBox<String> scheduleCombo = new JComboBox<>();
        scheduleCombo.addItem("5/2 (пн-пт 8:00-17:00)");
        scheduleCombo.addItem("Сменный график");
        scheduleCombo.addItem("6/1 (вс - выходной)");
        scheduleCombo.addItem("Гибкий график");
        
        // Устанавливаем текущий график
        if (postman.getWorkSchedule() != null) {
            scheduleCombo.setSelectedItem(postman.getWorkSchedule());
        }
        
        formPanel.add(new JLabel("Фамилия*:"));
        formPanel.add(surnameField);
        formPanel.add(new JLabel("Имя*:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Отчество:"));
        formPanel.add(patronymicField);
        formPanel.add(new JLabel("Телефон*:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Район:"));
        formPanel.add(districtCombo);
        formPanel.add(new JLabel("Стаж (лет):"));
        formPanel.add(experienceField);
        formPanel.add(new JLabel("График работы:"));
        formPanel.add(scheduleCombo);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Валидация данных
                    if (surnameField.getText().trim().isEmpty() || 
                        firstNameField.getText().trim().isEmpty() ||
                        phoneField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Заполните обязательные поля (помечены *)!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    postman.setSurname(surnameField.getText().trim());
                    postman.setFirstName(firstNameField.getText().trim());
                    postman.setPatronymic(patronymicField.getText().trim());
                    postman.setPhone(phoneField.getText().trim());
                    
                    String selectedDistrict = (String) districtCombo.getSelectedItem();
                    if (selectedDistrict != null && !selectedDistrict.isEmpty() && !selectedDistrict.equals("Не назначен")) {
                        postman.setDistrict(selectedDistrict);
                    } else {
                        postman.setDistrict("");
                    }
                    
                    try {
                        postman.setExperience(Integer.parseInt(experienceField.getText().trim()));
                    } catch (NumberFormatException ex) {
                        postman.setExperience(0);
                    }
                    
                    postman.setWorkSchedule((String) scheduleCombo.getSelectedItem());
                    
                    dataManager.updatePostman(postman);
                    
                    // Обновляем UI
                    loadPostmenData();
                    updateDistrictFilter();
                    updateInfoLabel();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(Program.this, 
                        "Данные почтальона обновлены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    
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
    
    private void deleteSelectedPostman() {
        int selectedRow = postmanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите почтальона для удаления!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int postmanId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String postmanName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Вы уверены, что хотите удалить почтальона " + postmanName + "?", 
            "Подтверждение удаления", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataManager.deletePostman(postmanId);
                loadPostmenData();
                updateDistrictFilter();
                updateInfoLabel();
                
                JOptionPane.showMessageDialog(this, 
                    "Почтальон удален!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Ошибка при удалении: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showPostmanChain() {
        int selectedRow = postmanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите почтальона для просмотра цепочки!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int postmanId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Получаем цепочку почтальона
        java.util.Map<String, Object> chain = dataManager.getPostmanChain(postmanId);
        
        StringBuilder report = new StringBuilder();
        report.append("ПОЛНАЯ ЦЕПОЧКА ПОЧТАЛЬОНА\n");
        report.append("========================\n\n");
        
        if (chain.containsKey("postman")) {
            Postman postman = (Postman) chain.get("postman");
            report.append("Почтальон: ").append(postman.getFullName()).append("\n");
            report.append("Район: ").append(postman.getDistrict()).append("\n");
            report.append("Телефон: ").append(postman.getPhone()).append("\n");
            report.append("Стаж: ").append(postman.getExperience()).append(" лет\n");
            report.append("График: ").append(postman.getWorkSchedule()).append("\n\n");
        }
        
        if (chain.containsKey("clients")) {
            List<Client> clients = (List<Client>) chain.get("clients");
            report.append("Клиенты (").append(clients.size()).append("):\n");
            for (Client client : clients) {
                report.append("  - ").append(client.getSurname())
                     .append(", Адрес: ").append(client.getAddress())
                     .append(", Телефон: ").append(client.getPhone())
                     .append(", Газета: ").append(client.getNewspaper()).append("\n");
            }
            report.append("\n");
        }
        
        if (chain.containsKey("newspaperDistribution")) {
            java.util.Map<String, Integer> distribution = 
                (java.util.Map<String, Integer>) chain.get("newspaperDistribution");
            report.append("Распределение газет среди клиентов:\n");
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
    
    private void showPostmanClients() {
        int selectedRow = postmanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите почтальона для просмотра его клиентов!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int postmanId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Получаем клиентов почтальона
        List<Client> clients = dataManager.getClientsByPostman(postmanId);
        
        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "У этого почтальона нет клиентов!", 
                "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("КЛИЕНТЫ ПОЧТАЛЬОНА\n");
        report.append("==================\n\n");
        
        // Группируем клиентов по газетам
        java.util.Map<String, java.util.List<Client>> byNewspaper = new java.util.HashMap<>();
        for (Client client : clients) {
            String newspaper = client.getNewspaper() != null ? client.getNewspaper() : "Не указана";
            byNewspaper.computeIfAbsent(newspaper, k -> new java.util.ArrayList<>()).add(client);
        }
        
        for (java.util.Map.Entry<String, java.util.List<Client>> entry : byNewspaper.entrySet()) {
            report.append("Газета: ").append(entry.getKey()).append("\n");
            for (Client client : entry.getValue()) {
                report.append("  - ").append(client.getSurname())
                     .append(", ").append(client.getAddress())
                     .append(", тел.: ").append(client.getPhone()).append("\n");
            }
            report.append("\n");
        }
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Клиенты почтальона", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void applyFilter() {
        String selectedDistrict = (String) districtFilter.getSelectedItem();
        if (selectedDistrict != null && !selectedDistrict.equals("Все районы")) {
            tableModel.setRowCount(0);
            List<Postman> allPostmen = dataManager.getPostmen();
            
            for (Postman postman : allPostmen) {
                String postmanDistrict = postman.getDistrict();
                if (selectedDistrict.equals("Не назначен")) {
                    if (postmanDistrict == null || postmanDistrict.isEmpty()) {
                        addPostmanToTable(postman);
                    }
                } else if (selectedDistrict.equals(postmanDistrict)) {
                    addPostmanToTable(postman);
                }
            }
        } else {
            loadPostmenData();
        }
    }
    
    private void addPostmanToTable(Postman postman) {
        int clientCount = dataManager.getClientCount(postman.getId());
        tableModel.addRow(new Object[]{
            postman.getId(),
            postman.getFullName(),
            postman.getPhone(),
            postman.getDistrict() != null ? postman.getDistrict() : "Не назначен",
            postman.getExperience(),
            postman.getWorkSchedule(),
            clientCount
        });
    }
    
    private void clearFilter() {
        districtFilter.setSelectedItem("Все районы");
        loadPostmenData();
    }
    
    private void updateInfoLabel() {
        int selectedRow = postmanTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String district = (String) tableModel.getValueAt(selectedRow, 3);
            String schedule = (String) tableModel.getValueAt(selectedRow, 5);
            int clientCount = (Integer) tableModel.getValueAt(selectedRow, 6);
            
            infoLabel.setText("Почтальон: " + name + " | Район: " + district + 
                             " | Клиентов: " + clientCount + " | График: " + schedule);
        } else {
            infoLabel.setText("Выберите почтальона для просмотра информации");
        }
    }
}
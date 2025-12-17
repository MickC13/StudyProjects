package Districts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

public class DistrictsPanel extends JPanel {
    private DistrictDataManager dataManager;
    private JTable districtsTable;
    private DefaultTableModel tableModel;
    
    // Компоненты управления
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton statisticsButton;
    private JButton postmanReportButton;
    
    public DistrictsPanel(DistrictDataManager dataManager) {
        this.dataManager = dataManager;
        initializeUI();
        loadDistrictsData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Таблица районов
        String[] columns = {"ID", "Название", "Почтовый индекс", "Население", "Площадь (км²)", "Почтальоны", "Плотность"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        districtsTable = new JTable(tableModel);
        districtsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Настройка ширины столбцов
        districtsTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        districtsTable.getColumnModel().getColumn(1).setPreferredWidth(150);  // Название
        districtsTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Почтовый индекс
        districtsTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Население
        districtsTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Площадь
        districtsTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Почтальоны
        districtsTable.getColumnModel().getColumn(6).setPreferredWidth(80);   // Плотность
        
        JScrollPane scrollPane = new JScrollPane(districtsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель управления
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление районами"));
        
        addButton = new JButton("Добавить район");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        refreshButton = new JButton("Обновить");
        statisticsButton = new JButton("Статистика");
        postmanReportButton = new JButton("Отчет по почтальонам");
        
        // Настройка действий кнопок
        addButton.addActionListener(e -> showAddDistrictDialog());
        editButton.addActionListener(e -> showEditDistrictDialog());
        deleteButton.addActionListener(e -> deleteSelectedDistrict());
        refreshButton.addActionListener(e -> loadDistrictsData());
        statisticsButton.addActionListener(e -> showDistrictStatistics());
        postmanReportButton.addActionListener(e -> showPostmanReport());
        
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        controlPanel.add(statisticsButton);
        controlPanel.add(postmanReportButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void loadDistrictsData() {
        tableModel.setRowCount(0);
        List<District> districts = dataManager.getDistricts();
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for (District district : districts) {
            double density = district.getPopulationDensity();
            tableModel.addRow(new Object[]{
                district.getId(),
                district.getName(),
                district.getPostalCode(),
                district.getPopulation(),
                district.getArea(),
                district.getPostmanCount(),
                df.format(density)
            });
        }
    }
    
    private void showAddDistrictDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Добавить новый район", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField();
        JTextField postalCodeField = new JTextField();
        JTextField populationField = new JTextField();
        JTextField areaField = new JTextField();
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
        formPanel.add(new JLabel("Название района*:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Почтовый индекс*:"));
        formPanel.add(postalCodeField);
        formPanel.add(new JLabel("Население:"));
        formPanel.add(populationField);
        formPanel.add(new JLabel("Площадь (км²):"));
        formPanel.add(areaField);
        formPanel.add(new JLabel("Описание:"));
        formPanel.add(descriptionScroll);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Валидация данных
                    if (nameField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Введите название района!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (postalCodeField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Введите почтовый индекс!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    District district = new District();
                    district.setName(nameField.getText().trim());
                    district.setPostalCode(postalCodeField.getText().trim());
                    district.setDescription(descriptionArea.getText().trim());
                    
                    try {
                        district.setPopulation(Integer.parseInt(populationField.getText().trim()));
                    } catch (NumberFormatException ex) {
                        district.setPopulation(0);
                    }
                    
                    try {
                        district.setArea(Integer.parseInt(areaField.getText().trim()));
                    } catch (NumberFormatException ex) {
                        district.setArea(0);
                    }
                    
                    dataManager.addDistrict(district);
                    loadDistrictsData();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(DistrictsPanel.this, 
                        "Район успешно добавлен!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    
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
    
    private void showEditDistrictDialog() {
        int selectedRow = districtsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите район для редактирования!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int districtId = (Integer) tableModel.getValueAt(selectedRow, 0);
        District district = dataManager.getDistrictById(districtId);
        
        if (district == null) {
            JOptionPane.showMessageDialog(this, "Район не найден!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Редактировать район", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField nameField = new JTextField(district.getName());
        JTextField postalCodeField = new JTextField(district.getPostalCode());
        JTextField populationField = new JTextField(String.valueOf(district.getPopulation()));
        JTextField areaField = new JTextField(String.valueOf(district.getArea()));
        JTextArea descriptionArea = new JTextArea(district.getDescription(), 3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
        formPanel.add(new JLabel("Название района*:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Почтовый индекс*:"));
        formPanel.add(postalCodeField);
        formPanel.add(new JLabel("Население:"));
        formPanel.add(populationField);
        formPanel.add(new JLabel("Площадь (км²):"));
        formPanel.add(areaField);
        formPanel.add(new JLabel("Описание:"));
        formPanel.add(descriptionScroll);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Валидация данных
                    if (nameField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Введите название района!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (postalCodeField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Введите почтовый индекс!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    district.setName(nameField.getText().trim());
                    district.setPostalCode(postalCodeField.getText().trim());
                    district.setDescription(descriptionArea.getText().trim());
                    
                    try {
                        district.setPopulation(Integer.parseInt(populationField.getText().trim()));
                    } catch (NumberFormatException ex) {
                        district.setPopulation(0);
                    }
                    
                    try {
                        district.setArea(Integer.parseInt(areaField.getText().trim()));
                    } catch (NumberFormatException ex) {
                        district.setArea(0);
                    }
                    
                    dataManager.updateDistrict(district);
                    loadDistrictsData();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(DistrictsPanel.this, 
                        "Данные района обновлены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    
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
    
    private void deleteSelectedDistrict() {
        int selectedRow = districtsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите район для удаления!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int districtId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String districtName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Вы уверены, что хотите удалить район \"" + districtName + "\"?", 
            "Подтверждение удаления", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataManager.deleteDistrict(districtId);
                loadDistrictsData();
                
                JOptionPane.showMessageDialog(this, 
                    "Район удален!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Ошибка при удалении: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showDistrictStatistics() {
        List<District> districts = dataManager.getDistricts();
        
        if (districts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет данных о районах!", "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int totalPopulation = 0;
        int totalArea = 0;
        int totalPostmen = 0;
        
        for (District district : districts) {
            totalPopulation += district.getPopulation();
            totalArea += district.getArea();
            totalPostmen += district.getPostmanCount();
        }
        
        double avgDensity = totalArea > 0 ? (double) totalPopulation / totalArea : 0;
        double avgPostmenPerDistrict = districts.size() > 0 ? (double) totalPostmen / districts.size() : 0;
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        StringBuilder stats = new StringBuilder();
        stats.append("СТАТИСТИКА ПО РАЙОНАМ\n");
        stats.append("====================\n\n");
        stats.append("Всего районов: ").append(districts.size()).append("\n");
        stats.append("Общее население: ").append(totalPopulation).append(" чел.\n");
        stats.append("Общая площадь: ").append(totalArea).append(" км²\n");
        stats.append("Всего почтальонов: ").append(totalPostmen).append("\n");
        stats.append("Средняя плотность населения: ").append(df.format(avgDensity)).append(" чел/км²\n");
        stats.append("Среднее количество почтальонов на район: ").append(df.format(avgPostmenPerDistrict)).append("\n");
        
        stats.append("\nРайоны с наибольшим населением:\n");
        districts.stream()
            .sorted((d1, d2) -> Integer.compare(d2.getPopulation(), d1.getPopulation()))
            .limit(3)
            .forEach(d -> stats.append("  - ").append(d.getName()).append(": ").append(d.getPopulation()).append(" чел.\n"));
        
        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Статистика районов", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showPostmanReport() {
        List<District> districts = dataManager.getDistricts();
        
        if (districts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Нет данных о районах!", "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("ОТЧЕТ ПО ПОЧТАЛЬОНАМ ПО РАЙОНАМ\n");
        report.append("===============================\n\n");
        
        int districtsWithPostmen = 0;
        
        for (District district : districts) {
            int postmanCount = district.getPostmanCount();
            if (postmanCount > 0) {
                report.append("Район: ").append(district.getName()).append("\n");
                report.append("  Почтовый индекс: ").append(district.getPostalCode()).append("\n");
                report.append("  Количество почтальонов: ").append(postmanCount).append("\n");
                report.append("  Население на почтальона: ");
                
                if (postmanCount > 0) {
                    int populationPerPostman = district.getPopulation() / postmanCount;
                    report.append(populationPerPostman).append(" чел.\n");
                } else {
                    report.append("Нет данных\n");
                }
                
                report.append("  Площадь на почтальона: ");
                if (postmanCount > 0) {
                    double areaPerPostman = (double) district.getArea() / postmanCount;
                    DecimalFormat df = new DecimalFormat("#.##");
                    report.append(df.format(areaPerPostman)).append(" км²\n");
                } else {
                    report.append("Нет данных\n");
                }
                
                report.append("\n");
                districtsWithPostmen++;
            }
        }
        
        if (districtsWithPostmen == 0) {
            report.append("Нет районов с почтальонами.\n");
        } else {
            report.append("Итого: ").append(districtsWithPostmen).append(" районов с почтальонами.\n");
        }
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Отчет по почтальонам", JOptionPane.INFORMATION_MESSAGE);
    }
}
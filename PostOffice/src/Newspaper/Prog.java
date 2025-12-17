package Newspaper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList; // Добавлен импорт ArrayList
import PostalSystem.IntegratedDataManager;
import OfficeOperator.Client; // Добавлен импорт Client

/**
 * Панель для управления газетами
 * ТОЛЬКО 3 поля: ID, название, день выпуска
 */
public class Prog extends JPanel {
    private IntegratedDataManager dataManager;
    private JTable newspaperTable;
    private DefaultTableModel tableModel;
    
    // Компоненты управления
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    public Prog(IntegratedDataManager dataManager) {
        this.dataManager = dataManager;
        initializeUI();
        loadNewspapersData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Таблица газет (ТОЛЬКО 3 столбца)
        String[] columns = {"ID", "Название газеты", "День выпуска"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        newspaperTable = new JTable(tableModel);
        newspaperTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Настройка ширины столбцов
        newspaperTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        newspaperTable.getColumnModel().getColumn(1).setPreferredWidth(300);  // Название
        newspaperTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // День выпуска
        
        JScrollPane scrollPane = new JScrollPane(newspaperTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Панель управления
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Управление газетами"));
        
        addButton = new JButton("Добавить газету");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        refreshButton = new JButton("Обновить");
        
        // Настройка действий кнопок
        addButton.addActionListener(e -> showAddNewspaperDialog());
        editButton.addActionListener(e -> showEditNewspaperDialog());
        deleteButton.addActionListener(e -> deleteSelectedNewspaper());
        refreshButton.addActionListener(e -> loadNewspapersData());
        
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(refreshButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private void loadNewspapersData() {
        tableModel.setRowCount(0);
        List<Newspaper> newspapers = dataManager.getNewspapers();
        
        for (Newspaper newspaper : newspapers) {
            tableModel.addRow(new Object[]{
                newspaper.getId(),
                newspaper.getTitle(),
                newspaper.getReleaseDay()
            });
        }
    }
    
    private void showAddNewspaperDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Добавить новую газету", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField titleField = new JTextField();
        
        // Выпадающий список для дня выпуска
        JComboBox<String> dayCombo = new JComboBox<>(new String[]{
            "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье",
            "Ежедневно", "Раз в неделю", "Раз в месяц"
        });
        
        formPanel.add(new JLabel("Название газеты*:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("День выпуска*:"));
        formPanel.add(dayCombo);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Валидация данных
                    if (titleField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Введите название газеты!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    Newspaper newspaper = new Newspaper();
                    newspaper.setTitle(titleField.getText().trim());
                    newspaper.setReleaseDay((String) dayCombo.getSelectedItem());
                    
                    dataManager.addNewspaper(newspaper);
                    loadNewspapersData();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(Prog.this, 
                        "Газета успешно добавлена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    
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
    
    private void showEditNewspaperDialog() {
        int selectedRow = newspaperTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите газету для редактирования!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int newspaperId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Newspaper newspaper = dataManager.getNewspaperById(newspaperId);
        
        if (newspaper == null) {
            JOptionPane.showMessageDialog(this, "Газета не найдена!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                    "Редактировать газету", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField titleField = new JTextField(newspaper.getTitle());
        
        // Выпадающий список для дня выпуска
        JComboBox<String> dayCombo = new JComboBox<>(new String[]{
            "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье",
            "Ежедневно", "Раз в неделю", "Раз в месяц"
        });
        dayCombo.setSelectedItem(newspaper.getReleaseDay());
        
        formPanel.add(new JLabel("Название газеты*:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("День выпуска*:"));
        formPanel.add(dayCombo);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Валидация данных
                    if (titleField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Введите название газеты!", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    newspaper.setTitle(titleField.getText().trim());
                    newspaper.setReleaseDay((String) dayCombo.getSelectedItem());
                    
                    dataManager.updateNewspaper(newspaper);
                    loadNewspapersData();
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(Prog.this, 
                        "Данные газеты обновлены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    
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
    
    private void deleteSelectedNewspaper() {
        int selectedRow = newspaperTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Выберите газету для удаления!", 
                "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int newspaperId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String newspaperTitle = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Вы уверены, что хотите удалить газету \"" + newspaperTitle + "\"?", 
            "Подтверждение удаления", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataManager.deleteNewspaper(newspaperId);
                loadNewspapersData();
                
                JOptionPane.showMessageDialog(this, 
                    "Газета удалена!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Ошибка при удалении: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Основной метод для тестирования панели отдельно
     */
    public static void main(String[] args) {
        // Создаем тестовый DataManager
        TestDataManager testDataManager = new TestDataManager();
        
        // Создаем и настраиваем окно
        JFrame frame = new JFrame("Управление газетами");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        
        // Добавляем нашу панель
        frame.add(new Prog(new IntegratedDataManager()));
        frame.setVisible(true);
    }
    
    /**
     * Тестовый DataManager для демонстрации
     */
    static class TestDataManager implements NewspaperDataManager {
        private List<Newspaper> newspapers = new ArrayList<>();
        
        public TestDataManager() {
            // Создаем тестовые данные газет (ТОЛЬКО 3 поля)
            newspapers.add(new Newspaper(1, "Почтовый вестник", "Ежедневно"));
            newspapers.add(new Newspaper(2, "Новости города", "Понедельник"));
            newspapers.add(new Newspaper(3, "Спортивные известия", "Вторник"));
            newspapers.add(new Newspaper(4, "Техника молодежи", "Раз в месяц"));
            newspapers.add(new Newspaper(5, "Вечерняя почта", "Ежедневно"));
        }
        
        @Override
        public List<Newspaper> getNewspapers() {
            return new ArrayList<>(newspapers);
        }
        
        @Override
        public void addNewspaper(Newspaper newspaper) {
            int maxId = newspapers.stream().mapToInt(Newspaper::getId).max().orElse(0);
            newspaper.setId(maxId + 1);
            newspapers.add(newspaper);
        }
        
        @Override
        public void updateNewspaper(Newspaper newspaper) {
            for (int i = 0; i < newspapers.size(); i++) {
                if (newspapers.get(i).getId() == newspaper.getId()) {
                    newspapers.set(i, newspaper);
                    break;
                }
            }
        }
        
        @Override
        public void deleteNewspaper(int id) {
            newspapers.removeIf(n -> n.getId() == id);
        }
        
        @Override
        public Newspaper getNewspaperById(int id) {
            return newspapers.stream()
                    .filter(n -> n.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
        
        // Реализация методов для работы со связями (возвращаем пустые значения для теста)
        @Override
        public List<Client> getSubscribers(String newspaperTitle) {
            return new ArrayList<>();
        }
        
        @Override
        public int getSubscriberCount(String newspaperTitle) {
            return 0;
        }
        
        @Override
        public List<String> getPopularNewspapers(int limit) {
            return new ArrayList<>();
        }
    }
}
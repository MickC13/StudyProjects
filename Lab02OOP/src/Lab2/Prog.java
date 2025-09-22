package Lab2;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс приложения для учета клиентов почтового отделения.
 * Реализует графический интерфейс пользователя для работы с данными клиентов.
 * 
 * @author [Mikhail & Nikita]
 * @version 1.0
 */

public class Prog {

    private JFrame mainFrame;
    private DefaultTableModel model;
    private JButton addButton, editButton, deleteButton, searchButton;
    private JToolBar toolBar;
    private JScrollPane scroll;
    private JTable clientsTable;
    private JTextField searchField, clientNameField, telephoneField, addressField, newspaperField;
    private JComboBox<String> surnameComboBox;
    
    public void show() {
        mainFrame = new JFrame("Pochta Rossii");
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        
        // Создание модели и таблицы
        String[] columns = {"Клиент", "Телефон", "Адрес", "Газета"};
        Object[][] data = {
            {"Ivanov", "451-50-70", "Kommunorský 22", "Rastishka"},
            {"Frolov", "789-63-45", "Lesnaya 44", "Basketbolchik"},
            {"Petrov", "225-25-52", "Sodovaya 13", "Modelist"}
        };
        
        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактирование ячеек напрямую
            }
        };
        
        clientsTable = new JTable(model);
        scroll = new JScrollPane(clientsTable);
        
        // Создание панели инструментов
        toolBar = new JToolBar("Toolbar");
        toolBar.setFloatable(false);
        
        // Создание кнопок с иконками
        try {
            addButton = new JButton(new ImageIcon("./images/ADD.png"));
            editButton = new JButton(new ImageIcon("./images/EDIT.png"));
            deleteButton = new JButton(new ImageIcon("./images/Recycle.jpg"));
            searchButton = new JButton(new ImageIcon("./images/search.png"));
        } catch (Exception e) {
            // Если изображения не найдены, используем текстовые кнопки
            addButton = new JButton("Добавить");
            editButton = new JButton("Редактировать");
            deleteButton = new JButton("Удалить");
            searchButton = new JButton("Поиск");
        }
        
        // Установка подсказок для кнопок
        addButton.setToolTipText("Добавить нового клиента");
        editButton.setToolTipText("Редактировать выбранного клиента");
        deleteButton.setToolTipText("Удалить выбранного клиента");
        searchButton.setToolTipText("Поиск клиента");
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        
        // Добавление разделителя
        toolBar.addSeparator();
        
        // Создание выпадающего списка для выбора фамилий
        surnameComboBox = new JComboBox<>();
        toolBar.add(new JLabel("Фамилия:"));
        toolBar.add(surnameComboBox);
        
        // Обновляем комбобокс
        updateSurnameComboBox();
        
        // Создание панели ввода данных
        JPanel inputPanel = createInputPanel();
        
        // Создание панели поиска
        JPanel searchPanel = createSearchPanel();
        
        // Компоновка компонентов на главном окне
        mainFrame.add(toolBar, BorderLayout.NORTH);
        mainFrame.add(scroll, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.NORTH);
        southPanel.add(searchPanel, BorderLayout.SOUTH);
        
        mainFrame.add(southPanel, BorderLayout.SOUTH);
        
        // Обработчики событий удалены
        
        // Отображение окна
        mainFrame.setVisible(true);
    }
    
    /**
     * Создает панель для ввода данных о клиенте
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Данные клиента"));
        
        inputPanel.add(new JLabel("Имя клиента:"));
        clientNameField = new JTextField();
        inputPanel.add(clientNameField);
        
        inputPanel.add(new JLabel("Телефон:"));
        telephoneField = new JTextField();
        inputPanel.add(telephoneField);
        
        inputPanel.add(new JLabel("Адрес:"));
        addressField = new JTextField();
        inputPanel.add(addressField);
        
        inputPanel.add(new JLabel("Газета:"));
        newspaperField = new JTextField();
        inputPanel.add(newspaperField);
        
        return inputPanel;
    }
    
    /**
     * Создает панель для поиска клиентов
     */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Поиск клиента"));
        
        searchField = new JTextField();
        
        searchPanel.add(new JLabel("Поиск:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        return searchPanel;
    }
    
    /**
     * Обновляет список фамилий в выпадающем списке
     */
    private void updateSurnameComboBox() {
        // Сохраняем текущий выбор
        String selectedSurname = (String) surnameComboBox.getSelectedItem();
        
        // Очищаем список
        surnameComboBox.removeAllItems();
        
        // Собираем уникальные фамилии из таблицы
        Set<String> surnames = new HashSet<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String surname = (String) model.getValueAt(i, 0);
            surnames.add(surname);
        }
        
        // Добавляем фамилии в комбобокс
        for (String surname : surnames) {
            surnameComboBox.addItem(surname);
        }
        
        // Восстанавливаем предыдущий выбор, если он еще существует
        if (selectedSurname != null && surnames.contains(selectedSurname)) {
            surnameComboBox.setSelectedItem(selectedSurname);
        } else if (surnameComboBox.getItemCount() > 0) {
            surnameComboBox.setSelectedIndex(0);
        }
    }
    
    /**
     * Очищает поля ввода
     */
    private void clearInputFields() {
        clientNameField.setText("");
        telephoneField.setText("");
        addressField.setText("");
        newspaperField.setText("");
    }
    
    public static void main(String[] args) {
        // Запуск приложения в потоке обработки событий Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Prog().show();
            }
        });
    }
}
package PostalSystem;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;

import Newspaper.Newspaper;
import Newspaper.NewspaperDataManager;
import Newspaper.Prog;

import Postman.Postman;
import Postman.PostmanDataManager;
import Postman.Program;

import OfficeOperator.Client;
import OfficeOperator.ClientDataManager;
import OfficeOperator.Proga;

import Districts.District;
import Districts.DistrictDataManager;
import Districts.DistrictsPanel;

public class MainApplication extends JFrame {
    
    public MainApplication() {
        setTitle("Почта России - Интегрированная система (Вариант 4)");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Создаем общий менеджер данных
        IntegratedDataManager dataManager = new IntegratedDataManager();
        
        // Создаем вкладки
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Создаем панели с передачей менеджера данных
        // Теперь передаем IntegratedDataManager, который реализует все интерфейсы
        tabbedPane.addTab("Управление клиентами", new Proga(dataManager));
        tabbedPane.addTab("Управление газетами", new Prog(dataManager));
        tabbedPane.addTab("Управление почтальонами", new Program(dataManager));
        tabbedPane.addTab("Управление районами", new DistrictsPanel(dataManager));
        
        // Панель статистики
        JPanel statsPanel = createStatisticsPanel(dataManager);
        tabbedPane.addTab("Статистика и отчеты", statsPanel);
        
        add(tabbedPane);
    }
    
    private JPanel createStatisticsPanel(IntegratedDataManager dataManager) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Сбор статистики
        updateStatistics(dataManager, statsArea);
        
        JScrollPane scrollPane = new JScrollPane(statsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton refreshButton = new JButton("Обновить статистику");
        JButton detailedReportButton = new JButton("Подробный отчет");
        JButton clearDataButton = new JButton("Очистить данные");
        JButton loadTestDataButton = new JButton("Загрузить тестовые данные");
        JButton autoAssignButton = new JButton("Автораспределение клиентов");
        
        refreshButton.addActionListener(e -> updateStatistics(dataManager, statsArea));
        detailedReportButton.addActionListener(e -> showDetailedReport(dataManager));
        clearDataButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, 
                "Вы уверены, что хотите очистить все данные?", 
                "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dataManager.loadTestData(); // Перезагружаем тестовые данные
                updateStatistics(dataManager, statsArea);
                JOptionPane.showMessageDialog(this, "Данные очищены и загружены тестовые!");
            }
        });
        
        loadTestDataButton.addActionListener(e -> {
            dataManager.loadTestData();
            updateStatistics(dataManager, statsArea);
            JOptionPane.showMessageDialog(this, "Тестовые данные загружены!");
        });
        
        autoAssignButton.addActionListener(e -> {
            dataManager.autoAssignClientsToPostmen();
            updateStatistics(dataManager, statsArea);
            JOptionPane.showMessageDialog(this, 
                "Клиенты автоматически распределены по почтальонам!", 
                "Автораспределение", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(detailedReportButton);
        buttonPanel.add(clearDataButton);
        buttonPanel.add(loadTestDataButton);
        buttonPanel.add(autoAssignButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateStatistics(IntegratedDataManager dataManager, JTextArea statsArea) {
        StringBuilder stats = new StringBuilder();
        stats.append("СТАТИСТИКА ПОЧТОВОЙ СИСТЕМЫ\n");
        stats.append("================================\n\n");
        
        stats.append("Общая статистика:\n");
        stats.append("  Клиенты: ").append(dataManager.getClientCount()).append("\n");
        stats.append("  Газеты/журналы: ").append(dataManager.getNewspaperCount()).append("\n");
        stats.append("  Почтальоны: ").append(dataManager.getPostmanCount()).append("\n");
        stats.append("  Районы: ").append(dataManager.getDistrictCount()).append("\n\n");
        
        // Статистика связей
        stats.append("Статистика связей:\n");
        
        // Клиенты с почтальонами
        int clientsWithPostmen = 0;
        for (Client client : dataManager.getClients()) {
            if (client.getPostmanId() != null) {
                clientsWithPostmen++;
            }
        }
        stats.append("  Клиентов с почтальонами: ").append(clientsWithPostmen)
             .append(" (").append(dataManager.getClientCount() > 0 ? 
             (clientsWithPostmen * 100 / dataManager.getClientCount()) : 0).append("%)\n");
        
        // Клиенты с газетами
        int clientsWithNewspapers = 0;
        for (Client client : dataManager.getClients()) {
            if (client.getNewspaper() != null && !client.getNewspaper().isEmpty()) {
                clientsWithNewspapers++;
            }
        }
        stats.append("  Клиентов с газетами: ").append(clientsWithNewspapers)
             .append(" (").append(dataManager.getClientCount() > 0 ? 
             (clientsWithNewspapers * 100 / dataManager.getClientCount()) : 0).append("%)\n");
        
        // Почтальоны с районами
        int postmenWithDistricts = 0;
        for (Postman postman : dataManager.getPostmen()) {
            if (postman.getDistrict() != null && !postman.getDistrict().isEmpty()) {
                postmenWithDistricts++;
            }
        }
        stats.append("  Почтальонов с районами: ").append(postmenWithDistricts)
             .append(" (").append(dataManager.getPostmanCount() > 0 ? 
             (postmenWithDistricts * 100 / dataManager.getPostmanCount()) : 0).append("%)\n\n");
        
        stats.append("Статистика по газетам (топ-5):\n");
        List<String> popularNewspapers = dataManager.getPopularNewspapers(5);
        for (String newspaper : popularNewspapers) {
            int count = dataManager.getSubscriberCount(newspaper);
            stats.append("  ").append(newspaper).append(": ").append(count).append(" подписчиков\n");
        }
        
        stats.append("\nРаспределение по районам:\n");
        for (District district : dataManager.getDistricts()) {
            int postmenCount = district.getPostmanCount();
            int clientsCount = dataManager.getClientsByDistrict(district.getName()).size();
            stats.append("  ").append(district.getName()).append(": ")
                 .append(postmenCount).append(" почтальонов, ")
                 .append(clientsCount).append(" клиентов\n");
        }
        
        statsArea.setText(stats.toString());
    }
    
    private void showDetailedReport(IntegratedDataManager dataManager) {
        StringBuilder report = new StringBuilder();
        report.append("ПОДРОБНЫЙ ОТЧЕТ СО СВЯЗЯМИ\n");
        report.append("==========================\n\n");
        
        report.append("КЛИЕНТЫ:\n");
        report.append("--------\n");
        for (Client client : dataManager.getClients()) {
            report.append("ID: ").append(client.getId())
                  .append(", Фамилия: ").append(client.getSurname())
                  .append(", Телефон: ").append(client.getPhone())
                  .append(", Адрес: ").append(client.getAddress())
                  .append(", Газета: ").append(client.getNewspaper())
                  .append(", Район: ").append(client.getDistrict() != null ? client.getDistrict() : "Не указан")
                  .append(", Почтальон ID: ").append(client.getPostmanId() != null ? client.getPostmanId() : "Не назначен")
                  .append("\n");
        }
        
        report.append("\nГАЗЕТЫ:\n");
        report.append("-------\n");
        for (Newspaper newspaper : dataManager.getNewspapers()) {
            int subscriberCount = dataManager.getSubscriberCount(newspaper.getTitle());
            report.append("ID: ").append(newspaper.getId())
                  .append(", Название: ").append(newspaper.getTitle())
                  .append(", День выпуска: ").append(newspaper.getReleaseDay())
                  .append(", Подписчиков: ").append(subscriberCount)
                  .append("\n");
        }
        
        report.append("\nПОЧТАЛЬОНЫ:\n");
        report.append("----------\n");
        for (Postman postman : dataManager.getPostmen()) {
            int clientCount = dataManager.getClientCount(postman.getId());
            report.append("ID: ").append(postman.getId())
                  .append(", ФИО: ").append(postman.getFullName())
                  .append(", Телефон: ").append(postman.getPhone())
                  .append(", Район: ").append(postman.getDistrict() != null ? postman.getDistrict() : "Не назначен")
                  .append(", Стаж: ").append(postman.getExperience())
                  .append(" лет, Клиентов: ").append(clientCount)
                  .append("\n");
        }
        
        report.append("\nРАЙОНЫ:\n");
        report.append("-------\n");
        for (District district : dataManager.getDistricts()) {
            int clientCount = dataManager.getClientsByDistrict(district.getName()).size();
            report.append("ID: ").append(district.getId())
                  .append(", Название: ").append(district.getName())
                  .append(", Индекс: ").append(district.getPostalCode())
                  .append(", Население: ").append(district.getPopulation())
                  .append(", Площадь: ").append(district.getArea()).append(" км²")
                  .append(", Почтальонов: ").append(district.getPostmanCount())
                  .append(", Клиентов: ").append(clientCount)
                  .append("\n");
        }
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Подробный отчет со связями", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
    }
}
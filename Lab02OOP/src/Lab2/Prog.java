package Lab2;

import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.table.DefaultTableModel;

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
    private JTextField searchField, clientNameField, newspaperField;
    private JComboBox<String> filterComboBox;
    
    public void show() {
    	 mainFrame = new JFrame("Pochta Rossii");
         mainFrame.setSize(800, 500);
         mainFrame.setLocationRelativeTo(null);
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         mainFrame.setLayout(new BorderLayout());
         
         
         toolBar = new JToolBar("Toolbar");
         toolBar.setFloatable(false);
         
         
         try {
             addButton = new JButton(new ImageIcon("./images/ADD.png"));
             editButton = new JButton(new ImageIcon("./images/EDIT.png"));
             deleteButton = new JButton(new ImageIcon("./images/Recycle.jpg"));
         } catch (Exception e) {
             // Если изображения не найдены, используем текстовые кнопки
             addButton = new JButton("Добавить");
             editButton = new JButton("Редактировать");
             deleteButton = new JButton("Удалить");
         }

         
         toolBar.add(addButton);
         toolBar.add(editButton);
         toolBar.add(deleteButton);
         
         // Добавление разделителя
         toolBar.addSeparator();
         
         // Создание выпадающего списка для фильтрации
         filterComboBox = new JComboBox<>(new String[]{"Все клиенты", "С подпиской", "Без подписки"});
         toolBar.add(filterComboBox);
         
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 System.out.println("Hello Eclipse!");

	}

}

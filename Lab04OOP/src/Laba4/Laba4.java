package Laba4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

/**
 * Main class of "Pochta Rossii" application for client management.
 * Contains graphical interface for displaying and managing client list.
 * Includes custom exception handling for data validation and client operations.
 * 
 * @author Mikhail
 * @version 2.0
 */
public class Laba4 {
    
    private JTextField clientNameField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField newspaperField;
    private JTextField searchField;
    private DefaultTableModel model;
    private JTable clientsTable;
    private JComboBox<String> surnameComboBox;
    
    /**
     * Creates and displays main application window with client management interface.
     * Initializes all GUI components and sets up event listeners.
     */
    public void show() {
        JFrame mainFrame = new JFrame("Pochta Rossii");
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        
        // Create model and table with initial data
        String[] columns = {"Client", "Phone", "Address", "Newspaper"};
        Object[][] data = {
            {"Ivanov", "451-50-70", "Kommunorský 22", "Rastishka"},
            {"Frolov", "789-63-45", "Lesnaya 44", "Basketbolchik"},
            {"Petrov", "225-25-52", "Sodovaya 13", "Modelist"}
        };
        
        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        clientsTable = new JTable(model);
        JScrollPane scroll = new JScrollPane(clientsTable);
        
        // Create toolbar with buttons
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = createButton("ADD.png", "Add");
        JButton editButton = createButton("EDIT.png", "Edit");
        JButton deleteButton = createButton("Recycle.jpg", "Delete");
        JButton searchButton = createButton("SEARCH.png", "Search");
        
        setupButtonListeners(addButton, editButton, deleteButton, searchButton, mainFrame);
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        
        surnameComboBox = new JComboBox<>();
        updateSurnameComboBox(model, surnameComboBox);
        setupComboBoxListener(surnameComboBox, mainFrame);
        
        toolBar.add(new JLabel("Surname:"));
        toolBar.add(surnameComboBox);
        
        // Create input and search panels
        JPanel inputPanel = createInputPanel();
        JPanel searchPanel = createSearchPanel(searchButton);
        
        // Layout components
        mainFrame.add(toolBar, BorderLayout.NORTH);
        mainFrame.add(scroll, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.NORTH);
        southPanel.add(searchPanel, BorderLayout.SOUTH);
        mainFrame.add(southPanel, BorderLayout.SOUTH);
        
        setupTableListener(clientsTable, mainFrame);
        mainFrame.setVisible(true);
    }
    
    /**
     * Sets up event listeners for all interface buttons.
     * Handles Add, Edit, Delete, and Search operations with exception handling.
     * 
     * @param addButton button for adding new clients
     * @param editButton button for editing existing clients
     * @param deleteButton button for deleting clients
     * @param searchButton button for searching clients
     * @param parent the parent frame for dialog messages
     */
    private void setupButtonListeners(JButton addButton, JButton editButton, 
                                     JButton deleteButton, JButton searchButton, 
                                     JFrame parent) {
        
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = clientNameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String address = addressField.getText().trim();
                    String newspaper = newspaperField.getText().trim();
                    
                    validateClientData(name, phone, address, newspaper);
                    
                    model.addRow(new Object[]{name, phone, address, newspaper});
                    updateSurnameComboBox(model, surnameComboBox);
                    clearInputFields();
                    
                    JOptionPane.showMessageDialog(parent, 
                        "Client '" + name + "' successfully added!", 
                        "Add Client", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (MyException ex) {
                    JOptionPane.showMessageDialog(parent, 
                        ex.getMessage(), 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, 
                        "Unknown error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = clientsTable.getSelectedRow();
                    
                    if (selectedRow == -1) {
                        throw new ClientNotFoundException("Please select a client to edit!");
                    }
                    
                    String name = clientNameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String address = addressField.getText().trim();
                    String newspaper = newspaperField.getText().trim();
                    
                    validateClientData(name, phone, address, newspaper);
                    
                    model.setValueAt(name, selectedRow, 0);
                    model.setValueAt(phone, selectedRow, 1);
                    model.setValueAt(address, selectedRow, 2);
                    model.setValueAt(newspaper, selectedRow, 3);
                    
                    updateSurnameComboBox(model, surnameComboBox);
                    clearInputFields();
                    
                    JOptionPane.showMessageDialog(parent, 
                        "Client '" + name + "' successfully updated!", 
                        "Edit Client", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (ClientNotFoundException ex) {
                    JOptionPane.showMessageDialog(parent, 
                        ex.getMessage(), 
                        "Edit Error", 
                        JOptionPane.WARNING_MESSAGE);
                } catch (MyException ex) {
                    JOptionPane.showMessageDialog(parent, 
                        ex.getMessage(), 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = clientsTable.getSelectedRow();
                    
                    if (selectedRow == -1) {
                        throw new ClientNotFoundException("Please select a client to delete!");
                    }
                    
                    String clientName = (String) model.getValueAt(selectedRow, 0);
                    
                    int confirm = JOptionPane.showConfirmDialog(parent, 
                        "Are you sure you want to delete client '" + clientName + "'?", 
                        "Confirm Deletion", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        model.removeRow(selectedRow);
                        updateSurnameComboBox(model, surnameComboBox);
                        JOptionPane.showMessageDialog(parent, 
                            "Client '" + clientName + "' deleted!", 
                            "Deletion Complete", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                } catch (ClientNotFoundException ex) {
                    JOptionPane.showMessageDialog(parent, 
                        ex.getMessage(), 
                        "Delete Error", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String searchText = searchField.getText().trim().toLowerCase();
                    
                    if (searchText.isEmpty()) {
                        throw new MyException("Please enter text to search!");
                    }
                    
                    boolean found = false;
                    clientsTable.clearSelection();
                    
                    for (int row = 0; row < model.getRowCount(); row++) {
                        for (int col = 0; col < model.getColumnCount(); col++) {
                            String cellValue = model.getValueAt(row, col).toString().toLowerCase();
                            if (cellValue.contains(searchText)) {
                                clientsTable.addRowSelectionInterval(row, row);
                                clientsTable.scrollRectToVisible(clientsTable.getCellRect(row, 0, true));
                                found = true;
                                break;
                            }
                        }
                    }
                    
                    if (!found) {
                        throw new ClientNotFoundException("No clients found for query: '" + searchText + "'");
                    } else {
                        JOptionPane.showMessageDialog(parent, 
                            "Found clients for query: '" + searchText + "'", 
                            "Search Complete", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                } catch (MyException | ClientNotFoundException ex) {
                    JOptionPane.showMessageDialog(parent, 
                        ex.getMessage(), 
                        "Search Error", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }
    
    /**
     * Sets up event listener for surname combobox selection.
     * Automatically fills input fields with selected client's data.
     * 
     * @param comboBox the combobox containing client surnames
     * @param parent the parent frame for dialog messages
     */
    private void setupComboBoxListener(JComboBox<String> comboBox, JFrame parent) {
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSurname = (String) comboBox.getSelectedItem();
                if (selectedSurname != null && !selectedSurname.equals("")) {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (selectedSurname.equals(model.getValueAt(i, 0))) {
                            clientNameField.setText(selectedSurname);
                            phoneField.setText(model.getValueAt(i, 1).toString());
                            addressField.setText(model.getValueAt(i, 2).toString());
                            newspaperField.setText(model.getValueAt(i, 3).toString());
                            
                            clientsTable.setRowSelectionInterval(i, i);
                            clientsTable.scrollRectToVisible(clientsTable.getCellRect(i, 0, true));
                            
                            JOptionPane.showMessageDialog(parent, 
                                "Loaded client data: " + selectedSurname, 
                                "Data Loaded", 
                                JOptionPane.INFORMATION_MESSAGE);
                            break;
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Sets up listener for table row selection.
     * Automatically updates combobox when table row is selected.
     * 
     * @param table the clients table
     * @param parent the parent frame
     */
    private void setupTableListener(JTable table, JFrame parent) {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedName = (String) table.getValueAt(selectedRow, 0);
                    surnameComboBox.setSelectedItem(selectedName);
                }
            }
        });
    }
    
    /**
     * Validates client data according to business rules.
     * Throws MyException if data doesn't meet requirements.
     * 
     * @param name client name to validate
     * @param phone phone number to validate
     * @param address address to validate
     * @param newspaper newspaper name to validate
     * @throws MyException if validation fails with specific error message
     */
    private void validateClientData(String name, String phone, String address, String newspaper) 
            throws MyException {
        
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || newspaper.isEmpty()) {
            throw new MyException("All fields must be filled!");
        }
        
        if (!name.matches("[a-zA-Zа-яА-Я]+")) {
            throw new MyException("Client name must contain only letters!");
        }
        
        if (!phone.matches("\\d{3}-\\d{2}-\\d{2}")) {
            throw new MyException("Phone must be in format XXX-XX-XX!");
        }
    }
    
    /**
     * Clears all input fields in the client data form.
     */
    private void clearInputFields() {
        clientNameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        newspaperField.setText("");
    }
    
    /**
     * Creates a button with icon and tooltip.
     * Falls back to text button if icon cannot be loaded.
     * 
     * @param iconName name of the icon file
     * @param tooltip tooltip text for the button
     * @return the created button
     */
    private JButton createButton(String iconName, String tooltip) {
        try {
            JButton button = new JButton(new ImageIcon("./images/" + iconName));
            button.setToolTipText(tooltip);
            return button;
        } catch (Exception e) {
            JButton button = new JButton(tooltip);
            button.setToolTipText(tooltip);
            return button;
        }
    }
    
    /**
     * Creates the input panel with client data fields.
     * 
     * @return the configured input panel
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Client Data"));
        
        String[] labels = {"Client Name:", "Phone:", "Address:", "Newspaper:"};
        String[] placeholders = {"Letters only", "XXX-XX-XX", "Enter address", "Enter newspaper name"};
        
        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i]));
            JTextField textField = new JTextField();
            textField.setToolTipText(placeholders[i]);
            panel.add(textField);
            
            switch(i) {
                case 0: clientNameField = textField; break;
                case 1: phoneField = textField; break;
                case 2: addressField = textField; break;
                case 3: newspaperField = textField; break;
            }
        }
        
        return panel;
    }
    
    /**
     * Creates the search panel with search field and button.
     * 
     * @param searchButton the search button to add to the panel
     * @return the configured search panel
     */
    private JPanel createSearchPanel(JButton searchButton) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Client Search"));
        
        searchField = new JTextField();
        panel.add(new JLabel("Search:"), BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Updates the surname combobox with current data from the table model.
     * 
     * @param model the table model containing client data
     * @param comboBox the combobox to update
     */
    private void updateSurnameComboBox(DefaultTableModel model, JComboBox<String> comboBox) {
        String selected = (String) comboBox.getSelectedItem();
        comboBox.removeAllItems();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            comboBox.addItem((String) model.getValueAt(i, 0));
        }
        
        if (selected != null && comboBox.getItemCount() > 0) {
            comboBox.setSelectedItem(selected);
        }
    }
    
    /**
     * Main method that launches the application.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Laba4().show();
            }
        });
    }
}
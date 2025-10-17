package Lab05;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.awt.FileDialog;

/**
 * Main class of "Pochta Rossii" application for client management.
 * Extended with file operations for saving and loading client data.
 * Includes UTF-8 support for proper Russian character handling.
 * 
 * @author Mikhail
 * @version 3.0
 */
public class Prog {
    
    private JTextField clientNameField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField newspaperField;
    private JTextField searchField;
    private DefaultTableModel model;
    private JTable clientsTable;
    private JComboBox<String> surnameComboBox;
    private JFrame mainFrame;
    
    /**
     * Creates and displays main application window with client management interface.
     * Includes file operations for data persistence.
     */
    public void show() {
        mainFrame = new JFrame("Pochta Rossii - File Operations");
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
        
        // Create toolbar with buttons including file operations
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = createButton("ADD.png", "Add");
        JButton editButton = createButton("EDIT.png", "Edit");
        JButton deleteButton = createButton("Recycle.jpg", "Delete");
        JButton searchButton = createButton("SEARCH.png", "Search");
        JButton saveButton = createButton("SAVE.png", "Save to file");
        JButton loadButton = createButton("LOAD.png", "Load from file");
        
        setupButtonListeners(addButton, editButton, deleteButton, searchButton, saveButton, loadButton);
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(saveButton);
        toolBar.add(loadButton);
        toolBar.addSeparator();
        
        surnameComboBox = new JComboBox<>();
        updateSurnameComboBox(model, surnameComboBox);
        setupComboBoxListener(surnameComboBox);
        
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
        
        setupTableListener(clientsTable);
        mainFrame.setVisible(true);
    }
    
    /**
     * Sets up event listeners for all interface buttons including file operations.
     */
    private void setupButtonListeners(JButton addButton, JButton editButton, 
                                     JButton deleteButton, JButton searchButton,
                                     JButton saveButton, JButton loadButton) {
        
        // Add button listener
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
                    
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Client '" + name + "' successfully added!", 
                        "Add Client", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (MyException ex) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        ex.getMessage(), 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Unknown error: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Edit button listener
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
                    
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Client '" + name + "' successfully updated!", 
                        "Edit Client", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (ClientNotFoundException ex) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        ex.getMessage(), 
                        "Edit Error", 
                        JOptionPane.WARNING_MESSAGE);
                } catch (MyException ex) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        ex.getMessage(), 
                        "Input Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Delete button listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int selectedRow = clientsTable.getSelectedRow();
                    
                    if (selectedRow == -1) {
                        throw new ClientNotFoundException("Please select a client to delete!");
                    }
                    
                    String clientName = (String) model.getValueAt(selectedRow, 0);
                    
                    int confirm = JOptionPane.showConfirmDialog(mainFrame, 
                        "Are you sure you want to delete client '" + clientName + "'?", 
                        "Confirm Deletion", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        model.removeRow(selectedRow);
                        updateSurnameComboBox(model, surnameComboBox);
                        JOptionPane.showMessageDialog(mainFrame, 
                            "Client '" + clientName + "' deleted!", 
                            "Deletion Complete", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                } catch (ClientNotFoundException ex) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        ex.getMessage(), 
                        "Delete Error", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        // Search button listener
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
                        JOptionPane.showMessageDialog(mainFrame, 
                            "Found clients for query: '" + searchText + "'", 
                            "Search Complete", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                } catch (MyException | ClientNotFoundException ex) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        ex.getMessage(), 
                        "Search Error", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        // Save button listener
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDataToFile();
            }
        });
        
        // Load button listener
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDataFromFile();
            }
        });
    }
    
    /**
     * Saves table data to a text file using FileDialog for file selection.
     * Uses CSV format with comma separators for data storage.
     * Supports UTF-8 encoding for Russian characters.
     */
    private void saveDataToFile() {
        FileDialog saveDialog = new FileDialog(mainFrame, "Save Client Data", FileDialog.SAVE);
        saveDialog.setFile("clients.txt");
        saveDialog.setVisible(true);
        
        String directory = saveDialog.getDirectory();
        String fileName = saveDialog.getFile();
        
        if (fileName == null) {
            return; // User cancelled
        }
        
        String fullPath = directory + fileName;
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPath), "UTF-8"))) {
            // Write column headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();
            
            // Write data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    String value = model.getValueAt(i, j).toString();
                    writer.write(value);
                    if (j < model.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
            
            JOptionPane.showMessageDialog(mainFrame, 
                "Data successfully saved to: " + fileName, 
                "Save Complete", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error saving file: " + ex.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Loads table data from a text file using FileDialog for file selection.
     * Expects CSV format with comma separators.
     * Supports UTF-8 encoding for Russian characters.
     */
    private void loadDataFromFile() {
        FileDialog loadDialog = new FileDialog(mainFrame, "Load Client Data", FileDialog.LOAD);
        loadDialog.setFile("*.txt");
        loadDialog.setVisible(true);
        
        String directory = loadDialog.getDirectory();
        String fileName = loadDialog.getFile();
        
        if (fileName == null) {
            return; // User cancelled
        }
        
        String fullPath = directory + fileName;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullPath), "UTF-8"))) {
            // Clear existing data
            model.setRowCount(0);
            
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                
                if (isFirstLine) {
                    isFirstLine = false;
                    // Optional: validate header
                    if (!line.contains("Client") || !line.contains("Phone")) {
                        JOptionPane.showMessageDialog(mainFrame, 
                            "File format error: invalid header", 
                            "Load Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    continue; // Skip header line
                }
                
                String[] rowData = line.split(",");
                if (rowData.length == 4) {
                    model.addRow(rowData);
                } else {
                    System.out.println("Skipping invalid line " + lineNumber + ": " + line);
                }
            }
            
            updateSurnameComboBox(model, surnameComboBox);
            clearInputFields();
            
            JOptionPane.showMessageDialog(mainFrame, 
                "Data successfully loaded from: " + fileName + "\nLoaded " + model.getRowCount() + " clients", 
                "Load Complete", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(mainFrame, 
                "File not found: " + fileName, 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error reading file: " + ex.getMessage(), 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Unexpected error: " + ex.getMessage(), 
                "Load Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Sets up event listener for surname combobox selection.
     * Automatically fills input fields with selected client's data.
     * 
     * @param comboBox the combobox containing client surnames
     */
    private void setupComboBoxListener(JComboBox<String> comboBox) {
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
                            
                            JOptionPane.showMessageDialog(mainFrame, 
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
     */
    private void setupTableListener(JTable table) {
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
        comboBox.addItem(""); // Empty option
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String surname = (String) model.getValueAt(i, 0);
            if (surname != null && !surname.trim().isEmpty()) {
                comboBox.addItem(surname);
            }
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
                new Prog().show();
            }
        });
    }
    
    /**
     * Custom exception for handling data validation errors in the application.
     * Controls the following situations:
     * 1. Empty fields when adding/editing clients
     * 2. Invalid client name format (contains digits or special characters)
     * 3. Invalid phone number format (not matching XXX-XX-XX pattern)
     * 4. Empty search query
     * 
     * This is a checked exception that must be either handled or declared in method signatures.
     * 
     * @author Mikhail
     * @version 1.0
     */
    public static class MyException extends Exception {
        
        /**
         * Constructs a new MyException with the specified detail message.
         * 
         * @param message the detail message describing the validation error
         */
        public MyException(String message) {
            super(message);
        }
        
        /**
         * Constructs a new MyException with the specified detail message and cause.
         * 
         * @param message the detail message describing the validation error
         * @param cause the underlying cause of this exception
         */
        public MyException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Custom exception for handling situations when clients cannot be found in the system.
     * Controls the following situations:
     * 5. Attempt to delete a client without selection
     * 6. Attempt to edit a client without selection  
     * 7. Search operation returns no results
     * 
     * This is a checked exception that requires explicit handling in methods
     * that may encounter missing client scenarios.
     * 
     * @author Mikhail
     * @version 1.0
     */
    public static class ClientNotFoundException extends Exception {
        
        /**
         * Constructs a new ClientNotFoundException with the specified detail message.
         * 
         * @param message the detail message describing the missing client scenario
         */
        public ClientNotFoundException(String message) {
            super(message);
        }
        
        /**
         * Constructs a new ClientNotFoundException with the specified detail message and cause.
         * 
         * @param message the detail message describing the missing client scenario
         * @param cause the underlying cause of this exception
         */
        public ClientNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
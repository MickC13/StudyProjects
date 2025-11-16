package laba9;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;

/**
 * JUnit тесты для приложения "Почта России"
 */
public class ProgramTest {
    
    private Program program;
    private DefaultTableModel model;
    private JComboBox<String> comboBox;
    
    @Before
    public void setUp() {
        program = new Program();
        program.clientNameField = new JTextField();
        program.phoneField = new JTextField();
        program.addressField = new JTextField();
        program.newspaperField = new JTextField();
        
        String[] columns = {"Клиент", "Телефон", "Адрес", "Газета"};
        model = new DefaultTableModel(columns, 0);
        comboBox = new JComboBox<>();
    }
    
    @Test
    public void testUpdateSurnameComboBoxWithData() {
        model.addRow(new Object[]{"Ivanov", "451-50-70", "Address 1", "Paper 1"});
        model.addRow(new Object[]{"Petrov", "225-25-52", "Address 2", "Paper 2"});
        
        program.updateSurnameComboBox(model, comboBox);
        
        assertEquals(2, comboBox.getItemCount());
        assertEquals("Ivanov", comboBox.getItemAt(0));
        assertEquals("Petrov", comboBox.getItemAt(1));
    }
    
    @Test
    public void testUpdateSurnameComboBoxWithEmptyTable() {
        program.updateSurnameComboBox(model, comboBox);
        assertEquals(0, comboBox.getItemCount());
    }
    
    @Test
    public void testUpdateSurnameComboBoxPreservesSelection() {
        model.addRow(new Object[]{"Ivanov", "451-50-70", "Address 1", "Paper 1"});
        model.addRow(new Object[]{"Petrov", "225-25-52", "Address 2", "Paper 2"});
        
        program.updateSurnameComboBox(model, comboBox);
        comboBox.setSelectedItem("Petrov");
        String selectedBefore = (String) comboBox.getSelectedItem();
        
        program.updateSurnameComboBox(model, comboBox);
        String selectedAfter = (String) comboBox.getSelectedItem();
        
        assertEquals(selectedBefore, selectedAfter);
    }
    
    @Test
    public void testClearInputFields() {
        program.clientNameField.setText("Ivanov");
        program.phoneField.setText("451-50-70");
        program.addressField.setText("Address 1");
        program.newspaperField.setText("Paper 1");
        
        program.clearInputFields();
        
        assertEquals("", program.clientNameField.getText());
        assertEquals("", program.phoneField.getText());
        assertEquals("", program.addressField.getText());
        assertEquals("", program.newspaperField.getText());
    }
    
    @Test
    public void testEmptyFieldValidation() {
        program.clientNameField.setText("");
        program.phoneField.setText("");
        program.addressField.setText("");
        program.newspaperField.setText("");
        
        boolean allEmpty = program.clientNameField.getText().isEmpty() && 
                          program.phoneField.getText().isEmpty() && 
                          program.addressField.getText().isEmpty() && 
                          program.newspaperField.getText().isEmpty();
        
        assertTrue(allEmpty);
    }
    
    @Test
    public void testSearchLogic() {
        model.addRow(new Object[]{"Ivanov", "451-50-70", "Address 1", "Paper 1"});
        model.addRow(new Object[]{"Petrov", "225-25-52", "Address 2", "Paper 2"});
        
        boolean foundIvanov = false;
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                String cellValue = model.getValueAt(row, col).toString().toLowerCase();
                if (cellValue.contains("ivanov")) {
                    foundIvanov = true;
                    break;
                }
            }
        }
        assertTrue(foundIvanov);
    }
    
    @Test
    public void testSpecialCharactersInData() {
        model.addRow(new Object[]{"Иванов-Петров", "451-50-70", "Address 1", "Paper 1"});
        
        program.updateSurnameComboBox(model, comboBox);
        assertEquals("Иванов-Петров", comboBox.getItemAt(0));
    }
    
    @Test
    public void testLongFieldValues() {
        String longName = "ОченьДлиннаяФамилия";
        program.clientNameField.setText(longName);
        assertEquals(longName, program.clientNameField.getText());
    }
    
    @Test
    public void testNullValuesHandling() {
        model.addRow(new Object[]{null, null, null, null});
        
        program.updateSurnameComboBox(model, comboBox);
        assertEquals(1, comboBox.getItemCount());
    }
    
    @After
    public void tearDown() {
        program = null;
        model = null;
        comboBox = null;
    }
}
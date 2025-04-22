package library.ui.main.dialogs;

import library.service.UserService;
import library.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Диалог за добавяне на нов потребител
 */
public class AddUserDialog extends JDialog {
    
    private UserService userService;
    
    // Компоненти за въвеждане на данни
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton addButton;
    private JButton cancelButton;
    
    /**
     * Конструктор
     * @param parent родителският компонент
     * @param userService сервиз за потребители
     */
    public AddUserDialog(Window parent, UserService userService) {
        super(parent, "Добавяне на нов потребител", ModalityType.APPLICATION_MODAL);
        this.userService = userService;
        
        // Настройки на диалога
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Инициализация на компонентите
        initComponents();
        
        // Разположение на компонентите
        layoutComponents();
        
        // Добавяне на слушатели за събития
        addEventListeners();
    }
    
    /**
     * Инициализира компонентите на диалога
     */
    private void initComponents() {
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(new String[] {"потребител", "администратор"});
        addButton = new JButton("Добави");
        cancelButton = new JButton("Отказ");
    }
    
    /**
     * Разполага компонентите в диалога
     */
    private void layoutComponents() {
        // Основен панел с подредба тип BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Панел за формата
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.add(new JLabel("Име:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Имейл:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Парола:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Потвърждение на паролата:"));
        formPanel.add(confirmPasswordField);
        formPanel.add(new JLabel("Роля:"));
        formPanel.add(roleComboBox);
        mainPanel.add(formPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);
        
        // Добавяне на основния панел към диалога
        getContentPane().add(mainPanel);
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за бутона "Добави"
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        
        // Слушател за бутона "Отказ"
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    /**
     * Добавя нов потребител
     */
    private void addUser() {
        // Извличане на въведените данни
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(name, email, password, confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Моля, попълнете всички полета!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Моля, въведете валиден имейл адрес!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtils.isStrongPassword(password)) {
            JOptionPane.showMessageDialog(this,
                    "Паролата трябва да бъде поне 8 символа с цифри и букви!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtils.areEqual(password, confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Паролите не съвпадат!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Проверка дали имейлът вече съществува
        if (userService.emailExists(email)) {
            JOptionPane.showMessageDialog(this,
                    "Потребител с този имейл вече съществува!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Добавяне на потребителя
        int userId = userService.createUser(name, email, password, role);
        
        if (userId > 0) {
            JOptionPane.showMessageDialog(this,
                    "Потребителят е добавен успешно!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Грешка при добавяне на потребителя!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
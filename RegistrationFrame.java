package library.ui;

import library.service.AuthenticationService;
import library.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Прозорец за регистрация на нов потребител
 */
public class RegistrationFrame extends JFrame {
    
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    
    private AuthenticationService authService;
    private JFrame parentFrame;
    
    /**
     * Конструктор
     * @param parentFrame родителският прозорец (обикновено LoginFrame)
     */
    public RegistrationFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.authService = new AuthenticationService();
        
        // Настройки на прозореца
        setTitle("Регистрация в библиотечната система");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(parentFrame); // Центриране спрямо родителя
        setResizable(false);
        
        // Инициализация на компонентите
        initComponents();
        
        // Разположение на компонентите
        layoutComponents();
        
        // Добавяне на слушатели за събития
        addEventListeners();
        
        // Добавяне на слушател за затваряне на прозореца
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentFrame.setVisible(true); // Показване на родителския прозорец
            }
        });
    }
    
    /**
     * Инициализира компонентите на прозореца
     */
    private void initComponents() {
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        registerButton = new JButton("Регистрация");
        cancelButton = new JButton("Отказ");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
    }
    
    /**
     * Разполага компонентите в контейнера
     */
    private void layoutComponents() {
        // Основен панел с подредба тип BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Заглавие
        JLabel titleLabel = new JLabel("Регистрация на нов потребител");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за формата
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 10));
        formPanel.add(new JLabel("Име:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Имейл:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Парола:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Потвърдете паролата:"));
        formPanel.add(confirmPasswordField);
        mainPanel.add(formPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Панел за статус
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel);
        
        // Добавяне на основния панел към прозореца
        getContentPane().add(mainPanel);
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за бутона "Регистрация"
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRegistration();
            }
        });
        
        // Слушател за бутона "Отказ"
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Затваряне на прозореца за регистрация
                parentFrame.setVisible(true); // Показване на родителския прозорец
            }
        });
        
        // Слушател за клавиша Enter в полето за потвърждение на паролата
        confirmPasswordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptRegistration();
            }
        });
    }
    
    /**
     * Опит за регистрация на нов потребител
     */
    private void attemptRegistration() {
        // Извличане на въведените данни
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(name, email, password, confirmPassword)) {
            statusLabel.setText("Моля, попълнете всички полета!");
            return;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            statusLabel.setText("Невалиден имейл адрес!");
            return;
        }
        
        if (!ValidationUtils.isStrongPassword(password)) {
            statusLabel.setText("Паролата трябва да бъде поне 8 символа с цифри и букви!");
            return;
        }
        
        if (!ValidationUtils.areEqual(password, confirmPassword)) {
            statusLabel.setText("Паролите не съвпадат!");
            return;
        }
        
        // Опит за регистрация
        int userId = authService.register(name, email, password, confirmPassword);
        if (userId > 0) {
            // Успешна регистрация
            JOptionPane.showMessageDialog(this,
                    "Регистрацията е успешна! Моля, влезте със своите данни.",
                    "Успешна регистрация",
                    JOptionPane.INFORMATION_MESSAGE);
            
            dispose(); // Затваряне на прозореца за регистрация
            parentFrame.setVisible(true); // Показване на прозореца за вход
        } else {
            // Неуспешна регистрация
            statusLabel.setText("Грешка при регистрацията. Този имейл може би вече съществува.");
        }
    }
}
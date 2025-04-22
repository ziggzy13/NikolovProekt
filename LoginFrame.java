package library.ui;

import library.service.AuthenticationService;
import library.ui.main.MainFrame;
import library.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Прозорец за вход в системата
 */
public class LoginFrame extends JFrame {
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    
    private AuthenticationService authService;
    
    /**
     * Конструктор
     */
    public LoginFrame() {
        authService = new AuthenticationService();
        
        // Настройки на прозореца
        setTitle("Вход в библиотечната система");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Центриране на екрана
        setResizable(false);
        
        // Инициализация на компонентите
        initComponents();
        
        // Разположение на компонентите
        layoutComponents();
        
        // Добавяне на слушатели за събития
        addEventListeners();
    }
    
    /**
     * Инициализира компонентите на прозореца
     */
    private void initComponents() {
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Вход");
        registerButton = new JButton("Регистрация");
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
        
        // Лого и заглавие
        JLabel logoLabel = new JLabel("Библиотечна система");
        logoLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за формата
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.add(new JLabel("Имейл:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Парола:"));
        formPanel.add(passwordField);
        mainPanel.add(formPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
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
        // Слушател за бутона "Вход"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        
        // Слушател за бутона "Регистрация"
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationFrame();
            }
        });
        
        // Слушател за клавиша Enter в полето за парола
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
    }
    
    /**
     * Опит за влизане в системата
     */
    private void attemptLogin() {
        // Извличане на въведените данни
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        
        // Валидация на входните данни
        if (!ValidationUtils.isNotEmpty(email) || !ValidationUtils.isNotEmpty(password)) {
            statusLabel.setText("Моля, попълнете всички полета!");
            return;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            statusLabel.setText("Невалиден имейл адрес!");
            return;
        }
        
        // Опит за вход
        if (authService.login(email, password)) {
            // Успешен вход
            dispose(); // Затваряне на прозореца за вход
            
            // Отваряне на главния прозорец на приложението
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(authService);
                mainFrame.setVisible(true);
            });
        } else {
            // Неуспешен вход
            statusLabel.setText("Грешен имейл или парола!");
            passwordField.setText(""); // Изчистване на паролата
        }
    }
    
    /**
     * Отваря прозореца за регистрация
     */
    private void openRegistrationFrame() {
        SwingUtilities.invokeLater(() -> {
            RegistrationFrame regFrame = new RegistrationFrame(this);
            regFrame.setVisible(true);
            this.setVisible(false); // Скриване на прозореца за вход
        });
    }
}
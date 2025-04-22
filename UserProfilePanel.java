package library.ui.main.panels;

import library.model.User;
import library.service.AuthenticationService;
import library.service.LoanService;
import library.service.UserService;
import library.ui.main.MainFrame;
import library.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Панел за управление на потребителския профил
 */
public class UserProfilePanel extends JPanel {
    
    private MainFrame mainFrame;
    private AuthenticationService authService;
    private UserService userService;
    private LoanService loanService;
    private User currentUser;
    
    // Компоненти за информация за профила
    private JLabel idLabel;
    private JLabel idValueLabel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel roleLabel;
    private JLabel roleValueLabel;
    
    // Компоненти за промяна на парола
    private JLabel currentPasswordLabel;
    private JPasswordField currentPasswordField;
    private JLabel newPasswordLabel;
    private JPasswordField newPasswordField;
    private JLabel confirmPasswordLabel;
    private JPasswordField confirmPasswordField;
    
    // Компоненти за статистика
    private JLabel loanCountLabel;
    private JLabel loanCountValueLabel;
    private JLabel activeLoansLabel;
    private JLabel activeLoansValueLabel;
    
    // Бутони
    private JButton updateProfileButton;
    private JButton changePasswordButton;
    
    /**
     * Конструктор
     * @param mainFrame главният прозорец на приложението
     * @param authService сервиз за автентикация
     */
    public UserProfilePanel(MainFrame mainFrame, AuthenticationService authService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.userService = new UserService();
        this.loanService = new LoanService();
        this.currentUser = authService.getCurrentUser();
        
        // Инициализация на компонентите
        initComponents();
        
        // Разположение на компонентите
        layoutComponents();
        
        // Добавяне на слушатели за събития
        addEventListeners();
        
        // Зареждане на данните
        refreshData();
    }
    
    /**
     * Инициализира компонентите на панела
     */
    private void initComponents() {
        // Компоненти за информация за профила
        idLabel = new JLabel("ID:");
        idValueLabel = new JLabel();
        
        nameLabel = new JLabel("Име:");
        nameField = new JTextField(20);
        
        emailLabel = new JLabel("Имейл:");
        emailField = new JTextField(20);
        
        roleLabel = new JLabel("Роля:");
        roleValueLabel = new JLabel();
        
        // Компоненти за промяна на парола
        currentPasswordLabel = new JLabel("Текуща парола:");
        currentPasswordField = new JPasswordField(20);
        
        newPasswordLabel = new JLabel("Нова парола:");
        newPasswordField = new JPasswordField(20);
        
        confirmPasswordLabel = new JLabel("Потвърдете паролата:");
        confirmPasswordField = new JPasswordField(20);
        
        // Компоненти за статистика
        loanCountLabel = new JLabel("Общ брой заемания:");
        loanCountValueLabel = new JLabel();
        
        activeLoansLabel = new JLabel("Активни заемания:");
        activeLoansValueLabel = new JLabel();
        
        // Бутони
        updateProfileButton = new JButton("Обнови профила");
        changePasswordButton = new JButton("Промени паролата");
    }
    
    /**
     * Разполага компонентите в панела
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Заглавие
        JLabel titleLabel = new JLabel("Потребителски профил", JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        
        // Основен панел с подредба тип BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Панел за информация за профила
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBorder(BorderFactory.createTitledBorder("Информация за профила"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Добавяне на компонентите за информация за профила
        gbc.gridx = 0; gbc.gridy = 0;
        profilePanel.add(idLabel, gbc);
        
        gbc.gridx = 1;
        profilePanel.add(idValueLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        profilePanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        profilePanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        profilePanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        profilePanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        profilePanel.add(roleLabel, gbc);
        
        gbc.gridx = 1;
        profilePanel.add(roleValueLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        profilePanel.add(updateProfileButton, gbc);
        
        // Добавяне на панела за информация за профила
        mainPanel.add(profilePanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за промяна на парола
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Промяна на парола"));
        
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Добавяне на компонентите за промяна на парола
        gbc.gridx = 0; gbc.gridy = 0;
        passwordPanel.add(currentPasswordLabel, gbc);
        
        gbc.gridx = 1;
        passwordPanel.add(currentPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        passwordPanel.add(newPasswordLabel, gbc);
        
        gbc.gridx = 1;
        passwordPanel.add(newPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        passwordPanel.add(confirmPasswordLabel, gbc);
        
        gbc.gridx = 1;
        passwordPanel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        passwordPanel.add(changePasswordButton, gbc);
        
        // Добавяне на панела за промяна на парола
        mainPanel.add(passwordPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за статистика
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBorder(BorderFactory.createTitledBorder("Статистика"));
        
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Добавяне на компонентите за статистика
        gbc.gridx = 0; gbc.gridy = 0;
        statsPanel.add(loanCountLabel, gbc);
        
        gbc.gridx = 1;
        statsPanel.add(loanCountValueLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        statsPanel.add(activeLoansLabel, gbc);
        
        gbc.gridx = 1;
        statsPanel.add(activeLoansValueLabel, gbc);
        
        // Добавяне на панела за статистика
        mainPanel.add(statsPanel);
        
        // Добавяне на основния панел към панела
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за бутона "Обнови профила"
        updateProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
        
        // Слушател за бутона "Промени паролата"
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
    }
    
    /**
     * Зарежда данните в компонентите
     */
    public void refreshData() {
        // Презареждане на текущия потребител
        currentUser = authService.getCurrentUser();
        
        if (currentUser != null) {
            // Попълване на информацията за профила
            idValueLabel.setText(String.valueOf(currentUser.getUserId()));
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            roleValueLabel.setText(currentUser.getRole());
            
            // Изчистване на полетата за парола
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            
            // Попълване на статистиката
            int totalLoans = loanService.getLoanCountByUser(currentUser.getUserId());
            int activeLoans = loanService.getActiveLoansCountByUser(currentUser.getUserId());
            
            loanCountValueLabel.setText(String.valueOf(totalLoans));
            activeLoansValueLabel.setText(String.valueOf(activeLoans));
        }
    }
    
    /**
     * Обновява профила на потребителя
     */
    private void updateProfile() {
        // Извличане на въведените данни
        String name = nameField.getText();
        String email = emailField.getText();
        
        // Валидация на входните данни
        if (!ValidationUtils.isNotEmpty(name)) {
            JOptionPane.showMessageDialog(this,
                    "Моля, въведете име!",
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
        
        // Проверка дали има промяна
        if (name.equals(currentUser.getName()) && email.equals(currentUser.getEmail())) {
            JOptionPane.showMessageDialog(this,
                    "Няма промени за запазване.",
                    "Информация",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Обновяване на профила
        boolean success = userService.updateUser(
                currentUser.getUserId(),
                name,
                email,
                currentUser.getRole());
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Профилът е обновен успешно!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Презареждане на данните
            refreshData();
            
            // Обновяване на заглавието на главния прозорец
            mainFrame.setTitle("Библиотечна система - " + name);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Грешка при обновяване на профила!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Променя паролата на потребителя
     */
    private void changePassword() {
        // Извличане на въведените данни
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(currentPassword, newPassword, confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Моля, попълнете всички полета!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtils.isStrongPassword(newPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Новата парола трябва да бъде поне 8 символа с цифри и букви!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!ValidationUtils.areEqual(newPassword, confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Паролите не съвпадат!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Промяна на паролата
        boolean success = authService.changePassword(currentPassword, newPassword, confirmPassword);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Паролата е променена успешно!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Изчистване на полетата за парола
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Грешка при промяна на паролата! Проверете текущата парола.",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
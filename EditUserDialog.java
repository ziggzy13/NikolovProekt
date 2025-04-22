package library.ui.main.dialogs;

import library.model.User;
import library.service.UserService;
import library.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Диалог за редактиране на потребител
 */
public class EditUserDialog extends JDialog {
    
    private UserService userService;
    private User user;
    
    // Компоненти за въвеждане на данни
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JButton changePasswordButton;
    private JButton saveButton;
    private JButton cancelButton;
    
    /**
     * Конструктор
     * @param parent родителският компонент
     * @param user потребителят, който ще се редактира
     * @param userService сервиз за потребители
     */
    public EditUserDialog(Window parent, User user, UserService userService) {
        super(parent, "Редактиране на потребител", ModalityType.APPLICATION_MODAL);
        this.user = user;
        this.userService = userService;
        
        // Настройки на диалога
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Инициализация на компонентите
        initComponents();
        
        // Разположение на компонентите
        layoutComponents();
        
        // Зареждане на данните
        loadUserData();
        
        // Добавяне на слушатели за събития
        addEventListeners();
    }
    
    /**
     * Инициализира компонентите на диалога
     */
    private void initComponents() {
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        roleComboBox = new JComboBox<>(new String[] {"потребител", "администратор"});
        changePasswordButton = new JButton("Промяна на паролата");
        saveButton = new JButton("Запази");
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
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Име:"));
        formPanel.add(nameField);
        formPanel.add(new
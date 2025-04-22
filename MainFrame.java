package library.ui.main;

import library.model.User;
import library.service.AuthenticationService;
import library.ui.LoginFrame;
import library.ui.main.panels.AdminPanel;
import library.ui.main.panels.BookListPanel;
import library.ui.main.panels.LoanHistoryPanel;
import library.ui.main.panels.UserProfilePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Главен прозорец на приложението след вход в системата
 */
public class MainFrame extends JFrame {
    
    private AuthenticationService authService;
    private User currentUser;
    
    // Панели за различните секции на приложението
    private JPanel contentPanel;
    private BookListPanel bookListPanel;
    private LoanHistoryPanel loanHistoryPanel;
    private UserProfilePanel userProfilePanel;
    private AdminPanel adminPanel;
    
    // Компоненти на навигацията
    private JButton booksButton;
    private JButton loansButton;
    private JButton profileButton;
    private JButton adminButton;
    private JButton logoutButton;
    
    // Компоненти на статус лентата
    private JLabel statusLabel;
    private JLabel userLabel;
    
    /**
     * Конструктор
     * @param authService сервиз за автентикация
     */
    public MainFrame(AuthenticationService authService) {
        this.authService = authService;
        this.currentUser = authService.getCurrentUser();
        
        // Проверка дали има влязъл потребител
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Няма влязъл потребител! Моля, влезте отново.",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }
        
        // Настройки на прозореца
        setTitle("Библиотечна система - " + currentUser.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null); // Центриране на екрана
        setMinimumSize(new Dimension(800, 500)); // Минимален размер
        
        // Инициализация на компонентите
        initComponents();
        
        // Разположение на компонентите
        layoutComponents();
        
        // Добавяне на слушатели за събития
        addEventListeners();
        
        // По подразбиране показваме списъка с книги
        showPanel("books");
    }
    
    /**
     * Инициализира компонентите на прозореца
     */
    private void initComponents() {
        // Инициализация на панелите
        contentPanel = new JPanel(new CardLayout());
        bookListPanel = new BookListPanel(this, authService);
        loanHistoryPanel = new LoanHistoryPanel(this, authService);
        userProfilePanel = new UserProfilePanel(this, authService);
        
        // Ако потребителят е администратор, създаваме и административен панел
        if (authService.isAdmin()) {
            adminPanel = new AdminPanel(this, authService);
        }
        
        // Инициализация на навигационните бутони
        booksButton = new JButton("Книги");
        booksButton.setIcon(new ImageIcon(getClass().getResource("/icons/book.png")));
        
        loansButton = new JButton("Заемания");
        loansButton.setIcon(new ImageIcon(getClass().getResource("/icons/loan.png")));
        
        profileButton = new JButton("Профил");
        profileButton.setIcon(new ImageIcon(getClass().getResource("/icons/user.png")));
        
        if (authService.isAdmin()) {
            adminButton = new JButton("Администрация");
            adminButton.setIcon(new ImageIcon(getClass().getResource("/icons/admin.png")));
        }
        
        logoutButton = new JButton("Изход");
        logoutButton.setIcon(new ImageIcon(getClass().getResource("/icons/logout.png")));
        
        // Инициализация на статус лентата
        statusLabel = new JLabel("Добре дошли в библиотечната система!");
        userLabel = new JLabel("Потребител: " + currentUser.getName() + 
                " (" + (authService.isAdmin() ? "Администратор" : "Потребител") + ")");
    }
    
    /**
     * Разполага компонентите в контейнера
     */
    private void layoutComponents() {
        // Разположение на прозореца с BorderLayout
        setLayout(new BorderLayout());
        
        // Добавяне на панелите към contentPanel
        contentPanel.add(bookListPanel, "books");
        contentPanel.add(loanHistoryPanel, "loans");
        contentPanel.add(userProfilePanel, "profile");
        
        if (authService.isAdmin()) {
            contentPanel.add(adminPanel, "admin");
        }
        
        // Създаване на навигационен панел
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Добавяне на бутоните към навигационния панел
        navPanel.add(booksButton);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(loansButton);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        navPanel.add(profileButton);
        
        if (authService.isAdmin()) {
            navPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            navPanel.add(adminButton);
        }
        
        navPanel.add(Box.createVerticalGlue());
        navPanel.add(logoutButton);
        
        // Създаване на статус лента
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(userLabel, BorderLayout.EAST);
        
        // Добавяне на панелите към основния прозорец
        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за бутона "Книги"
        booksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("books");
            }
        });
        
        // Слушател за бутона "Заемания"
        loansButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("loans");
            }
        });
        
        // Слушател за бутона "Профил"
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel("profile");
            }
        });
        
        // Слушател за бутона "Администрация" (ако е видим)
        if (authService.isAdmin()) {
            adminButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showPanel("admin");
                }
            });
        }
        
        // Слушател за бутона "Изход"
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }
    
    /**
     * Показва определен панел от приложението
     * @param panelName име на панела
     */
    public void showPanel(String panelName) {
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        cl.show(contentPanel, panelName);
        
        // Промяна на статус лентата
        switch (panelName) {
            case "books":
                statusLabel.setText("Преглед на книги");
                break;
            case "loans":
                statusLabel.setText("История на заеманията");
                break;
            case "profile":
                statusLabel.setText("Потребителски профил");
                break;
            case "admin":
                statusLabel.setText("Административен панел");
                break;
        }
    }
    
    /**
     * Обновява данните на текущия панел
     */
    public void refreshCurrentPanel() {
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        String currentCard = getCurrentCardName();
        
        if (currentCard != null) {
            switch (currentCard) {
                case "books":
                    bookListPanel.refreshData();
                    break;
                case "loans":
                    loanHistoryPanel.refreshData();
                    break;
                case "profile":
                    userProfilePanel.refreshData();
                    break;
                case "admin":
                    if (adminPanel != null) {
                        adminPanel.refreshData();
                    }
                    break;
            }
        }
    }
    
    /**
     * Връща името на текущо показвания панел
     * @return име на панела или null, ако не може да бъде определен
     */
    private String getCurrentCardName() {
        CardLayout cl = (CardLayout)(contentPanel.getLayout());
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) {
                if (comp == bookListPanel) return "books";
                if (comp == loanHistoryPanel) return "loans";
                if (comp == userProfilePanel) return "profile";
                if (comp == adminPanel) return "admin";
            }
        }
        return null;
    }
    
    /**
     * Излизане от системата
     */
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Сигурни ли сте, че искате да излезете от системата?",
                "Потвърждение",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose(); // Затваряне на главния прозорец
            
            // Отваряне на прозореца за вход
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }
    
    /**
     * Показва съобщение в статус лентата
     * @param message съобщение за показване
     */
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
}
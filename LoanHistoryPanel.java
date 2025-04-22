package library.ui.main.panels;

import library.model.Loan;
import library.model.User;
import library.service.AuthenticationService;
import library.service.LoanService;
import library.ui.main.MainFrame;
import library.ui.main.dialogs.ReturnBookDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Панел за показване на историята на заеманията
 */
public class LoanHistoryPanel extends JPanel {
    
    private MainFrame mainFrame;
    private AuthenticationService authService;
    private LoanService loanService;
    
    // Компоненти за показване на заеманията
    private JTabbedPane tabbedPane;
    private JTable userLoansTable;
    private DefaultTableModel userLoansModel;
    private JTable adminLoansTable;
    private DefaultTableModel adminLoansModel;
    
    // Компоненти за управление
    private JButton returnBookButton;
    private JButton refreshButton;
    
    // Форматиране на дати
    private SimpleDateFormat dateFormat;
    
    /**
     * Конструктор
     * @param mainFrame главният прозорец на приложението
     * @param authService сервиз за автентикация
     */
    public LoanHistoryPanel(MainFrame mainFrame, AuthenticationService authService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.loanService = new LoanService();
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        
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
        // Създаване на tabbedPane
        tabbedPane = new JTabbedPane();
        
        // Инициализация на таблицата за потребителски заемания
        String[] userColumnsNames = {"ID", "Заглавие на книгата", "Автор", "Дата на заемане", "Дата на връщане", "Статус"};
        userLoansModel = new DefaultTableModel(userColumnsNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Правим таблицата нередактируема
            }
        };
        
        userLoansTable = new JTable(userLoansModel);
        userLoansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userLoansTable.setRowHeight(25);
        userLoansTable.getTableHeader().setReorderingAllowed(false);
        
        // Центриране на текста в колоните
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < userLoansTable.getColumnCount(); i++) {
            userLoansTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Скриване на колоната с ID
        userLoansTable.getColumnModel().getColumn(0).setMinWidth(0);
        userLoansTable.getColumnModel().getColumn(0).setMaxWidth(0);
        userLoansTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Инициализация на таблицата за администраторски преглед на заеманията (само за админи)
        if (authService.isAdmin()) {
            String[] adminColumnsNames = {"ID", "Заглавие на книгата", "Потребител", "Дата на заемане", "Дата на връщане", "Статус"};
            adminLoansModel = new DefaultTableModel(adminColumnsNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Правим таблицата нередактируема
                }
            };
            
            adminLoansTable = new JTable(adminLoansModel);
            adminLoansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            adminLoansTable.setRowHeight(25);
            adminLoansTable.getTableHeader().setReorderingAllowed(false);
            
            // Центриране на текста в колоните
            for (int i = 0; i < adminLoansTable.getColumnCount(); i++) {
                adminLoansTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            
            // Скриване на колоната с ID
            adminLoansTable.getColumnModel().getColumn(0).setMinWidth(0);
            adminLoansTable.getColumnModel().getColumn(0).setMaxWidth(0);
            adminLoansTable.getColumnModel().getColumn(0).setWidth(0);
        }
        
        // Инициализация на бутоните
        returnBookButton = new JButton("Върни книга");
        returnBookButton.setIcon(new ImageIcon(getClass().getResource("/icons/return_book.png")));
        
        refreshButton = new JButton("Обнови");
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png")));
    }
    
    /**
     * Разполага компонентите в панела
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Панел за потребителските заемания
        JPanel userLoansPanel = new JPanel(new BorderLayout());
        userLoansPanel.add(new JScrollPane(userLoansTable), BorderLayout.CENTER);
        
        JPanel userButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userButtonPanel.add(returnBookButton);
        userButtonPanel.add(refreshButton);
        userLoansPanel.add(userButtonPanel, BorderLayout.SOUTH);
        
        // Добавяне на табове
        tabbedPane.addTab("Моите заемания", userLoansPanel);
        
        // Панел за администраторски преглед (само за админи)
        if (authService.isAdmin()) {
            JPanel adminLoansPanel = new JPanel(new BorderLayout());
            adminLoansPanel.add(new JScrollPane(adminLoansTable), BorderLayout.CENTER);
            
            JPanel adminButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton adminReturnButton = new JButton("Върни избраната книга");
            adminReturnButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    returnBookAdmin();
                }
            });
            adminButtonPanel.add(adminReturnButton);
            adminLoansPanel.add(adminButtonPanel, BorderLayout.SOUTH);
            
            tabbedPane.addTab("Всички заемания", adminLoansPanel);
        }
        
        // Добавяне на tabbedPane към панела
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за бутона "Върни книга"
        returnBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });
        
        // Слушател за бутона "Обнови"
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
    }
    
    /**
     * Зарежда данните в таблиците
     */
    public void refreshData() {
        // Изчистване на таблиците
        userLoansModel.setRowCount(0);
        if (adminLoansModel != null) {
            adminLoansModel.setRowCount(0);
        }
        
        // Зареждане на заеманията на текущия потребител
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            List<Loan> userLoans = loanService.getLoansByUser(currentUser.getUserId());
            
            // Добавяне на заеманията в таблицата
            for (Loan loan : userLoans) {
                String returnDate = loan.getReturnDate() != null ? dateFormat.format(loan.getReturnDate()) : "Няма";
                String status = loan.isReturned() ? "Върната" : "Заета";
                
                userLoansModel.addRow(new Object[] {
                        loan.getLoanId(),
                        loan.getBook().getTitle(),
                        loan.getBook().getAuthor(),
                        dateFormat.format(loan.getLoanDate()),
                        returnDate,
                        status
                });
            }
            
            // Зареждане на всички заемания (само за админи)
            if (authService.isAdmin() && adminLoansModel != null) {
                List<Loan> allLoans = loanService.getAllLoans();
                
                // Добавяне на всички заемания в администраторската таблица
                for (Loan loan : allLoans) {
                    String returnDate = loan.getReturnDate() != null ? dateFormat.format(loan.getReturnDate()) : "Няма";
                    String status = loan.isReturned() ? "Върната" : "Заета";
                    
                    adminLoansModel.addRow(new Object[] {
                            loan.getLoanId(),
                            loan.getBook().getTitle(),
                            loan.getUser().getName(),
                            dateFormat.format(loan.getLoanDate()),
                            returnDate,
                            status
                    });
                }
            }
            
            // Обновяване на статус съобщението
            mainFrame.setStatusMessage("Заредени " + userLoans.size() + " заемания");
        }
    }
    
    /**
     * Връщане на книга от потребителя
     */
    private void returnBook() {
        int selectedRow = userLoansTable.getSelectedRow();
        if (selectedRow != -1) {
            // Извличане на ID-то на избраното заемане
            int loanId = (int) userLoansTable.getValueAt(selectedRow, 0);
            
            // Проверка дали книгата вече е върната
            String status = (String) userLoansTable.getValueAt(selectedRow, 5);
            if ("Върната".equals(status)) {
                JOptionPane.showMessageDialog(this,
                        "Тази книга вече е върната!",
                        "Информация",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Създаване и показване на диалог за връщане на книга
            ReturnBookDialog dialog = new ReturnBookDialog(mainFrame, loanId, loanService);
            dialog.setVisible(true);
            
            // Обновяване на данните след затваряне на диалога
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Моля, изберете заемане от списъка!",
                    "Няма избрано заемане",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Връщане на книга от администратор
     */
    private void returnBookAdmin() {
        if (!authService.isAdmin()) {
            return;
        }
        
        int selectedRow = adminLoansTable.getSelectedRow();
        if (selectedRow != -1) {
            // Извличане на ID-то на избраното заемане
            int loanId = (int) adminLoansTable.getValueAt(selectedRow, 0);
            
            // Проверка дали книгата вече е върната
            String status = (String) adminLoansTable.getValueAt(selectedRow, 5);
            if ("Върната".equals(status)) {
                JOptionPane.showMessageDialog(this,
                        "Тази книга вече е върната!",
                        "Информация",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Създаване и показване на диалог за връщане на книга
            ReturnBookDialog dialog = new ReturnBookDialog(mainFrame, loanId, loanService);
            dialog.setVisible(true);
            
            // Обновяване на данните след затваряне на диалога
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Моля, изберете заемане от списъка!",
                    "Няма избрано заемане",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
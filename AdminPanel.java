package library.ui.main.panels;

import library.model.Book;
import library.model.User;
import library.service.AuthenticationService;
import library.service.BookService;
import library.service.LoanService;
import library.service.UserService;
import library.ui.main.MainFrame;
import library.ui.main.dialogs.AddBookDialog;
import library.ui.main.dialogs.AddUserDialog;
import library.ui.main.dialogs.EditBookDialog;
import library.ui.main.dialogs.EditUserDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Панел за администраторски функции
 */
public class AdminPanel extends JPanel {
    
    private MainFrame mainFrame;
    private AuthenticationService authService;
    private UserService userService;
    private BookService bookService;
    private LoanService loanService;
    
    // Компоненти на интерфейса
    private JTabbedPane tabbedPane;
    
    // Компоненти за управление на потребители
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JButton addUserButton;
    private JButton editUserButton;
    private JButton deleteUserButton;
    
    // Компоненти за управление на книги
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JButton addBookButton;
    private JButton editBookButton;
    private JButton deleteBookButton;
    
    /**
     * Конструктор
     * @param mainFrame главният прозорец на приложението
     * @param authService сервиз за автентикация
     */
    public AdminPanel(MainFrame mainFrame, AuthenticationService authService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.userService = new UserService();
        this.bookService = new BookService();
        this.loanService = new LoanService();
        
        // Проверка дали текущият потребител е администратор
        if (!authService.isAdmin()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Нямате администраторски права!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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
        
        // Инициализация на компонентите за управление на потребители
        String[] userColumns = {"ID", "Име", "Имейл", "Роля"};
        usersTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Правим таблицата нередактируема
            }
        };
        
        usersTable = new JTable(usersTableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setRowHeight(25);
        usersTable.getTableHeader().setReorderingAllowed(false);
        
        // Центриране на текста в колоните
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Скриване на колоната с ID
        usersTable.getColumnModel().getColumn(0).setMinWidth(0);
        usersTable.getColumnModel().getColumn(0).setMaxWidth(0);
        usersTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Инициализация на бутоните за управление на потребители
        addUserButton = new JButton("Добави потребител");
        editUserButton = new JButton("Редактирай потребител");
        deleteUserButton = new JButton("Изтрий потребител");
        
        // Инициализация на компонентите за управление на книги
        String[] bookColumns = {"ID", "Заглавие", "Автор", "Жанр", "Наличност"};
        booksTableModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Правим таблицата нередактируема
            }
        };
        
        booksTable = new JTable(booksTableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setRowHeight(25);
        booksTable.getTableHeader().setReorderingAllowed(false);
        
        // Центриране на текста в колоните
        for (int i = 0; i < booksTable.getColumnCount(); i++) {
            booksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Скриване на колоната с ID
        booksTable.getColumnModel().getColumn(0).setMinWidth(0);
        booksTable.getColumnModel().getColumn(0).setMaxWidth(0);
        booksTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Инициализация на бутоните за управление на книги
        addBookButton = new JButton("Добави книга");
        editBookButton = new JButton("Редактирай книга");
        deleteBookButton = new JButton("Изтрий книга");
    }
    
    /**
     * Разполага компонентите в панела
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Панел за управление на потребители
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        
        JPanel userButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userButtonsPanel.add(addUserButton);
        userButtonsPanel.add(editUserButton);
        userButtonsPanel.add(deleteUserButton);
        usersPanel.add(userButtonsPanel, BorderLayout.SOUTH);
        
        // Панел за управление на книги
        JPanel booksPanel = new JPanel(new BorderLayout());
        booksPanel.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        
        JPanel bookButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bookButtonsPanel.add(addBookButton);
        bookButtonsPanel.add(editBookButton);
        bookButtonsPanel.add(deleteBookButton);
        booksPanel.add(bookButtonsPanel, BorderLayout.SOUTH);
        
        // Добавяне на панелите към tabbedPane
        tabbedPane.addTab("Потребители", usersPanel);
        tabbedPane.addTab("Книги", booksPanel);
        
        // Добавяне на tabbedPane към панела
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушатели за управление на потребители
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        
        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });
        
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        
        // Слушатели за управление на книги
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });
        
        editBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editBook();
            }
        });
        
        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });
    }
    
    /**
     * Зарежда данните в таблиците
     */
    public void refreshData() {
        // Изчистване на таблиците
        usersTableModel.setRowCount(0);
        booksTableModel.setRowCount(0);
        
        // Зареждане на всички потребители
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            usersTableModel.addRow(new Object[] {
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole()
            });
        }
        
        // Зареждане на всички книги
        List<Book> books = bookService.getAllBooks();
        for (Book book : books) {
            booksTableModel.addRow(new Object[] {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getAvailability()
            });
        }
        
        // Обновяване на статус съобщението
        mainFrame.setStatusMessage("Заредени " + users.size() + " потребители и " + books.size() + " книги");
    }
    
    /**
     * Добавя нов потребител
     */
    private void addUser() {
        AddUserDialog dialog = new AddUserDialog(mainFrame, userService);
        dialog.setVisible(true);
        
        // Обновяване на данните след затваряне на диалога
        refreshData();
    }
    
    /**
     * Редактира избран потребител
     */
    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow != -1) {
            // Извличане на ID-то на избрания потребител
            int userId = (int) usersTable.getValueAt(selectedRow, 0);
            
            // Зареждане на потребителя от базата данни
            User user = userService.getUserById(userId);
            
            if (user != null) {
                // Проверка дали опитваме да редактираме себе си
                if (user.getUserId() == authService.getCurrentUser().getUserId()) {
                    JOptionPane.showMessageDialog(this,
                            "Не можете да редактирате собствения си профил от административния панел!",
                            "Предупреждение",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Създаване и показване на диалог за редактиране
                EditUserDialog dialog = new EditUserDialog(mainFrame, user, userService);
                dialog.setVisible(true);
                
                // Обновяване на данните след затваряне на диалога
                refreshData();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Моля, изберете потребител от списъка!",
                    "Няма избран потребител",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Изтрива избран потребител
     */
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow != -1) {
            // Извличане на ID-то и името на избрания потребител
            int userId = (int) usersTable.getValueAt(selectedRow, 0);
            String userName = (String) usersTable.getValueAt(selectedRow, 1);
            
            // Проверка дали опитваме да изтрием себе си
            if (userId == authService.getCurrentUser().getUserId()) {
                JOptionPane.showMessageDialog(this,
                        "Не можете да изтриете собствения си акаунт!",
                        "Грешка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Потвърждение от потребителя
            int choice = JOptionPane.showConfirmDialog(this,
                    "Сигурни ли сте, че искате да изтриете потребителя '" + userName + "'?",
                    "Потвърждение",
                    JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                // Изтриване на потребителя
                boolean success = userService.deleteUser(userId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Потребителят е изтрит успешно!",
                            "Успех",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Обновяване на данните
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Грешка при изтриване на потребителя! Възможно е потребителят да има активни заемания.",
                            "Грешка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Моля, изберете потребител от списъка!",
                    "Няма избран потребител",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Добавя нова книга
     */
    private void addBook() {
        AddBookDialog dialog = new AddBookDialog(mainFrame, bookService);
        dialog.setVisible(true);
        
        // Обновяване на данните след затваряне на диалога
        refreshData();
    }
    
    /**
     * Редактира избрана книга
     */
    private void editBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            // Извличане на ID-то на избраната книга
            int bookId = (int) booksTable.getValueAt(selectedRow, 0);
            
            // Зареждане на книгата от базата данни
            Book book = bookService.getBookById(bookId);
            
            if (book != null) {
                // Създаване и показване на диалог за редактиране
                EditBookDialog dialog = new EditBookDialog(mainFrame, book, bookService);
                dialog.setVisible(true);
                
                // Обновяване на данните след затваряне на диалога
                refreshData();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Моля, изберете книга от списъка!",
                    "Няма избрана книга",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Изтрива избрана книга
     */
    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            // Извличане на ID-то и заглавието на избраната книга
            int bookId = (int) booksTable.getValueAt(selectedRow, 0);
            String bookTitle = (String) booksTable.getValueAt(selectedRow, 1);
            
            // Проверка дали книгата е заета
            if (loanService.isBookLoaned(bookId)) {
                JOptionPane.showMessageDialog(this,
                        "Не можете да изтриете книга, която е заета!",
                        "Грешка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Потвърждение от потребителя
            int choice = JOptionPane.showConfirmDialog(this,
                    "Сигурни ли сте, че искате да изтриете книгата '" + bookTitle + "'?",
                    "Потвърждение",
                    JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                // Изтриване на книгата
                boolean success = bookService.deleteBook(bookId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Книгата е изтрита успешно!",
                            "Успех",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Обновяване на данните
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Грешка при изтриване на книгата!",
                            "Грешка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Моля, изберете книга от списъка!",
                    "Няма избрана книга",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
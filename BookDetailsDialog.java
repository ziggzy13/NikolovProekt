package library.ui.main.dialogs;

import library.model.Book;
import library.service.AuthenticationService;
import library.service.BookService;
import library.service.LoanService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Диалог за показване на детайли за книга
 */
public class BookDetailsDialog extends JDialog {
    
    private Book book;
    private AuthenticationService authService;
    private BookService bookService;
    private LoanService loanService;
    
    // Компоненти за показване на информация
    private JLabel idLabel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel genreLabel;
    private JLabel availabilityLabel;
    
    // Компоненти за действия
    private JButton borrowButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton closeButton;
    
    /**
     * Конструктор
     * @param parent родителският компонент
     * @param book книгата, чиито детайли ще се показват
     * @param authService сервиз за автентикация
     * @param bookService сервиз за книги
     * @param loanService сервиз за заемания
     */
    public BookDetailsDialog(Window parent, Book book, AuthenticationService authService, BookService bookService, LoanService loanService) {
        super(parent, "Детайли за книга", ModalityType.APPLICATION_MODAL);
        this.book = book;
        this.authService = authService;
        this.bookService = bookService;
        this.loanService = loanService;
        
        // Настройки на диалога
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Инициализация на компонентите
        initComponents();
        
        // Разположение на компонентите
        layoutComponents();
        
        // Зареждане на данните
        loadBookData();
        
        // Добавяне на слушатели за събития
        addEventListeners();
    }
    
    /**
     * Инициализира компонентите на диалога
     */
    private void initComponents() {
        idLabel = new JLabel();
        titleLabel = new JLabel();
        authorLabel = new JLabel();
        genreLabel = new JLabel();
        availabilityLabel = new JLabel();
        
        borrowButton = new JButton("Заеми книгата");
        editButton = new JButton("Редактирай");
        deleteButton = new JButton("Изтрий");
        closeButton = new JButton("Затвори");
        
        // Бутоните за редактиране и изтриване са видими само за администратори
        editButton.setVisible(authService.isAdmin());
        deleteButton.setVisible(authService.isAdmin());
    }
    
    /**
     * Разполага компонентите в диалога
     */
    private void layoutComponents() {
        // Основен панел с подредба тип BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Заглавие
        JLabel titleHeaderLabel = new JLabel("Детайли за книга", JLabel.CENTER);
        titleHeaderLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        mainPanel.add(titleHeaderLabel, BorderLayout.NORTH);
        
        // Панел за информация
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(idLabel);
        
        infoPanel.add(new JLabel("Заглавие:"));
        infoPanel.add(titleLabel);
        
        infoPanel.add(new JLabel("Автор:"));
        infoPanel.add(authorLabel);
        
        infoPanel.add(new JLabel("Жанр:"));
        infoPanel.add(genreLabel);
        
        infoPanel.add(new JLabel("Наличност:"));
        infoPanel.add(availabilityLabel);
        
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(borrowButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Добавяне на основния панел към диалога
        getContentPane().add(mainPanel);
    }
    
    /**
     * Зарежда данните на книгата в компонентите
     */
    private void loadBookData() {
        if (book != null) {
            idLabel.setText(String.valueOf(book.getBookId()));
            titleLabel.setText(book.getTitle());
            authorLabel.setText(book.getAuthor());
            genreLabel.setText(book.getGenre());
            availabilityLabel.setText(book.getAvailability());
            
            // Деактивиране на бутона за заемане, ако книгата не е налична
            borrowButton.setEnabled("налична".equals(book.getAvailability()));
        }
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за бутона "Заеми книгата"
        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrowBook();
            }
        });
        
        // Слушател за бутона "Редактирай"
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editBook();
            }
        });
        
        // Слушател за бутона "Изтрий"
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });
        
        // Слушател за бутона "Затвори"
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    /**
     * Заема книга
     */
    private void borrowBook() {
        if (!"налична".equals(book.getAvailability())) {
            JOptionPane.showMessageDialog(this,
                    "Тази книга не е налична за заемане!",
                    "Информация",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int userId = authService.getCurrentUser().getUserId();
        
        // Проверка дали потребителят има активни заемания
        int activeLoans = loanService.getActiveLoansCountByUser(userId);
        if (activeLoans >= 5) {
            JOptionPane.showMessageDialog(this,
                    "Не можете да заемете повече от 5 книги едновременно!",
                    "Предупреждение",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Потвърждение от потребителя
        int choice = JOptionPane.showConfirmDialog(this,
                "Искате ли да заемете книгата \"" + book.getTitle() + "\"?",
                "Потвърждение",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Заемане на книгата
            int loanId = loanService.borrowBook(book.getBookId(), userId);
            
            if (loanId > 0) {
                JOptionPane.showMessageDialog(this,
                        "Книгата е заета успешно!",
                        "Успех",
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Обновяване на данните
                book = bookService.getBookById(book.getBookId());
                loadBookData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Грешка при заемане на книгата!",
                        "Грешка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Редактира книга
     */
    private void editBook() {
        if (!authService.isAdmin()) {
            JOptionPane.showMessageDialog(this,
                    "Нямате права за редактиране на книги!",
                    "Отказан достъп",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Създаване и показване на диалог за редактиране
        EditBookDialog dialog = new EditBookDialog(this, book, bookService);
        dialog.setVisible(true);
        
        // Обновяване на данните след затваряне на диалога
        book = bookService.getBookById(book.getBookId());
        loadBookData();
    }
    
    /**
     * Изтрива книга
     */
    private void deleteBook() {
        if (!authService.isAdmin()) {
            JOptionPane.showMessageDialog(this,
                    "Нямате права за изтриване на книги!",
                    "Отказан достъп",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Проверка дали книгата е заета
        if (loanService.isBookLoaned(book.getBookId())) {
            JOptionPane.showMessageDialog(this,
                    "Не можете да изтриете книга, която е заета!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Потвърждение от потребителя
        int choice = JOptionPane.showConfirmDialog(this,
                "Сигурни ли сте, че искате да изтриете книгата \"" + book.getTitle() + "\"?",
                "Потвърждение",
                JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Изтриване на книгата
            boolean success = bookService.deleteBook(book.getBookId());
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Книгата е изтрита успешно!",
                        "Успех",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Грешка при изтриване на книгата!",
                        "Грешка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
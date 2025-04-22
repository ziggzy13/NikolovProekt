package library.ui.main.panels;

import library.model.Book;
import library.service.AuthenticationService;
import library.service.BookService;
import library.service.LoanService;
import library.ui.main.MainFrame;
import library.ui.main.dialogs.AddBookDialog;
import library.ui.main.dialogs.BookDetailsDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Панел за показване на списък с книги
 */
public class BookListPanel extends JPanel {
    
    private MainFrame mainFrame;
    private AuthenticationService authService;
    private BookService bookService;
    private LoanService loanService;
    
    // Компоненти за търсене и филтриране
    private JTextField searchField;
    private JComboBox<String> genreComboBox;
    private JCheckBox availableOnlyCheckBox;
    private JButton clearFilterButton;
    
    // Компоненти за списъка с книги
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    
    // Компоненти за управление на книги
    private JButton addBookButton;
    private JButton refreshButton;
    private JButton detailsButton;
    
    /**
     * Конструктор
     * @param mainFrame главният прозорец на приложението
     * @param authService сервиз за автентикация
     */
    public BookListPanel(MainFrame mainFrame, AuthenticationService authService) {
        this.mainFrame = mainFrame;
        this.authService = authService;
        this.bookService = new BookService();
        this.loanService = new LoanService();
        
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
        // Компоненти за търсене и филтриране
        searchField = new JTextField(20);
        searchField.setToolTipText("Търсене по заглавие или автор");
        
        genreComboBox = new JComboBox<>(new String[] {"Всички жанрове", "Класика", "Фентъзи", "Научна фантастика", "Романтика", "Приключенска", "Сатира", "Детска литература"});
        
        availableOnlyCheckBox = new JCheckBox("Само налични");
        
        clearFilterButton = new JButton("Изчисти филтрите");
        
        // Компоненти за списъка с книги
        String[] columnNames = {"ID", "Заглавие", "Автор", "Жанр", "Наличност"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Правим таблицата нередактируема
            }
        };
        
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setRowHeight(25);
        booksTable.getTableHeader().setReorderingAllowed(false);
        
        // Центриране на текста в колоните
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < booksTable.getColumnCount(); i++) {
            booksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Скриване на колоната с ID
        booksTable.getColumnModel().getColumn(0).setMinWidth(0);
        booksTable.getColumnModel().getColumn(0).setMaxWidth(0);
        booksTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Настройка на сортирането
        sorter = new TableRowSorter<>(tableModel);
        booksTable.setRowSorter(sorter);
        
        // Компоненти за управление на книги
        addBookButton = new JButton("Добави книга");
        addBookButton.setIcon(new ImageIcon(getClass().getResource("/icons/add_book.png")));
        
        refreshButton = new JButton("Обнови");
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png")));
        
        detailsButton = new JButton("Детайли");
        detailsButton.setIcon(new ImageIcon(getClass().getResource("/icons/details.png")));
        
        // Показваме бутона за добавяне само за администратори
        addBookButton.setVisible(authService.isAdmin());
    }
    
    /**
     * Разполага компонентите в панела
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Панел за търсене и филтриране
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Търсене:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Жанр:"));
        searchPanel.add(genreComboBox);
        searchPanel.add(availableOnlyCheckBox);
        searchPanel.add(clearFilterButton);
        
        // Панел за таблицата
        JScrollPane scrollPane = new JScrollPane(booksTable);
        
        // Панел за бутоните
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(detailsButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addBookButton);
        
        // Добавяне на панелите към основния панел
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за полето за търсене
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });
        
        // Слушател за combo box-а с жанрове
        genreComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterTable();
            }
        });
        
        // Слушател за чекбокса "Само налични"
        availableOnlyCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterTable();
            }
        });
        
        // Слушател за бутона "Изчисти филтрите"
        clearFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                genreComboBox.setSelectedIndex(0);
                availableOnlyCheckBox.setSelected(false);
                filterTable();
            }
        });
        
        // Слушател за двойно кликване върху ред в таблицата
        booksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showBookDetails();
                }
            }
        });
        
        // Слушател за бутона "Добави книга"
        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewBook();
            }
        });
        
        // Слушател за бутона "Обнови"
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        
        // Слушател за бутона "Детайли"
        detailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBookDetails();
            }
        });
    }
    
    /**
     * Зарежда данните в таблицата
     */
    public void refreshData() {
        // Изчистване на таблицата
        tableModel.setRowCount(0);
        
        // Зареждане на всички книги
        List<Book> books = bookService.getAllBooks();
        
        // Добавяне на книгите в таблицата
        for (Book book : books) {
            tableModel.addRow(new Object[] {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getAvailability()
            });
        }
        
        // Прилагане на филтрите
        filterTable();
        
        // Обновяване на статус съобщението
        mainFrame.setStatusMessage("Заредени " + books.size() + " книги");
    }
    
    /**
     * Филтрира таблицата според зададените критерии
     */
    private void filterTable() {
        RowFilter<DefaultTableModel, Object> rf = null;
        
        // Комбиниране на различни филтри
        List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
        
        // Филтър по текст за търсене
        String searchText = searchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            RowFilter<DefaultTableModel, Object> searchFilter = RowFilter.regexFilter("(?i)" + searchText, 1, 2); // Търсене в колоните "Заглавие" и "Автор"
            filters.add(searchFilter);
        }
        
        // Филтър по жанр
        String selectedGenre = (String) genreComboBox.getSelectedItem();
        if (!"Всички жанрове".equals(selectedGenre)) {
            RowFilter<DefaultTableModel, Object> genreFilter = RowFilter.regexFilter("(?i)" + selectedGenre, 3); // Търсене в колоната "Жанр"
            filters.add(genreFilter);
        }
        
        // Филтър за наличност
        if (availableOnlyCheckBox.isSelected()) {
            RowFilter<DefaultTableModel, Object> availabilityFilter = RowFilter.regexFilter("налична", 4); // Търсене в колоната "Наличност"
            filters.add(availabilityFilter);
        }
        
        // Прилагане на комбинирания филтър
        if (!filters.isEmpty()) {
            rf = RowFilter.andFilter(filters);
            sorter.setRowFilter(rf);
        } else {
            sorter.setRowFilter(null);
        }
    }
    
    /**
     * Показва диалог с детайли за избраната книга
     */
    private void showBookDetails() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            // Преобразуване на индекса от view в model
            int modelRow = booksTable.convertRowIndexToModel(selectedRow);
            
            // Извличане на ID-то на избраната книга
            int bookId = (int) tableModel.getValueAt(modelRow, 0);
            
            // Зареждане на книгата от базата данни
            Book book = bookService.getBookById(bookId);
            
            if (book != null) {
                // Създаване и показване на диалог с детайли
                BookDetailsDialog dialog = new BookDetailsDialog(mainFrame, book, authService, bookService, loanService);
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
     * Показва диалог за добавяне на нова книга
     */
    private void addNewBook() {
        if (!authService.isAdmin()) {
            JOptionPane.showMessageDialog(this,
                    "Нямате права за добавяне на книги!",
                    "Отказан достъп",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Създаване и показване на диалог за добавяне на книга
        AddBookDialog dialog = new AddBookDialog(mainFrame, bookService);
        dialog.setVisible(true);
        
        // Обновяване на данните след затваряне на диалога
        refreshData();
    }
}
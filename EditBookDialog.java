package library.ui.main.dialogs;

import library.model.Book;
import library.service.BookService;
import library.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Диалог за редактиране на книга
 */
public class EditBookDialog extends JDialog {
    
    private BookService bookService;
    private Book book;
    
    // Компоненти за въвеждане на данни
    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<String> genreComboBox;
    private JComboBox<String> availabilityComboBox;
    private JButton saveButton;
    private JButton cancelButton;
    
    /**
     * Конструктор
     * @param parent родителският компонент
     * @param book книгата, която ще се редактира
     * @param bookService сервиз за книги
     */
    public EditBookDialog(Window parent, Book book, BookService bookService) {
        super(parent, "Редактиране на книга", ModalityType.APPLICATION_MODAL);
        this.book = book;
        this.bookService = bookService;
        
        // Настройки на диалога
        setSize(400, 300);
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
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        genreComboBox = new JComboBox<>(new String[] {"Класика", "Фентъзи", "Научна фантастика", "Романтика", "Приключенска", "Сатира", "Детска литература"});
        availabilityComboBox = new JComboBox<>(new String[] {"налична", "заета", "върната"});
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
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Заглавие:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Автор:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Жанр:"));
        formPanel.add(genreComboBox);
        formPanel.add(new JLabel("Наличност:"));
        formPanel.add(availabilityComboBox);
        mainPanel.add(formPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);
        
        // Добавяне на основния панел към диалога
        getContentPane().add(mainPanel);
    }
    
    /**
     * Зарежда данните на книгата в компонентите
     */
    private void loadBookData() {
        if (book != null) {
            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            
            // Избиране на жанра от списъка
            for (int i = 0; i < genreComboBox.getItemCount(); i++) {
                if (genreComboBox.getItemAt(i).equals(book.getGenre())) {
                    genreComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            // Избиране на наличността от списъка
            for (int i = 0; i < availabilityComboBox.getItemCount(); i++) {
                if (availabilityComboBox.getItemAt(i).equals(book.getAvailability())) {
                    availabilityComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за бутона "Запази"
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBook();
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
     * Запазва промените по книгата
     */
    private void saveBook() {
        // Извличане на въведените данни
        String title = titleField.getText();
        String author = authorField.getText();
        String genre = (String) genreComboBox.getSelectedItem();
        String availability = (String) availabilityComboBox.getSelectedItem();
        
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(title, author)) {
            JOptionPane.showMessageDialog(this,
                    "Моля, попълнете всички полета!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Обновяване на книгата
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setAvailability(availability);
        
        boolean success = bookService.updateBook(book.getBookId(), title, author, genre);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Книгата е обновена успешно!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Грешка при обновяване на книгата!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
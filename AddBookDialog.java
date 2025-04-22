package library.ui.main.dialogs;

import library.service.BookService;
import library.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Диалог за добавяне на нова книга
 */
public class AddBookDialog extends JDialog {
    
    private BookService bookService;
    
    // Компоненти за въвеждане на данни
    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<String> genreComboBox;
    private JButton addButton;
    private JButton cancelButton;
    
    /**
     * Конструктор
     * @param parent родителският компонент
     * @param bookService сервиз за книги
     */
    public AddBookDialog(Window parent, BookService bookService) {
        super(parent, "Добавяне на нова книга", ModalityType.APPLICATION_MODAL);
        this.bookService = bookService;
        
        // Настройки на диалога
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // Инициализация на компонентите
        initComponents();
        
        // Разположение на компонентите
        layoutComponents();
        
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
        addButton = new JButton("Добави");
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
        formPanel.add(new JLabel("Заглавие:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Автор:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Жанр:"));
        formPanel.add(genreComboBox);
        mainPanel.add(formPanel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Панел за бутоните
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);
        
        // Добавяне на основния панел към диалога
        getContentPane().add(mainPanel);
    }
    
    /**
     * Добавя слушатели за събития към компонентите
     */
    private void addEventListeners() {
        // Слушател за бутона "Добави"
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
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
     * Добавя нова книга
     */
    private void addBook() {
        // Извличане на въведените данни
        String title = titleField.getText();
        String author = authorField.getText();
        String genre = (String) genreComboBox.getSelectedItem();
        
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(title, author)) {
            JOptionPane.showMessageDialog(this,
                    "Моля, попълнете всички полета!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Добавяне на книгата
        int bookId = bookService.addBook(title, author, genre);
        
        if (bookId > 0) {
            JOptionPane.showMessageDialog(this,
                    "Книгата е добавена успешно!",
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Грешка при добавяне на книгата!",
                    "Грешка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
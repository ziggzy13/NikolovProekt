package library.service;

import library.dao.BookDAO;
import library.dao.LoanDAO;
import library.model.Book;
import library.utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервизен клас за управление на книги
 */
public class BookService {
    
    private BookDAO bookDAO;
    private LoanDAO loanDAO;
    
    /**
     * Конструктор
     */
    public BookService() {
        this.bookDAO = new BookDAO();
        this.loanDAO = new LoanDAO();
    }
    
    /**
     * Добавя нова книга в системата
     * @param title заглавие на книгата
     * @param author автор на книгата
     * @param genre жанр на книгата
     * @return ID на новата книга или -1 при неуспех
     */
    public int addBook(String title, String author, String genre) {
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(title, author, genre)) {
            return -1;
        }
        
        // Създаване на нова книга
        Book book = new Book(title, author, genre);
        
        // Запазване на книгата в базата данни
        return bookDAO.addBook(book);
    }
    
    /**
     * Обновява информация за книга
     * @param bookId ID на книгата
     * @param title ново заглавие
     * @param author нов автор
     * @param genre нов жанр
     * @return true при успех, false при неуспех
     */
    public boolean updateBook(int bookId, String title, String author, String genre) {
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(title, author, genre)) {
            return false;
        }
        
        // Първо вземаме текущата книга
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            return false;
        }
        
        // Актуализираме информацията
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        
        // Записваме промените в базата данни
        return bookDAO.updateBook(book);
    }
    
    /**
     * Изтрива книга от системата
     * @param bookId ID на книгата
     * @return true при успех, false при неуспех
     */
    public boolean deleteBook(int bookId) {
        // Проверяваме дали книгата е заета
        if (loanDAO.isBookLoaned(bookId)) {
            return false;
        }
        
        // Изтриваме книгата от базата данни
        return bookDAO.deleteBook(bookId);
    }
    
    /**
     * Връща книга по ID
     * @param bookId ID на книгата
     * @return книгата или null, ако не е намерена
     */
    public Book getBookById(int bookId) {
        return bookDAO.getBookById(bookId);
    }
    
    /**
     * Връща списък с всички книги
     * @return списък с всички книги
     */
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }
    
    /**
     * Търси книги по заглавие
     * @param title част от заглавието за търсене
     * @return списък с намерените книги
     */
    public List<Book> searchBooksByTitle(String title) {
        if (!ValidationUtils.isNotEmpty(title)) {
            return new ArrayList<>();
        }
        
        return bookDAO.searchBooksByTitle(title);
    }
    
    /**
     * Търси книги по автор
     * @param author част от името на автора за търсене
     * @return списък с намерените книги
     */
    public List<Book> searchBooksByAuthor(String author) {
        if (!ValidationUtils.isNotEmpty(author)) {
            return new ArrayList<>();
        }
        
        return bookDAO.searchBooksByAuthor(author);
    }
    
    /**
     * Търси книги по жанр
     * @param genre жанрът за търсене
     * @return списък с намерените книги
     */
    public List<Book> searchBooksByGenre(String genre) {
        if (!ValidationUtils.isNotEmpty(genre)) {
            return new ArrayList<>();
        }
        
        return bookDAO.searchBooksByGenre(genre);
    }
    
    /**
     * Комбинирано търсене на книги по различни критерии
     * @param query текст за търсене
     * @param searchTitle търсене в заглавия
     * @param searchAuthor търсене в автори
     * @param searchGenre търсене в жанрове
     * @param onlyAvailable търси само налични книги
     * @return списък с намерените книги
     */
    public List<Book> searchBooks(String query, boolean searchTitle, boolean searchAuthor, 
                                 boolean searchGenre, boolean onlyAvailable) {
        if (!ValidationUtils.isNotEmpty(query)) {
            if (onlyAvailable) {
                return getAvailableBooks();
            } else {
                return getAllBooks();
            }
        }
        
        List<Book> results = new ArrayList<>();
        
        if (searchTitle) {
            results.addAll(bookDAO.searchBooksByTitle(query));
        }
        
        if (searchAuthor) {
            results.addAll(bookDAO.searchBooksByAuthor(query));
        }
        
        if (searchGenre) {
            results.addAll(bookDAO.searchBooksByGenre(query));
        }
        
        // Премахваме дубликатите и филтрираме по наличност, ако е необходимо
        List<Book> uniqueResults = new ArrayList<>();
        for (Book book : results) {
            if (!uniqueResults.contains(book)) {
                if (!onlyAvailable || "налична".equals(book.getAvailability())) {
                    uniqueResults.add(book);
                }
            }
        }
        
        return uniqueResults;
    }
    
    /**
     * Връща списък само с наличните книги
     * @return списък с наличните книги
     */
    public List<Book> getAvailableBooks() {
        List<Book> allBooks = bookDAO.getAllBooks();
        List<Book> availableBooks = new ArrayList<>();
        
        for (Book book : allBooks) {
            if ("налична".equals(book.getAvailability())) {
                availableBooks.add(book);
            }
        }
        
        return availableBooks;
    }
}
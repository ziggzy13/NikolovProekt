package library.model;

/**
 * Клас, представящ книга в библиотечната система
 */
public class Book {
    private int bookId;
    private String title;
    private String author;
    private String genre;
    private String availability; // "налична", "заета", "върната"
    
    /**
     * Конструктор по подразбиране
     */
    public Book() {
    }
    
    /**
     * Конструктор с параметри
     */
    public Book(int bookId, String title, String author, String genre, String availability) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.availability = availability;
    }
    
    /**
     * Конструктор за нова книга (без ID)
     */
    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.availability = "налична";
    }
    
    // Getters и Setters
    public int getBookId() {
        return bookId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getAvailability() {
        return availability;
    }
    
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
    @Override
    public String toString() {
        return "Книга{" +
                "ID=" + bookId +
                ", Заглавие='" + title + '\'' +
                ", Автор='" + author + '\'' +
                ", Жанр='" + genre + '\'' +
                ", Статус='" + availability + '\'' +
                '}';
    }
}
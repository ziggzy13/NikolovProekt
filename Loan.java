package library.model;

import java.util.Date;

/**
 * Клас, представящ заемане на книга в библиотечната система
 */
public class Loan {
    private int loanId;
    private int bookId;
    private int userId;
    private Date loanDate;
    private Date returnDate;
    private boolean isReturned;
    
    // За по-добра функционалност, добавяме референции към обектите
    private Book book;
    private User user;
    
    /**
     * Конструктор по подразбиране
     */
    public Loan() {
    }
    
    /**
     * Конструктор с параметри
     */
    public Loan(int loanId, int bookId, int userId, Date loanDate, Date returnDate, boolean isReturned) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }
    
    /**
     * Конструктор за ново заемане (без ID)
     */
    public Loan(int bookId, int userId, Date loanDate, Date returnDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.isReturned = false;
    }
    
    // Getters и Setters
    public int getLoanId() {
        return loanId;
    }
    
    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }
    
    public int getBookId() {
        return bookId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public Date getLoanDate() {
        return loanDate;
    }
    
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }
    
    public Date getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
    
    public boolean isReturned() {
        return isReturned;
    }
    
    public void setReturned(boolean returned) {
        isReturned = returned;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "Заемане{" +
                "ID=" + loanId +
                ", Книга ID=" + bookId +
                ", Потребител ID=" + userId +
                ", Дата на заемане=" + loanDate +
                ", Дата на връщане=" + returnDate +
                ", Върната=" + isReturned +
                '}';
    }
}
package library.dao;

import library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO клас за операции с книги в базата данни
 */
public class BookDAO {
    
    /**
     * Добавя нова книга в базата данни
     * @param book книгата, която трябва да бъде добавена
     * @return ID на добавената книга или -1 при неуспех
     */
    public int addBook(Book book) {
        String sql = "INSERT INTO books (title, author, genre, availability) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getGenre());
            pstmt.setString(4, book.getAvailability());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Добавянето на книга не бе успешно, няма редове за добавяне");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Добавянето на книга не бе успешно, не е генериран ID");
            }
        } catch (SQLException e) {
            System.err.println("Грешка при добавяне на книга: " + e.getMessage());
            return -1;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /**
     * Обновява информация за книга в базата данни
     * @param book книгата с обновената информация
     * @return true при успех, false при неуспех
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, genre = ?, availability = ? WHERE book_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getGenre());
            pstmt.setString(4, book.getAvailability());
            pstmt.setInt(5, book.getBookId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Грешка при обновяване на книга: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Изтрива книга от базата данни
     * @param bookId ID на книгата, която трябва да бъде изтрита
     * @return true при успех, false при неуспех
     */
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Грешка при изтриване на книга: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Намира книга по ID
     * @param bookId ID на търсената книга
     * @return книгата или null, ако не е намерена
     */
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при търсене на книга по ID: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Търси книги по заглавие
     * @param title част от заглавието за търсене
     * @return списък от книги, отговарящи на критерия
     */
    public List<Book> searchBooksByTitle(String title) {
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + title + "%");
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Грешка при търсене на книги по заглавие: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return books;
    }
    
    /**
     * Търси книги по автор
     * @param author част от името на автора за търсене
     * @return списък от книги, отговарящи на критерия
     */
    public List<Book> searchBooksByAuthor(String author) {
        String sql = "SELECT * FROM books WHERE author LIKE ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + author + "%");
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Грешка при търсене на книги по автор: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return books;
    }
    
    /**
     * Търси книги по жанр
     * @param genre жанрът, който трябва да се търси
     * @return списък от книги, отговарящи на критерия
     */
    public List<Book> searchBooksByGenre(String genre) {
        String sql = "SELECT * FROM books WHERE genre LIKE ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + genre + "%");
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Грешка при търсене на книги по жанр: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return books;
    }
    
    /**
     * Връща списък с всички книги
     * @return списък с всички книги в базата данни
     */
    public List<Book> getAllBooks() {
        String sql = "SELECT * FROM books";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<Book> books = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Грешка при извличане на всички книги: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return books;
    }
    
    /**
     * Обновява статуса на наличност на книга
     * @param bookId ID на книгата
     * @param availability новият статус на наличност
     * @return true при успех, false при неуспех
     */
    public boolean updateBookAvailability(int bookId, String availability) {
        String sql = "UPDATE books SET availability = ? WHERE book_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, availability);
            pstmt.setInt(2, bookId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Грешка при обновяване на наличността на книга: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Извлича книга от ResultSet обект
     * @param rs ResultSet обект
     * @return извлечената книга
     * @throws SQLException при грешка в извличането
     */
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setGenre(rs.getString("genre"));
        book.setAvailability(rs.getString("availability"));
        return book;
    }
    
    /**
     * Затваря ресурсите за връзка с базата данни
     * @param conn Connection обект
     * @param pstmt PreparedStatement обект
     * @param rs ResultSet обект
     */
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Грешка при затваряне на ресурсите: " + e.getMessage());
        }
    }
}
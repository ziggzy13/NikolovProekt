package library.dao;

import library.model.Book;
import library.model.Loan;
import library.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DAO клас за операции със заемания в базата данни
 */
public class LoanDAO {
    
    private BookDAO bookDAO;
    private UserDAO userDAO;
    
    /**
     * Конструктор
     */
    public LoanDAO() {
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Добавя ново заемане в базата данни
     * @param loan заемането, което трябва да бъде добавено
     * @return ID на добавеното заемане или -1 при неуспех
     */
    public int addLoan(Loan loan) {
        String sql = "INSERT INTO loans (book_id, user_id, loan_date, return_date, is_returned) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // Проверяваме дали книгата е налична
            Book book = bookDAO.getBookById(loan.getBookId());
            if (book == null || !book.getAvailability().equals("налична")) {
                System.err.println("Книгата не е налична за заемане");
                return -1;
            }
            
            conn = DatabaseConnection.getConnection();
            
            // Започваме транзакция
            conn.setAutoCommit(false);
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getUserId());
            
            // Задаваме текущата дата и час ако не е посочена
            if (loan.getLoanDate() == null) {
                loan.setLoanDate(new Date());
            }
            pstmt.setTimestamp(3, new Timestamp(loan.getLoanDate().getTime()));
            
            // Задаваме дата на връщане, ако е посочена
            if (loan.getReturnDate() != null) {
                pstmt.setTimestamp(4, new Timestamp(loan.getReturnDate().getTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            
            pstmt.setBoolean(5, loan.isReturned());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                throw new SQLException("Добавянето на заемане не бе успешно, няма редове за добавяне");
            }
            
            // Получаваме генерирания ID
            rs = pstmt.getGeneratedKeys();
            int loanId = -1;
            if (rs.next()) {
                loanId = rs.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("Добавянето на заемане не бе успешно, не е генериран ID");
            }
            
            // Обновяваме статуса на книгата
            if (!bookDAO.updateBookAvailability(loan.getBookId(), "заета")) {
                conn.rollback();
                throw new SQLException("Грешка при обновяване на статуса на книгата");
            }
            
            // Завършваме транзакцията
            conn.commit();
            return loanId;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Грешка при rollback: " + ex.getMessage());
            }
            System.err.println("Грешка при добавяне на заемане: " + e.getMessage());
            return -1;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Грешка при възстановяване на autoCommit: " + e.getMessage());
            }
            closeResources(conn, pstmt, rs);
        }
    }
    
    /**
     * Обновява информация за заемане в базата данни
     * @param loan заемането с обновената информация
     * @return true при успех, false при неуспех
     */
    public boolean updateLoan(Loan loan) {
        String sql = "UPDATE loans SET book_id = ?, user_id = ?, loan_date = ?, return_date = ?, is_returned = ? WHERE loan_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getUserId());
            pstmt.setTimestamp(3, new Timestamp(loan.getLoanDate().getTime()));
            
            if (loan.getReturnDate() != null) {
                pstmt.setTimestamp(4, new Timestamp(loan.getReturnDate().getTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            
            pstmt.setBoolean(5, loan.isReturned());
            pstmt.setInt(6, loan.getLoanId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Грешка при обновяване на заемане: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Маркира заемане като върнато
     * @param loanId ID на заемането
     * @return true при успех, false при неуспех
     */
    public boolean returnBook(int loanId) {
        String sql = "UPDATE loans SET return_date = ?, is_returned = TRUE WHERE loan_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Първо получаваме информация за заемането
            Loan loan = getLoanById(loanId);
            if (loan == null || loan.isReturned()) {
                return false;
            }
            
            conn = DatabaseConnection.getConnection();
            
            // Започваме транзакция
            conn.setAutoCommit(false);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            pstmt.setInt(2, loanId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Обновяваме статуса на книгата
                if (!bookDAO.updateBookAvailability(loan.getBookId(), "налична")) {
                    conn.rollback();
                    return false;
                }
                
                conn.commit();
                return true;
            }
            
            conn.rollback();
            return false;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Грешка при rollback: " + ex.getMessage());
            }
            System.err.println("Грешка при връщане на книга: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Грешка при възстановяване на autoCommit: " + e.getMessage());
            }
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Изтрива заемане от базата данни
     * @param loanId ID на заемането, което трябва да бъде изтрито
     * @return true при успех, false при неуспех
     */
    public boolean deleteLoan(int loanId) {
        String sql = "DELETE FROM loans WHERE loan_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Първо получаваме информация за заемането
            Loan loan = getLoanById(loanId);
            if (loan == null) {
                return false;
            }
            
            conn = DatabaseConnection.getConnection();
            
            // Започваме транзакция
            conn.setAutoCommit(false);
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, loanId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Ако книгата е била заета и сега я изтриваме, трябва да променим статуса й обратно на "налична"
                if (!loan.isReturned()) {
                    if (!bookDAO.updateBookAvailability(loan.getBookId(), "налична")) {
                        conn.rollback();
                        return false;
                    }
                }
                
                conn.commit();
                return true;
            }
            
            conn.rollback();
            return false;
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Грешка при rollback: " + ex.getMessage());
            }
            System.err.println("Грешка при изтриване на заемане: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Грешка при възстановяване на autoCommit: " + e.getMessage());
            }
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Намира заемане по ID
     * @param loanId ID на търсеното заемане
     * @return заемането или null, ако не е намерено
     */
    public Loan getLoanById(int loanId) {
        String sql = "SELECT * FROM loans WHERE loan_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, loanId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Loan loan = extractLoanFromResultSet(rs);
                
                // Зареждаме книгата и потребителя
                loan.setBook(bookDAO.getBookById(loan.getBookId()));
                loan.setUser(userDAO.getUserById(loan.getUserId()));
                
                return loan;
            }
        } catch (SQLException e) {
            System.err.println("Грешка при търсене на заемане по ID: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Връща списък със заемания за даден потребител
     * @param userId ID на потребителя
     * @return списък със заемания на потребителя
     */
    public List<Loan> getLoansByUserId(int userId) {
        String sql = "SELECT * FROM loans WHERE user_id = ? ORDER BY loan_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<Loan> loans = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Loan loan = extractLoanFromResultSet(rs);
                loan.setBook(bookDAO.getBookById(loan.getBookId()));
                loan.setUser(userDAO.getUserById(loan.getUserId()));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при извличане на заемания за потребител: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return loans;
    }
    
    /**
     * Връща списък с активни (невърнати) заемания
     * @return списък с активни заемания
     */
    public List<Loan> getActiveLoans() {
        String sql = "SELECT * FROM loans WHERE is_returned = FALSE ORDER BY loan_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<Loan> loans = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Loan loan = extractLoanFromResultSet(rs);
                loan.setBook(bookDAO.getBookById(loan.getBookId()));
                loan.setUser(userDAO.getUserById(loan.getUserId()));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при извличане на активни заемания: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return loans;
    }
    
    /**
     * Връща списък с просрочени заемания
     * @param daysOverdue брой дни просрочие
     * @return списък с просрочени заемания
     */
    public List<Loan> getOverdueLoans(int daysOverdue) {
        String sql = "SELECT * FROM loans WHERE is_returned = FALSE AND loan_date < ? ORDER BY loan_date";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<Loan> loans = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            // Изчисляваме датата, преди която се считат за просрочени
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -daysOverdue);
            pstmt.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Loan loan = extractLoanFromResultSet(rs);
                loan.setBook(bookDAO.getBookById(loan.getBookId()));
                loan.setUser(userDAO.getUserById(loan.getUserId()));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при извличане на просрочени заемания: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return loans;
    }
    
    /**
     * Връща списък с всички заемания
     * @return списък с всички заемания в базата данни
     */
    public List<Loan> getAllLoans() {
        String sql = "SELECT * FROM loans ORDER BY loan_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<Loan> loans = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Loan loan = extractLoanFromResultSet(rs);
                loan.setBook(bookDAO.getBookById(loan.getBookId()));
                loan.setUser(userDAO.getUserById(loan.getUserId()));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при извличане на всички заемания: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return loans;
    }
    
    /**
     * Проверява дали книга е заета
     * @param bookId ID на книгата
     * @return true, ако книгата е заета, false в противен случай
     */
    public boolean isBookLoaned(int bookId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE book_id = ? AND is_returned = FALSE";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Грешка при проверка дали книга е заета: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    /**
     * Връща брой заемания на даден потребител
     * @param userId ID на потребителя
     * @return брой заемания
     */
    public int getLoanCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при броене на заемания на потребител: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return 0;
    }
    
    /**
     * Връща брой активни заемания на даден потребител
     * @param userId ID на потребителя
     * @return брой активни заемания
     */
    public int getActiveLoansCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE user_id = ? AND is_returned = FALSE";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при броене на активни заемания на потребител: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return 0;
    }
    
    /**
     * Автоматично генерира подходяща дата за връщане на базата на текущата дата (обикновено +14 дни)
     * @return препоръчителна дата на връщане
     */
    public Date generateReturnDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 14); // По подразбиране книгите се заемат за 14 дни
        return cal.getTime();
    }
    
    /**
     * Извлича заемане от ResultSet обект
     * @param rs ResultSet обект
     * @return извлеченото заемане
     * @throws SQLException при грешка в извличането
     */
    private Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setUserId(rs.getInt("user_id"));
        
        Timestamp loanDate = rs.getTimestamp("loan_date");
        if (loanDate != null) {
            loan.setLoanDate(new Date(loanDate.getTime()));
        }
        
        Timestamp returnDate = rs.getTimestamp("return_date");
        if (returnDate != null) {
            loan.setReturnDate(new Date(returnDate.getTime()));
        }
        
        loan.setReturned(rs.getBoolean("is_returned"));
        return loan;
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
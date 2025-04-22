package library.service;

import library.dao.BookDAO;
import library.dao.LoanDAO;
import library.dao.UserDAO;
import library.model.Book;
import library.model.Loan;
import library.model.User;

import java.util.Date;
import java.util.List;

/**
 * Сервизен клас за управление на заемания на книги
 */
public class LoanService {
    
    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    
    /**
     * Конструктор
     */
    public LoanService() {
        this.loanDAO = new LoanDAO();
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Заема книга на потребител
     * @param bookId ID на книгата
     * @param userId ID на потребителя
     * @return ID на заемането или -1 при неуспех
     */
    public int borrowBook(int bookId, int userId) {
        // Проверяваме дали книгата съществува и е налична
        Book book = bookDAO.getBookById(bookId);
        if (book == null || !"налична".equals(book.getAvailability())) {
            return -1;
        }
        
        // Проверяваме дали потребителят съществува
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return -1;
        }
        
        // Създаваме ново заемане
        Loan loan = new Loan();
        loan.setBookId(bookId);
        loan.setUserId(userId);
        loan.setLoanDate(new Date());
        
        // Определяме препоръчителна дата за връщане (14 дни от сега)
        loan.setReturnDate(loanDAO.generateReturnDate());
        
        loan.setReturned(false);
        
        // Записваме заемането в базата данни
        return loanDAO.addLoan(loan);
    }
    
    /**
     * Връща заета книга
     * @param loanId ID на заемането
     * @return true при успех, false при неуспех
     */
    public boolean returnBook(int loanId) {
        // Проверяваме дали заемането съществува
        Loan loan = loanDAO.getLoanById(loanId);
        if (loan == null || loan.isReturned()) {
            return false;
        }
        
        // Връщаме книгата
        return loanDAO.returnBook(loanId);
    }
    
    /**
     * Проверява дали книга е заета
     * @param bookId ID на книгата
     * @return true ако книгата е заета, false в противен случай
     */
    public boolean isBookLoaned(int bookId) {
        return loanDAO.isBookLoaned(bookId);
    }
    
    /**
     * Връща всички заемания за даден потребител
     * @param userId ID на потребителя
     * @return списък със заемания на потребителя
     */
    public List<Loan> getLoansByUser(int userId) {
        return loanDAO.getLoansByUserId(userId);
    }
    
    /**
     * Връща списък с всички активни (невърнати) заемания
     * @return списък с активни заемания
     */
    public List<Loan> getActiveLoans() {
        return loanDAO.getActiveLoans();
    }
    
    /**
     * Връща списък с всички заемания
     * @return списък с всички заемания
     */
    public List<Loan> getAllLoans() {
        return loanDAO.getAllLoans();
    }
    
    /**
     * Връща списък с просрочени заемания
     * @param daysOverdue брой дни просрочие
     * @return списък с просрочени заемания
     */
    public List<Loan> getOverdueLoans(int daysOverdue) {
        return loanDAO.getOverdueLoans(daysOverdue);
    }
    
    /**
     * Връща броя на текущите заемания на потребител
     * @param userId ID на потребителя
     * @return брой активни заемания
     */
    public int getActiveLoansCountByUser(int userId) {
        return loanDAO.getActiveLoansCountByUser(userId);
    }
    
    /**
     * Връща общия брой на заеманията на потребител
     * @param userId ID на потребителя
     * @return общ брой заемания
     */
    public int getLoanCountByUser(int userId) {
        return loanDAO.getLoanCountByUser(userId);
    }
    
    /**
     * Проверява дали потребител е длъжник (има просрочени заемания)
     * @param userId ID на потребителя
     * @param daysGrace гратисен период в дни
     * @return true ако потребителят има просрочени заемания, false в противен случай
     */
    public boolean isUserOverdue(int userId, int daysGrace) {
        List<Loan> userLoans = loanDAO.getLoansByUserId(userId);
        Date now = new Date();
        
        for (Loan loan : userLoans) {
            if (!loan.isReturned() && loan.getLoanDate() != null) {
                // Изчисляваме колко дни са минали от заемането
                long diffInMillies = now.getTime() - loan.getLoanDate().getTime();
                long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);
                
                // Ако са минали повече от разрешените дни, потребителят е просрочил
                if (diffInDays > daysGrace) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Генерира препоръчителна дата за връщане
     * @return дата за връщане (обикновено 14 дни от сега)
     */
    public Date generateReturnDate() {
        return loanDAO.generateReturnDate();
    }
    
    /**
     * Изтрива заемане от системата
     * @param loanId ID на заемането
     * @return true при успех, false при неуспех
     */
    public boolean deleteLoan(int loanId) {
        return loanDAO.deleteLoan(loanId);
    }
}
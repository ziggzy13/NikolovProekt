package library.service;

import library.dao.UserDAO;
import library.model.User;
import library.utils.ValidationUtils;

/**
 * Сервизен клас за автентикация и управление на потребителските сесии
 */
public class AuthenticationService {
    
    private UserDAO userDAO;
    private User currentUser;
    
    /**
     * Конструктор
     */
    public AuthenticationService() {
        this.userDAO = new UserDAO();
        this.currentUser = null;
    }
    
    /**
     * Извършва вход в системата
     * @param email потребителски имейл
     * @param password потребителска парола
     * @return true при успешен вход, false при неуспех
     */
    public boolean login(String email, String password) {
        if (!ValidationUtils.isValidEmail(email) || !ValidationUtils.isNotEmpty(password)) {
            return false;
        }
        
        User user = userDAO.login(email, password);
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        
        return false;
    }
    
    /**
     * Извършва изход от системата
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Проверява дали потребителят е влязъл в системата
     * @return true ако потребителят е влязъл, false в противен случай
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Проверява дали текущият потребител е администратор
     * @return true ако потребителят е администратор, false в противен случай
     */
    public boolean isAdmin() {
        if (!isLoggedIn()) {
            return false;
        }
        
        return "администратор".equals(currentUser.getRole());
    }
    
    /**
     * Връща текущия потребител
     * @return текущия потребител или null, ако няма влязъл потребител
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Регистрира нов потребител
     * @param name име на потребителя
     * @param email имейл на потребителя
     * @param password парола на потребителя
     * @param confirmPassword потвърждение на паролата
     * @return ID на новия потребител или -1 при неуспех
     */
    public int register(String name, String email, String password, String confirmPassword) {
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(name, email, password, confirmPassword)) {
            return -1;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            return -1;
        }
        
        if (!ValidationUtils.isStrongPassword(password)) {
            return -1;
        }
        
        if (!ValidationUtils.areEqual(password, confirmPassword)) {
            return -1;
        }
        
        // Проверка дали имейлът вече е регистриран
        if (userDAO.emailExists(email)) {
            return -1;
        }
        
        // Създаване на нов потребител
        User user = new User(name, email, password);
        
        // Ако е първият потребител в системата, го правим администратор
        if (userDAO.getAdminCount() == 0) {
            user.setRole("администратор");
        }
        
        // Запазване на потребителя в базата данни
        return userDAO.addUser(user);
    }
    
    /**
     * Променя паролата на текущия потребител
     * @param oldPassword текуща парола
     * @param newPassword нова парола
     * @param confirmPassword потвърждение на новата парола
     * @return true при успех, false при неуспех
     */
    public boolean changePassword(String oldPassword, String newPassword, String confirmPassword) {
        if (!isLoggedIn()) {
            return false;
        }
        
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(oldPassword, newPassword, confirmPassword)) {
            return false;
        }
        
        if (!ValidationUtils.isStrongPassword(newPassword)) {
            return false;
        }
        
        if (!ValidationUtils.areEqual(newPassword, confirmPassword)) {
            return false;
        }
        
        // Проверка на текущата парола
        User user = userDAO.login(currentUser.getEmail(), oldPassword);
        if (user == null) {
            return false;
        }
        
        // Променяне на паролата
        return userDAO.updatePassword(currentUser.getUserId(), newPassword);
    }
}
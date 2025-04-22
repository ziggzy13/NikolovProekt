package library.service;

import library.dao.UserDAO;
import library.model.User;
import library.utils.ValidationUtils;

import java.util.List;

/**
 * Сервизен клас за управление на потребители
 */
public class UserService {
    
    private UserDAO userDAO;
    
    /**
     * Конструктор
     */
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Създава нов потребител
     * @param name име на потребителя
     * @param email имейл на потребителя
     * @param password парола на потребителя
     * @param role роля на потребителя ("потребител" или "администратор")
     * @return ID на новия потребител или -1 при неуспех
     */
    public int createUser(String name, String email, String password, String role) {
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(name, email, password, role)) {
            return -1;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            return -1;
        }
        
        if (!ValidationUtils.isStrongPassword(password)) {
            return -1;
        }
        
        // Проверка дали имейлът вече е регистриран
        if (userDAO.emailExists(email)) {
            return -1;
        }
        
        // Валидация на ролята
        if (!"потребител".equals(role) && !"администратор".equals(role)) {
            role = "потребител"; // По подразбиране потребител
        }
        
        // Създаване на нов потребител
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        
        // Запазване на потребителя в базата данни
        return userDAO.addUser(user);
    }
    
    /**
     * Обновява информация за потребител
     * @param userId ID на потребителя
     * @param name ново име
     * @param email нов имейл
     * @param role нова роля
     * @return true при успех, false при неуспех
     */
    public boolean updateUser(int userId, String name, String email, String role) {
        // Валидация на входните данни
        if (!ValidationUtils.areNotEmpty(name, email, role)) {
            return false;
        }
        
        if (!ValidationUtils.isValidEmail(email)) {
            return false;
        }
        
        // Взимаме текущия потребител
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        // Проверка дали новият имейл вече се използва от друг потребител
        User existingUser = userDAO.getUserByEmail(email);
        if (existingUser != null && existingUser.getUserId() != userId) {
            return false;
        }
        
        // Валидация на ролята
        if (!"потребител".equals(role) && !"администратор".equals(role)) {
            role = "потребител"; // По подразбиране потребител
        }
        
        // Обновяване на потребителя
        user.setName(name);
        user.setEmail(email);
        user.setRole(role);
        
        // Записване на промените
        return userDAO.updateUser(user);
    }
    
    /**
     * Изтрива потребител от системата
     * @param userId ID на потребителя
     * @return true при успех, false при неуспех
     */
    public boolean deleteUser(int userId) {
        // Проверка дали потребителят съществува
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        // Проверка дали това е последният администратор
        if ("администратор".equals(user.getRole()) && userDAO.getAdminCount() <= 1) {
            return false; // Не можем да изтрием последния администратор
        }
        
        // Изтриване на потребителя
        return userDAO.deleteUser(userId);
    }
    
    /**
     * Променя паролата на потребител
     * @param userId ID на потребителя
     * @param newPassword нова парола
     * @return true при успех, false при неуспех
     */
    public boolean changeUserPassword(int userId, String newPassword) {
        // Валидация на входните данни
        if (!ValidationUtils.isNotEmpty(newPassword)) {
            return false;
        }
        
        if (!ValidationUtils.isStrongPassword(newPassword)) {
            return false;
        }
        
        // Промяна на паролата
        return userDAO.updatePassword(userId, newPassword);
    }
    
    /**
     * Връща потребител по ID
     * @param userId ID на потребителя
     * @return потребителят или null, ако не е намерен
     */
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }
    
    /**
     * Връща потребител по имейл
     * @param email имейл на потребителя
     * @return потребителят или null, ако не е намерен
     */
    public User getUserByEmail(String email) {
        if (!ValidationUtils.isValidEmail(email)) {
            return null;
        }
        
        return userDAO.getUserByEmail(email);
    }
    
    /**
     * Връща списък с всички потребители
     * @return списък с всички потребители
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    /**
     * Проверява дали имейл вече съществува в системата
     * @param email имейл за проверка
     * @return true ако имейлът вече съществува, false в противен случай
     */
    public boolean emailExists(String email) {
        if (!ValidationUtils.isValidEmail(email)) {
            return false;
        }
        
        return userDAO.emailExists(email);
    }
    
    /**
     * Връща броя на администраторите в системата
     * @return брой администратори
     */
    public int getAdminCount() {
        return userDAO.getAdminCount();
    }
    
    /**
     * Проверява дали потребител има администраторски права
     * @param userId ID на потребителя
     * @return true ако потребителят е администратор, false в противен случай
     */
    public boolean isUserAdmin(int userId) {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            return false;
        }
        
        return "администратор".equals(user.getRole());
    }
}
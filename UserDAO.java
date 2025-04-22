package library.dao;

import library.model.User;
import library.utils.PasswordEncryptor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO клас за операции с потребители в базата данни
 */
public class UserDAO {
    
    /**
     * Добавя нов потребител в базата данни
     * @param user потребителят, който трябва да бъде добавен
     * @return ID на добавения потребител или -1 при неуспех
     */
    public int addUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // Хеширане на паролата преди съхранение
            String hashedPassword = PasswordEncryptor.encryptPassword(user.getPassword());
            
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, user.getRole());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Добавянето на потребител не бе успешно, няма редове за добавяне");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Добавянето на потребител не бе успешно, не е генериран ID");
            }
        } catch (SQLException e) {
            System.err.println("Грешка при добавяне на потребител: " + e.getMessage());
            return -1;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }
    
    /**
     * Обновява информация за потребител в базата данни
     * @param user потребителят с обновената информация
     * @return true при успех, false при неуспех
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, role = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Грешка при обновяване на потребител: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Обновява паролата на потребителя
     * @param userId ID на потребителя
     * @param newPassword нова парола
     * @return true при успех, false при неуспех
     */
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Хеширане на новата парола преди съхранение
            String hashedPassword = PasswordEncryptor.encryptPassword(newPassword);
            
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Грешка при обновяване на парола: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Изтрива потребител от базата данни
     * @param userId ID на потребителя, който трябва да бъде изтрит
     * @return true при успех, false при неуспех
     */
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Грешка при изтриване на потребител: " + e.getMessage());
            return false;
        } finally {
            closeResources(conn, pstmt, null);
        }
    }
    
    /**
     * Намира потребител по ID
     * @param userId ID на търсения потребител
     * @return потребителят или null, ако не е намерен
     */
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при търсене на потребител по ID: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Намира потребител по email
     * @param email email на търсения потребител
     * @return потребителят или null, ако не е намерен
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при търсене на потребител по email: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Проверява дали потребителят съществува и паролата е правилна
     * @param email email на потребителя
     * @param password парола на потребителя
     * @return потребителят при успешно влизане или null при неуспех
     */
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                // Проверка на паролата
                if (PasswordEncryptor.checkPassword(password, user.getPassword())) {
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Грешка при вход в системата: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Връща списък с всички потребители
     * @return списък с всички потребители в базата данни
     */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        List<User> users = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Грешка при извличане на всички потребители: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return users;
    }
    
    /**
     * Проверява дали даден email вече съществува
     * @param email email за проверка
     * @return true ако email-ът вече е регистриран, false в противен случай
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Грешка при проверка за съществуващ email: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    /**
     * Намира броя на потребителите с администраторски права
     * @return брой администратори
     */
    public int getAdminCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'администратор'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при броене на администратори: " + e.getMessage());
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return 0;
    }
    
    /**
     * Извлича потребител от ResultSet обект
     * @param rs ResultSet обект
     * @return извлеченият потребител
     * @throws SQLException при грешка в извличането
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
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
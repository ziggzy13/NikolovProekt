package library.model;

/**
 * Клас, представящ потребител в библиотечната система
 */
public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private String role; // "потребител" или "администратор"
    
    /**
     * Конструктор по подразбиране
     */
    public User() {
    }
    
    /**
     * Конструктор с параметри
     */
    public User(int userId, String name, String email, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    /**
     * Конструктор за нов потребител (без ID)
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "потребител";
    }
    
    // Getters и Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "Потребител{" +
                "ID=" + userId +
                ", Име='" + name + '\'' +
                ", Email='" + email + '\'' +
                ", Роля='" + role + '\'' +
                '}';
    }
}
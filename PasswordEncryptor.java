package library.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Клас за криптиране и проверка на пароли
 */
public class PasswordEncryptor {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Криптира парола с SHA-256 и случайна сол
     * @param password паролата за криптиране
     * @return хеширана парола със сол във формат: [сол]:[хеш]
     */
    public static String encryptPassword(String password) {
        try {
            // Генерираме случайна сол
            byte[] salt = generateSalt();
            
            // Хешираме паролата
            byte[] hash = hash(password, salt);
            
            // Кодираме сол и хеш в Base64
            String saltStr = Base64.getEncoder().encodeToString(salt);
            String hashStr = Base64.getEncoder().encodeToString(hash);
            
            // Връщаме комбинацията от сол и хеш
            return saltStr + ":" + hashStr;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Грешка при хеширане на парола", e);
        }
    }
    
    /**
     * Проверява дали въведената парола съвпада с хешираната парола от базата данни
     * @param password въведената от потребителя парола
     * @param storedPassword хеширана парола от базата данни
     * @return true ако паролите съвпадат, false в противен случай
     */
    public static boolean checkPassword(String password, String storedPassword) {
        try {
            // Разделяме солта и хеша
            String[] parts = storedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            // Декодираме солта от Base64
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            
            // Хешираме въведената парола със същата сол
            byte[] hash = hash(password, salt);
            
            // Кодираме хеша в Base64
            String hashStr = Base64.getEncoder().encodeToString(hash);
            
            // Сравняваме хешираните пароли
            return parts[1].equals(hashStr);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Генерира случайна сол
     * @return масив от байтове със случайни стойности
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }
    
    /**
     * Хешира парола със зададена сол
     * @param password парола за хеширане
     * @param salt сол за хеширане
     * @return хеширана парола
     * @throws NoSuchAlgorithmException ако алгоритъмът не е наличен
     */
    private static byte[] hash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        md.reset();
        md.update(salt);
        return md.digest(password.getBytes());
    }
}
package library.utils;

import java.util.regex.Pattern;

/**
 * Клас, който съдържа методи за валидация на данни
 */
public class ValidationUtils {
    
    // Шаблон за валидна email адрес
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Шаблон за силна парола (поне 8 символа, поне 1 цифра, поне 1 малка и 1 голяма буква)
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");
    
    /**
     * Проверява дали поле не е празно
     * @param value стойност за валидация
     * @return true ако полето не е празно, false в противен случай
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Проверява дали всички полета не са празни
     * @param values масив от стойности за валидация
     * @return true ако всички полета не са празни, false в противен случай
     */
    public static boolean areNotEmpty(String... values) {
        for (String value : values) {
            if (!isNotEmpty(value)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Проверява дали email адресът е валиден
     * @param email email за валидация
     * @return true ако email адресът е валиден, false в противен случай
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Проверява дали паролата е достатъчно силна
     * @param password парола за валидация
     * @return true ако паролата е силна, false в противен случай
     */
    public static boolean isStrongPassword(String password) {
        if (!isNotEmpty(password)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Проверява дали два низа съвпадат
     * @param first първи низ
     * @param second втори низ
     * @return true ако низовете съвпадат, false в противен случай
     */
    public static boolean areEqual(String first, String second) {
        if (first == null || second == null) {
            return false;
        }
        return first.equals(second);
    }
    
    /**
     * Проверява дали низ е цяло число
     * @param value низ за проверка
     * @return true ако низът е цяло число, false в противен случай
     */
    public static boolean isInteger(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Проверява дали низ е число с плаваща запетая
     * @param value низ за проверка
     * @return true ако низът е число с плаваща запетая, false в противен случай
     */
    public static boolean isDouble(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Проверява дали стойността е в зададен диапазон
     * @param value стойност за проверка
     * @param min минимална стойност
     * @param max максимална стойност
     * @return true ако стойността е в диапазона, false в противен случай
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Проверява дали стойността е в зададен диапазон
     * @param value стойност за проверка
     * @param min минимална стойност
     * @param max максимална стойност
     * @return true ако стойността е в диапазона, false в противен случай
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
package com.op.teacipherfx.service;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {
    public List<String> getMissingCriteria(String password) {
        List<String> missing = new ArrayList<>();
        if (password.length() < 16) missing.add("- Довжина мінімум 16 символів");
        if (!password.matches(".*[A-Z].*")) missing.add("- Велика літера [A-Z]");
        if (!password.matches(".*[a-z].*")) missing.add("- Мала літера [a-z]");
        if (!password.matches(".*\\d.*")) missing.add("- Цифра [0-9]");
        if (!password.matches(".*[^a-zA-Z0-9].*")) missing.add("- Спеціальний символ (наприклад !@#$)");
        return missing;
    }

    public boolean isValid(String password) {
        return getMissingCriteria(password).isEmpty();
    }
}
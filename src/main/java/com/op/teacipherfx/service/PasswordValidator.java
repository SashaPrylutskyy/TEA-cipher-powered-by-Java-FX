package com.op.teacipherfx.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PasswordValidator {

    private record ValidationRule(Predicate<String> condition, String errorMessage) {
    }

    private final List<ValidationRule> rules = List.of(
            new ValidationRule(p -> p.length() >= 16, "- Довжина мінімум 16 символів"),
            new ValidationRule(p -> p.matches(".*[A-Z].*"), "- Велика літера [A-Z]"),
            new ValidationRule(p -> p.matches(".*[a-z].*"), "- Мала літера [a-z]"),
            new ValidationRule(p -> p.matches(".*\\d.*"), "- Цифра [0-9]"),
            new ValidationRule(p -> p.matches(".*[^a-zA-Z0-9].*"), "- Спеціальний символ (наприклад !@#$)")
    );

    public List<String> getMissingCriteria(String password) {
        if (password == null) return List.of("- Пароль не може бути порожнім");

        List<String> missing = new ArrayList<>();
        for (ValidationRule rule : rules) {
            if (!rule.condition.test(password)) {
                missing.add(rule.errorMessage);
            }
        }
        return missing;
    }

    public boolean isValid(String password) {
        return getMissingCriteria(password).isEmpty();
    }
}
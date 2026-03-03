package com.op.teacipherfx.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестування компонента валідації паролів")
class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    @DisplayName("Метод isValid має повертати true, якщо пароль відповідає всім критеріям безпеки")
    void isValid_ValidPassword_ReturnsTrue() {
        String securePassword = "Aa1!567890123456";

        boolean result = validator.isValid(securePassword);

        assertThat(result)
                .as("Пароль, що містить усі необхідні типи символів та має довжину 16, повинен бути валідним")
                .isTrue();
    }

    @Test
    @DisplayName("Метод getMissingCriteria має повертати помилку довжини, якщо вхідний рядок занадто короткий")
    void getMissingCriteria_ShortPassword_ReturnsLengthError() {
        String shortPassword = "Short1!";

        List<String> missing = validator.getMissingCriteria(shortPassword);

        assertThat(missing)
                .as("Список помилок повинен містити вказівку на мінімальну довжину символів")
                .anyMatch(s -> s.contains("16 символів"));
    }

    @Test
    @DisplayName("Метод isValid має повертати false, якщо в паролі відсутні спеціальні символи")
    void isValid_MissingSpecialCharacter_ReturnsFalse() {
        String passwordWithoutSpecials = "NoSpecialChar1234A";

        boolean result = validator.isValid(passwordWithoutSpecials);

        assertThat(result)
                .as("Пароль без спеціальних символів не відповідає політиці безпеки")
                .isFalse();
    }

    @Test
    @DisplayName("Метод isValid має повертати false при отриманні null, забезпечуючи стійкість до помилок")
    void isValid_NullInput_ReturnsFalse() {
        boolean result = validator.isValid(null);

        assertThat(result)
                .as("Валідатор повинен коректно обробляти null значення без викидання винятків")
                .isFalse();
    }
}
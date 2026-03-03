package com.op.teacipherfx.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестування криптографічного сервісу TEA")
class TeaCipherServiceTest {

    private TeaCipherService cipherService;
    private final String key = "_H-vR&9UFQ6g1FEC";

    @BeforeEach
    void setUp() {
        cipherService = new TeaCipherService();
    }

    @Test
    @DisplayName("Цикл шифрування та розшифрування має повертати ідентичний оригінальний текст")
    void decrypt_PreviouslyEncryptedData_RestoresOriginalText() {
        String original = "Hello, JavaFX!";

        byte[] encryptedBytes = cipherService.encrypt(original, key);
        String base64Encrypted = cipherService.binaryToString(encryptedBytes);
        String decrypted = cipherService.decrypt(base64Encrypted, key);

        assertThat(decrypted)
                .as("Після повного циклу обробки текст повинен залишитися незмінним")
                .isEqualTo(original);
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "Text with trailing spaces  ", "12345678"})
    @DisplayName("Алгоритм має зберігати всі символи (включаючи пробіли) завдяки коректній роботі падінгу")
    void decrypt_InputWithSpecificPadding_PreservesEveryCharacter(String input) {
        byte[] encrypted = cipherService.encrypt(input, key);
        String base64 = cipherService.binaryToString(encrypted);
        String decrypted = cipherService.decrypt(base64, key);

        assertThat(decrypted)
                .as("Будь-які символи, включаючи пробіли, мають бути відновлені без використання trim()")
                .isEqualTo(input);
    }

    @Test
    @DisplayName("Метод generatePassword має створювати унікальні паролі довжиною 16 символів")
    void generatePassword_StandardInvocation_ReturnsUniqueSixteenCharString() {
        String pass1 = cipherService.generatePassword();
        String pass2 = cipherService.generatePassword();

        assertThat(pass1).hasSize(16);
        assertThat(pass1).isNotEqualTo(pass2);
    }
}
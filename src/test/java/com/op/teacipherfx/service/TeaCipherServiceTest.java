package com.op.teacipherfx.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TeaCipherServiceTest {

    private TeaCipherService cipherService;
    private final String key = "_H-vR&9UFQ6g1FEC";

    @BeforeEach
    void setUp() {
        cipherService = new TeaCipherService();
    }

    @Test
    void testEncryptionDecryptionCycle() {
        String original = "Hello, JavaFX!";
        byte[] encryptedBytes = cipherService.encrypt(original, key);
        String base64Encrypted = cipherService.binaryToString(encryptedBytes);

        String decrypted = cipherService.decrypt(base64Encrypted, key);

        assertEquals(original, decrypted, "Розшифрований текст має збігатися з оригіналом");
    }

    @Test
    void testKnownVector() {
        String input = "iloveyou";
        String expectedBase64 = "A+oZbOVnY3PllnoGAY02Mg==";

        byte[] encryptedBytes = cipherService.encrypt(input, key);
        String resultBase64 = cipherService.binaryToString(encryptedBytes);

        assertEquals(expectedBase64, resultBase64, "Результат шифрування не збігається з еталоном Base64");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "12345678", "123456789", "Дуже довгий текст для перевірки падінгу блоків"})
    void testPaddingConsistency(String input) {
        byte[] encryptedBytes = cipherService.encrypt(input, key);
        String base64 = cipherService.binaryToString(encryptedBytes);
        String decrypted = cipherService.decrypt(base64, key);

        assertEquals(input, decrypted, "Алгоритм має коректно обробляти тексти різної довжини (падінг)");
    }
}
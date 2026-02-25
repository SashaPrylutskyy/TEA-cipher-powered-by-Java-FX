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
        cipherService = new TeaCipherService(); //
    }

    @Test
    void testEncryptionDecryptionCycle() {
        String original = "Hello, JavaFX!";
        String encrypted = cipherService.encrypt(original, key); //
        String decrypted = cipherService.decrypt(encrypted, key); //

        assertEquals(original, decrypted, "Розшифрований текст має збігатися з оригіналом");
    }

    @Test
    void testKnownVector() {
        String input = "iloveyou";
        // Розрахований еталонний Base64 для Little-Endian TEA з PKCS7 падінгом
        String expectedBase64 = "A+oZbOVnY3PllnoGAY02Mg==";

        String result = cipherService.encrypt(input, key); //
        assertEquals(expectedBase64, result, "Результат шифрування не збігається з еталоном Little-Endian");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "12345678", "123456789"})
    void testPaddingConsistency(String input) {
        String encrypted = cipherService.encrypt(input, key); //
        String decrypted = cipherService.decrypt(encrypted, key); //
        assertEquals(input, decrypted, "Алгоритм має коректно обробляти тексти різної довжини (падінг)");
    }
}
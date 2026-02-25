package com.op.teacipherfx.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator(); //

    @Test
    void testValidPassword() {
        assertTrue(validator.isValid("Aa1!567890123456")); //
    }

    @Test
    void testTooShortPassword() {
        List<String> missing = validator.getMissingCriteria("Short1!"); //
        assertTrue(missing.stream().anyMatch(s -> s.contains("16 символів")));
    }

    @Test
    void testMissingSpecialChar() {
        assertFalse(validator.isValid("NoSpecialChar1234A")); //
    }
}
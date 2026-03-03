package com.op.teacipherfx.service;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class TeaCipherService {

    public byte[] encrypt(String plainText, String password) {
        byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] paddedData = addPadding(data);
        int[] key = formatKey(password);

        for (int i = 0; i < paddedData.length; i += 8) {
            int[] block = bytesToInts(paddedData, i);
            TeaAlgorithm.encipherBlock(block, key);
            intsToBytes(block, paddedData, i);
        }
        return paddedData;
    }

    public String decrypt(String base64CipherText, String password) {
        byte[] data = Base64.getDecoder().decode(base64CipherText);
        int[] key = formatKey(password);

        for (int i = 0; i < data.length; i += 8) {
            int[] block = bytesToInts(data, i);
            TeaAlgorithm.decipherBlock(block, key);
            intsToBytes(block, data, i);
        }
        return new String(removePadding(data), StandardCharsets.UTF_8);
    }

    public String bytesToBitString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            for (int i = 7; i >= 0; i--) {
                sb.append((b >> i) & 1);
            }
        }
        return sb.toString();
    }

    public String binaryToString(byte[] binaryData) {
        return Base64.getEncoder().encodeToString(binaryData);
    }

    public String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private int[] formatKey(String password) {
        byte[] keyBytes = new byte[16];
        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(passBytes, 0, keyBytes, 0, Math.min(passBytes.length, 16));
        int[] key = new int[4];
        for (int i = 0; i < 4; i++) {
            int off = i * 4;
            key[i] = (keyBytes[off] & 0xFF) | ((keyBytes[off + 1] & 0xFF) << 8) |
                    ((keyBytes[off + 2] & 0xFF) << 16) | ((keyBytes[off + 3] & 0xFF) << 24);
        }
        return key;
    }

    private int[] bytesToInts(byte[] b, int offset) {
        int v0 = (b[offset] & 0xFF) | ((b[offset + 1] & 0xFF) << 8) |
                ((b[offset + 2] & 0xFF) << 16) | ((b[offset + 3] & 0xFF) << 24);
        int v1 = (b[offset + 4] & 0xFF) | ((b[offset + 5] & 0xFF) << 8) |
                ((b[offset + 6] & 0xFF) << 16) | ((b[offset + 7] & 0xFF) << 24);
        return new int[]{v0, v1};
    }

    private void intsToBytes(int[] v, byte[] b, int offset) {
        for (int i = 0; i < 4; i++) {
            b[offset + i] = (byte) (v[0] >> (i * 8));
            b[offset + 4 + i] = (byte) (v[1] >> (i * 8));
        }
    }

    private byte[] addPadding(byte[] data) {
        int padCount = 8 - (data.length % 8);
        byte[] padded = new byte[data.length + padCount];
        System.arraycopy(data, 0, padded, 0, data.length);
        for (int i = data.length; i < padded.length; i++) padded[i] = (byte) padCount;
        return padded;
    }

    private byte[] removePadding(byte[] data) {
        int padCount = data[data.length - 1] & 0xFF;
        if (padCount < 1 || padCount > 8) return data;
        byte[] unpadded = new byte[data.length - padCount];
        System.arraycopy(data, 0, unpadded, 0, unpadded.length);
        return unpadded;
    }
}
package com.op.teacipherfx.service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class WorkspaceService {
    private static final String KEY_FILE_NAME = "key.op";
    private static final String ENCRYPTED_DIR = "Encrypted";
    private static final String DECRYPTED_DIR = "Decrypted";
    private static final DateTimeFormatter FILE_NAME_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd.'No'.HHmmss");

    public void saveBinaryResult(byte[] content, String prefix, String password) throws Exception {
        String folderName = "KeySession_" + Integer.toHexString(password.hashCode());
        File targetDir = new File(new File(folderName), ENCRYPTED_DIR);

        if (!targetDir.exists()) targetDir.mkdirs();

        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMAT);
        String filename = String.format("%s_%s.bin", prefix, timestamp);
        Files.write(new File(targetDir, filename).toPath(), content);

        log.info("Бінарний файл збережено для тестів: {}", filename);
    }

    public void saveNistBitstream(String bitString, String password) throws Exception {
        String folderName = "KeySession_" + Integer.toHexString(password.hashCode());
        File targetDir = new File(new File(folderName), ENCRYPTED_DIR);

        if (!targetDir.exists()) targetDir.mkdirs();

        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMAT);
        String filename = String.format("NIST_BITS_%s.txt", timestamp);

        java.nio.file.Files.writeString(new File(targetDir, filename).toPath(), bitString);
        log.info("Файл з бітами для NIST збережено: {}", filename);
    }

    public void saveResult(String content, String prefix, String password, boolean isEncryption) throws Exception {
        String folderName = "KeySession_" + Integer.toHexString(password.hashCode());
        File baseDir = new File(folderName);

        File targetDir = new File(baseDir, isEncryption ? ENCRYPTED_DIR : DECRYPTED_DIR);

        targetDir.mkdirs();

        if (isEncryption) {
            File keyFile = new File(baseDir, KEY_FILE_NAME);
            if (!keyFile.exists()) {
                Files.writeString(keyFile.toPath(), password);
            }
        }

        String timestamp = LocalDateTime.now().format(FILE_NAME_FORMAT);
        String filename = String.format("%s_%s.txt", prefix, timestamp);
        Files.writeString(new File(targetDir, filename).toPath(), content);

        log.info("Результат збережено у: {}", targetDir.getPath());
    }
}
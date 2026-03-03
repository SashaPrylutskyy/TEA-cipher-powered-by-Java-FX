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

    private File getSessionDirectory(String password, String subDir) {
        String folderName = "KeySession_" + Integer.toHexString(password.hashCode());
        File dir = new File(new File(folderName), subDir);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public void saveNistBitstream(String bitString, String password) throws Exception {
        File targetDir = getSessionDirectory(password, ENCRYPTED_DIR);
        String filename = String.format("NIST_BITS_%s.txt", LocalDateTime.now().format(FILE_NAME_FORMAT));
        Files.writeString(new File(targetDir, filename).toPath(), bitString);
        log.info("NIST біти збережено: {}", filename);
    }

    public void saveResult(String content, String prefix, String password, boolean isEncryption) throws Exception {
        String subDir = isEncryption ? ENCRYPTED_DIR : DECRYPTED_DIR;
        File targetDir = getSessionDirectory(password, subDir);

        if (isEncryption) {
            File keyFile = new File(targetDir.getParentFile(), KEY_FILE_NAME);
            if (!keyFile.exists()) Files.writeString(keyFile.toPath(), password);
        }

        String filename = String.format("%s_%s.txt", prefix, LocalDateTime.now().format(FILE_NAME_FORMAT));
        Files.writeString(new File(targetDir, filename).toPath(), content);
        log.info("Результат збережено у: {}", targetDir.getPath());
    }
}
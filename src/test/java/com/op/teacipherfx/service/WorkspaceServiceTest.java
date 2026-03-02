package com.op.teacipherfx.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceServiceTest {

    private final WorkspaceService service = new WorkspaceService();
    private final String pass = "TestKey123!@#456";
    private final String sessionFolderName = "KeySession_" + Integer.toHexString(pass.hashCode());

    @AfterEach
    void cleanUp() throws Exception {
        File dir = new File(sessionFolderName);
        if (dir.exists()) {
            Files.walk(dir.toPath()).sorted(Comparator.reverseOrder()).map(java.nio.file.Path::toFile).forEach(File::delete);
        }
    }

    @Test
    void testSaveResultAndDirectoryStructure() throws Exception {
        service.saveResult("Base64Content", "ENCRYPTED", pass, true);

        File sessionDir = new File(sessionFolderName);
        File encryptedDir = new File(sessionDir, "Encrypted");
        File keyFile = new File(sessionDir, "key.op");

        assertTrue(sessionDir.exists(), "Папка сесії має бути створена");
        assertTrue(encryptedDir.exists(), "Підпапка Encrypted має бути створена");
        assertTrue(keyFile.exists(), "Файл ключа key.op має бути створений");
    }

    @Test
    void testSaveBinaryResult() throws Exception {
        byte[] rawData = {0x01, 0x02, 0x03, 0x04};
        service.saveBinaryResult(rawData, "NIST_READY", pass);

        File encryptedDir = new File(new File(sessionFolderName), "Encrypted");
        File[] files = encryptedDir.listFiles((dir, name) -> name.endsWith(".bin"));

        assertNotNull(files, "Директорія не повинна бути порожньою");
        assertTrue(files.length > 0, "Мав бути створений хоча б один .bin файл");
    }

    @Test
    void testSaveNistBitstream() throws Exception {
        String bitString = "010101011100";
        service.saveNistBitstream(bitString, pass);

        File encryptedDir = new File(new File(sessionFolderName), "Encrypted");
        File[] files = encryptedDir.listFiles((dir, name) -> name.startsWith("NIST_BITS_") && name.endsWith(".txt"));

        assertNotNull(files, "Директорія не повинна бути порожньою");
        assertTrue(files.length > 0, "Файл з бітовою послідовністю NIST має бути створений");

        String savedContent = Files.readString(files[0].toPath());
        assertEquals(bitString, savedContent, "Збережений бітовий рядок має збігатися з оригіналом");
    }
}
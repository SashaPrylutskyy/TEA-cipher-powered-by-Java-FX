package com.op.teacipherfx.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тестування сервісу керування робочим простором")
class WorkspaceServiceTest {

    private final WorkspaceService service = new WorkspaceService();
    private final String pass = "TestKey123!@#456";
    private final String sessionFolderName = "KeySession_" + Integer.toHexString(pass.hashCode());

    @AfterEach
    void cleanUp() throws Exception {
        File dir = new File(sessionFolderName);
        if (dir.exists()) {
            Files.walk(dir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(java.nio.file.Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    @DisplayName("Метод saveResult має створювати ієрархію папок та файл ключа при шифруванні")
    void saveResult_EncryptionEnabled_CreatesSessionStructureAndKeyFile() throws Exception {
        service.saveResult("Content", "ENCRYPTED", pass, true);

        File sessionDir = new File(sessionFolderName);
        File encryptedDir = new File(sessionDir, "Encrypted");
        File keyFile = new File(sessionDir, "key.op");

        assertThat(sessionDir).exists().isDirectory();
        assertThat(encryptedDir).exists().isDirectory();
        assertThat(keyFile).exists().isFile();
    }

    @Test
    @DisplayName("Метод saveNistBitstream має записувати коректну бітову послідовність у файл")
    void saveNistBitstream_ValidBitString_WritesExactContentToFile() throws Exception {
        String bitString = "110011001100";

        service.saveNistBitstream(bitString, pass);

        File encryptedDir = new File(new File(sessionFolderName), "Encrypted");
        File[] files = encryptedDir.listFiles((dir, name) -> name.startsWith("NIST_BITS_"));

        assertThat(files).isNotNull().isNotEmpty();
        String savedContent = Files.readString(files[0].toPath());
        assertThat(savedContent)
                .as("Вміст файлу NIST має точно відповідати переданому бітовому рядку")
                .isEqualTo(bitString);
    }
}
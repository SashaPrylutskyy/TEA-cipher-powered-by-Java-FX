package com.op.teacipherfx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WorkspaceServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void testDirectoryCreation() throws Exception {
        WorkspaceService service = new WorkspaceService(); //
        String pass = "TestKey123!@#45678";

        service.saveResult("Content", "PREFIX", pass, true); //

        String sessionFolder = "KeySession_" + Integer.toHexString(pass.hashCode());
        File dir = new File(sessionFolder);

        assertTrue(dir.exists(), "Папка сесії має бути створена");
    }
}
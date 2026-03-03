package com.op.teacipherfx;

import com.op.teacipherfx.service.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Slf4j
public class CipherController {

    @FXML
    private TextField passwordField;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private Label statusLabel;
    @FXML
    private Button encryptButton, decryptButton;
    @FXML
    private Label passwordStrengthLabel;
    @FXML
    private ProgressBar strengthBar;

    private final TeaCipherService cipherService = new TeaCipherService();
    private final WorkspaceService workspaceService = new WorkspaceService();
    private final PasswordValidator passwordValidator = new PasswordValidator();

    @FXML
    public void initialize() {
        passwordField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 16 ? change : null));

        passwordField.textProperty().addListener((obs, old, val) -> updatePasswordUI(val));
        setupDragAndDrop();
    }

    private void updatePasswordUI(String password) {
        List<String> missing = passwordValidator.getMissingCriteria(password);
        boolean isValid = missing.isEmpty();

        strengthBar.setProgress(password.length() / 16.0);

        if (isValid) {
            strengthBar.setStyle("-fx-accent: #2ecc71;");
            passwordStrengthLabel.setText("✓ Надійний ключ (128-bit)");
            passwordStrengthLabel.setTextFill(Color.GREEN);
        } else {
            strengthBar.setStyle("-fx-accent: #e74c3c;");
            passwordStrengthLabel.setText("Слабкий ключ. Треба:\n" + String.join("\n", missing));
            passwordStrengthLabel.setTextFill(Color.RED);
        }

        encryptButton.setDisable(!isValid);
        decryptButton.setDisable(!isValid);
    }

    @FXML
    protected void onEncryptClick() {
        processCipherAction(true);
    }

    @FXML
    protected void onDecryptClick() {
        processCipherAction(false);
    }

    private void processCipherAction(boolean isEncrypt) {
        try {
            String pass = passwordField.getText();
            String input = inputTextArea.getText();

            if (input == null || input.isEmpty()) {
                showStatus("Введіть дані для обробки", Color.ORANGE);
                return;
            }

            if (isEncrypt) {
                byte[] encrypted = cipherService.encrypt(input, pass);
                String base64 = cipherService.binaryToString(encrypted);

                workspaceService.saveNistBitstream(cipherService.bytesToBitString(encrypted), pass);
                workspaceService.saveResult(base64, "ENCRYPTED", pass, true);

                inputTextArea.setText(base64);
                showStatus("Успішно зашифровано", Color.GREEN);
            } else {
                String decrypted = cipherService.decrypt(input, pass);
                workspaceService.saveResult(decrypted, "DECRYPTED", pass, false);
                inputTextArea.setText(decrypted);
                showStatus("Успішно розшифровано", Color.GREEN);
            }
        } catch (Exception e) {
            log.error("Помилка під час виконання операції: ", e);
            showStatus("Помилка обробки даних!", Color.RED);
        }
    }

    @FXML
    protected void onGeneratePasswordClick() {
        passwordField.setText(cipherService.generatePassword());
    }

    @FXML
    protected void onUserManualClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Довідка");
        alert.setHeaderText("Інструкція TEA Cipher");
        alert.setContentText("""
                1. Використовуйте .op для завантаження ключів.
                2. Перетягуйте .txt файли безпосередньо в область тексту.
                3. Шифрування автоматично створює папку сесії з результатами.
                """);
        alert.showAndWait();
    }

    @FXML
    protected void onLoadPasswordFileClick() {
        loadFileToTarget(".op", "Ключ", passwordField, true);
    }

    @FXML
    protected void onLoadTextFileClick() {
        loadFileToTarget(".txt", "Текст", inputTextArea, false);
    }

    private void loadFileToTarget(String ext, String desc, Control target, boolean shouldTrim) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, "*" + ext));
        File file = chooser.showOpenDialog(statusLabel.getScene().getWindow());
        if (file != null) loadContent(file, target, shouldTrim);
    }

    private void loadContent(File file, Control target, boolean shouldTrim) {
        try {
            String content = Files.readString(file.toPath());
            if (shouldTrim) content = content.trim();

            if (target instanceof TextField t) t.setText(content);
            else if (target instanceof TextArea t) t.setText(content);

            showStatus("Завантажено: " + file.getName(), Color.GREEN);
        } catch (Exception e) {
            log.error("Файл не завантажено", e);
            showStatus("Помилка завантаження файлу", Color.RED);
        }
    }

    private void setupDragAndDrop() {
        passwordField.setOnDragOver(event -> handleDragOver(event, ".op"));
        passwordField.setOnDragDropped(event -> handleDragDropped(event, passwordField, ".op", true));

        inputTextArea.setOnDragOver(event -> handleDragOver(event, ".txt"));
        inputTextArea.setOnDragDropped(event -> handleDragDropped(event, inputTextArea, ".txt", false));
    }

    private void handleDragOver(DragEvent event, String extension) {
        if (event.getDragboard().hasFiles()) {
            File file = event.getDragboard().getFiles().get(0);
            if (file.getName().toLowerCase().endsWith(extension)) {
                event.acceptTransferModes(TransferMode.COPY);
            }
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event, Control target, String extension, boolean shouldTrim) {
        if (event.getDragboard().hasFiles()) {
            File file = event.getDragboard().getFiles().get(0);
            loadContent(file, target, shouldTrim);
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private void showStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(color);
    }
}
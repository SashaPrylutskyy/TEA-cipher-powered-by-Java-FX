package com.op.teacipherfx;

import com.op.teacipherfx.service.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
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
    private Button encryptButton;
    @FXML
    private Button decryptButton;
    @FXML
    private Label passwordStrengthLabel;

    private final TeaCipherService cipherService = new TeaCipherService();
    private final WorkspaceService workspaceService = new WorkspaceService();
    private final PasswordValidator passwordValidator = new PasswordValidator();

    @FXML
    public void initialize() {
        setupPasswordFormatting();
        setupDragAndDrop();
        passwordField.textProperty().addListener((obs, old, newValue) -> validatePassword(newValue));
    }

    private void setupPasswordFormatting() {
        passwordField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= 16 ? change : null));
    }

    private void validatePassword(String password) {
        List<String> missingCriteria = passwordValidator.getMissingCriteria(password);
        if (missingCriteria.isEmpty()) {
            passwordStrengthLabel.setText("✓ Надійний ключ (128-bit Ready)");
            passwordStrengthLabel.setTextFill(Color.GREEN);
            setButtonsDisabled(false);
        } else {
            passwordStrengthLabel.setText("Слабкий ключ. Бракує:\n" + String.join("\n", missingCriteria));
            passwordStrengthLabel.setTextFill(Color.RED);
            setButtonsDisabled(true);
        }
    }

    private void setButtonsDisabled(boolean disabled) {
        encryptButton.setDisable(disabled);
        decryptButton.setDisable(disabled);
    }

    @FXML
    protected void onEncryptClick() {
        executeAction(true);
    }

    @FXML
    protected void onDecryptClick() {
        executeAction(false);
    }

    @FXML
    protected void onGeneratePasswordClick() {
        passwordField.setText(cipherService.generatePassword());
    }

    @FXML
    protected void onUserManualClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Довідка");
        alert.setHeaderText("Як користуватися TEA Cipher");
        alert.setContentText("""
                1. Введіть або згенеруйте ключ (16 символів).
                2. Введіть текст або перетягніть .txt файл.
                3. Натисніть Шифрувати/Розшифрувати.
                Результати зберігаються в папку KeySession.""");
        alert.showAndWait();
    }

    @FXML
    protected void onLoadPasswordFileClick() {
        File file = openFileChooser("Виберіть файл ключа", "Ключ (*.op)", "*.op");
        if (file != null) {
            loadFileContent(file, passwordField);
        }
    }

    @FXML
    protected void onLoadTextFileClick() {
        File file = openFileChooser("Виберіть текстовий файл", "Текст (*.txt)", "*.txt");
        if (file != null) {
            loadFileContent(file, inputTextArea);
        }
    }

    private void executeAction(boolean isEncryption) {
        try {
            String pass = passwordField.getText();
            String input = inputTextArea.getText();
            if (pass.isEmpty() || input.isEmpty()) return;

            if (isEncryption) {
                byte[] rawBytes = cipherService.encrypt(input, pass);
                String bitString = cipherService.bytesToBitString(rawBytes);

                workspaceService.saveNistBitstream(bitString, pass);

                String base64Result = cipherService.binaryToString(rawBytes);
                inputTextArea.setText(base64Result);

                workspaceService.saveResult(base64Result, "ENCRYPTED", pass, true);

                showStatus("Успішно! Створено файл з бітами для тестів.", Color.GREEN);
            } else {
                String decryptedText = cipherService.decrypt(input, pass);
                workspaceService.saveResult(decryptedText, "DECRYPTED", pass, false);
                inputTextArea.setText(decryptedText);
            }
        } catch (Exception e) {
            log.error("Cipher error", e);
            showStatus("Помилка! Перевірте дані.", Color.RED);
        }
    }

    private File openFileChooser(String title, String desc, String mask) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, mask));
        return fileChooser.showOpenDialog(statusLabel.getScene().getWindow());
    }

    private void loadFileContent(File file, Control target) {
        try {
            String content = Files.readString(file.toPath()).trim();
            if (target instanceof TextField tf) tf.setText(content);
            else if (target instanceof TextArea ta) ta.setText(content);
            showStatus("Файл завантажено", Color.GREEN);
        } catch (Exception e) {
            showStatus("Помилка завантаження!", Color.RED);
        }
    }

    private void setupDragAndDrop() {
        passwordField.setOnDragOver(e -> handleDragOver(e, ".op"));
        passwordField.setOnDragDropped(e -> handleDragDropped(e, passwordField));
        inputTextArea.setOnDragOver(e -> handleDragOver(e, ".txt"));
        inputTextArea.setOnDragDropped(e -> handleDragDropped(e, inputTextArea));
    }

    private void handleDragOver(javafx.scene.input.DragEvent event, String extension) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles() && db.getFiles().get(0).getName().endsWith(extension)) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragDropped(javafx.scene.input.DragEvent event, Control target) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            loadFileContent(db.getFiles().get(0), target);
        }
        event.setDropCompleted(true);
        event.consume();
    }

    private void showStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(color);
    }
}
package com.op.teacipherfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CipherApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CipherApplication.class.getResource("application-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 650);
        stage.setTitle("TEA cipher by OP");
        stage.setScene(scene);
        stage.show();
    }
}

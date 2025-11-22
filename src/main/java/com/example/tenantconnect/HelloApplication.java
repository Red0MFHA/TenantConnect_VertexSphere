package com.example.tenantconnect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/tenantconnect/new.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

        scene.getStylesheets().add(
                getClass().getResource("/com/example/tenantconnect/app.css").toExternalForm()
        );

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}

package net.chuyang.apptracer.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Apptracer extends Application {

	@Override
	public void start(Stage primaryStage)  throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("Apptracer.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

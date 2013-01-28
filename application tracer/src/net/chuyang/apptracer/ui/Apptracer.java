package net.chuyang.apptracer.ui;

import java.util.ResourceBundle;

import net.chuyang.apptracer.Utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Apptracer extends Application {

	@Override
	public void start(Stage primaryStage)  throws Exception {
		ResourceBundle i18nBundle = ResourceBundle.getBundle("net/chuyang/apptracer/i18n/RB");
		Parent root = FXMLLoader.load(getClass().getResource("Apptracer.fxml"), i18nBundle);
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle(Utils.getlocalizedString("Apptracer.title.txt"));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

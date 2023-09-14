package com.example.dalrental;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainApplication extends Application {
    //Stage is the main window
    private Stage primaryStage;
    //in the main method, run launch as default
    public static void main(String[] args) {
        launch();
    }
    @Override
    //This is the home page, it will open when app is launched, we want login page opens first
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showLoginPage(); // Display the sign-up page initially
    }

    //load the signup page, use FXML Loader to load the page, *com.example.fxml path is changed to /com/example/fxml
    public void showSignUpPage() throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dalrental/views/SignUp.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Sign Up Page");
        primaryStage.show();
    }

    //Connect login page with main application
    public void showLoginPage() throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/dalrental/views/Login.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }


}
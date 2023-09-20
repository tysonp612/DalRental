package com.example.dalrental.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import models.UserModel;

import java.io.IOException;

public class LoginController {
    @FXML
    private Button loginBtn;
    @FXML
    private Button signUpBtn;
    //Load credentials (username and password - decrypted)

    //Check credentials with the DB

    //If error, error handling the message

    //If pass, get to the main page

    //Go to sign up


    @FXML
    private void goToSignUp(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dalrental/views/SignUp.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) signUpBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}

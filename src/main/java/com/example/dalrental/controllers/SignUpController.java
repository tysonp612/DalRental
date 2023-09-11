package com.example.dalrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import models.UserModel;

public class SignUpController {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    @FXML
    private TextField usernameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private PasswordField passwordConfirmInput;
    @FXML
    private TextField emailInput;
    @FXML
    private Label signUpErrorPanel;
    @FXML
    private Button closeErrorPanelBtn;
    @FXML
    private Button createUserBtn;

    @FXML
    private void checkLength(KeyEvent e) {
//        //How to get ID of any element
//        //1. get source of event, this will return an object
//        Object source = e.getSource();
//        //2. Casting from object to textField (or the elememnt's proper type)
//        TextField input = (TextField) source;
//        //3. In the element type (no object), we can extract id by using .getId();
//        String id = input.getId();
        String id = findId(e, TextField.class);
        if (id.equals("userInput")) {
            if (usernameInput.getText().length() < 8 && usernameInput.getText().length() > 2) {
                closeErrorPanel();
                this.username = usernameInput.getText();
            } else {
                signUpErrorHandler("Username has to be in range of 2 to 8 character");
            }
        } else if (id.equals("passwordInput")) {
            if (passwordInput.getText().length() < 15 && passwordInput.getText().length() > 5) {
                closeErrorPanel();
                this.password = passwordInput.getText();
            } else {
                signUpErrorHandler("Password has to be in range of 5 to 15 character");
            }
        }

    }

    @FXML
    private void checkPasswordConfirm() {
        //if the passwordConfirm field is not yet typed, no error shown
        if (this.passwordConfirmInput.getText().length() > 0) {
            //if it is typed, start comparing with the password typed after checking length;
            if (this.password != null && passwordConfirmInput.getText().equals(this.password)) {
                closeErrorPanel();
                this.confirmPassword = passwordConfirmInput.getText();
            } else {
                signUpErrorHandler("Password confirm does not match with password");
            }
        }
    }

    @FXML
    private void checkDalEmail() {
        if (this.emailInput.getText().length() > 0) {
            if (this.emailInput.getText().contains("@dal")) {
                closeErrorPanel();
                this.email = emailInput.getText();
            } else {
                signUpErrorHandler("Email has to be a Dalhousie email");
            }
        }
    }

    @FXML
    private void signUpErrorHandler(String message) {
        signUpErrorPanel.setOpacity(1.0);
        closeErrorPanelBtn.setOpacity(1.0);
        signUpErrorPanel.setText(message);
    }

    @FXML
    private void closeErrorPanel() {
        signUpErrorPanel.setOpacity(0.0);
        closeErrorPanelBtn.setOpacity(0.0);
        signUpErrorPanel.setText("");
    }

    @FXML
    private void createUserAndSaveToDB() {
        //hash password first
        //create new user
        UserModel newUser = new UserModel(this.username, this.password, this.email);
        //newUser.saveToDB();
        System.out.println(newUser);
    }


    private <T extends Control> String findId(KeyEvent event  , Class<T> controlClass) {
        Object source = event.getSource();

        if (controlClass.isInstance(source)) {
            T control = controlClass.cast(source);
            return control.getId();
        } else {
            // Handle the case where the source is not of type T
            return null; // or throw an exception, depending on your use case
        }
    }
}

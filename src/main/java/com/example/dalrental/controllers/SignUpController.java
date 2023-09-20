package com.example.dalrental.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import models.UserCredentials;
import utilities.auth.KeyedPasswordCryptographer;
import utilities.auth.PasswordEncryptEngine;

import java.io.IOException;

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
    private Button goToLoginBtn;
    @FXML
    private void checkLength(KeyEvent e) {
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
        if (!this.passwordConfirmInput.getText().isEmpty()) {
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
        if (!this.emailInput.getText().isEmpty()) {
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
        // Encryption engine used to hash passwords
        Class<? extends PasswordEncryptEngine> encryptionEngine = KeyedPasswordCryptographer.class;

        UserCredentials<? extends PasswordEncryptEngine> newUser
                = new UserCredentials<>(this.username, this.email, encryptionEngine);

        // Encrypts the given password and sets it into the user's credentials
        newUser.setPassword(this.password);

        // TODO: newUser.saveToDB();
        System.out.println(newUser);
    }

    //move back to login page
    @FXML
    private void goToLogin(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dalrental/views/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) goToLoginBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Finds the ID of the key event
     * @param event  KeyEvent you want to get the ID of
     * @param controlClass Class you want the event to be cast to
     * @return ID as a string
     * @param <T> Cast Object
     */
    private <T extends Control> String findId(KeyEvent event, Class<T> controlClass) {
        try {
            Object source = event.getSource();
            T control = controlClass.cast(source);
            return control.getId();
        } catch (ClassCastException e) {
            System.out.println();
            // Handle the case where the source is not of type T
            throw new ClassCastException(String.format("""
                            Fatal class cast exception. Cannot cast from %s to %s
                            """.trim(),
                    event,
                    controlClass.getName()));
        }
    }
}

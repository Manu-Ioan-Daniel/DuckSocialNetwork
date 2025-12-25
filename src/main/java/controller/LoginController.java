package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.UserModel;
import utils.Models;
import utils.StageManager;
import validation.UiUtils;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    @FXML
    private HBox root;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Button loginBtn;

    private UserModel userModel;


    @FXML
    public void login(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(!userModel.validLogin(username,password)){
            UiUtils.addTemporaryStylesheet(usernameField,"text-field-error");
            UiUtils.addTemporaryStylesheet(passwordField,"text-field-error");
            return;
        }
        StageManager.showUsersWindow((Stage) root.getScene().getWindow(),username);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginBtn.setDefaultButton(true);
        this.userModel = Models.getUserModel();
    }

}

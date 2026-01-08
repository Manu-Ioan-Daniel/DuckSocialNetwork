package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.User;
import service.SecurityService;
import utils.Services;
import utils.StageManager;
import utils.UiUtils;
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

    private SecurityService securityService;
    private StageManager stageManager;

    @FXML
    private void login(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(!securityService.validLogin(username,password)){
            UiUtils.addTemporaryStylesheet(usernameField,"text-field-error");
            UiUtils.addTemporaryStylesheet(passwordField,"text-field-error");
            return;
        }
        User currentUser = securityService.getUser(username).orElse(null);
        if(currentUser == null){
            UiUtils.addTemporaryStylesheet(usernameField,"text-field-error");
            return;
        }
        stageManager.showUsersWindow((Stage) root.getScene().getWindow(),currentUser);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginBtn.setDefaultButton(true);
        this.securityService = Services.getSecurityService();
        this.stageManager = new StageManager();
    }

}

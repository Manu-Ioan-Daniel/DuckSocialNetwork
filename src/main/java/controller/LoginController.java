package controller;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.FriendshipModel;
import models.UserModel;
import repo.DbFriendshipRepo;
import utils.FXMLUtil;
import utils.Models;
import utils.Tuple;
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
    private Label errorLabel;

    @FXML
    private Button loginBtn;

    private UserModel userModel;


    @FXML
    public void login(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        if(!userModel.validLogin(username,password)){
            showError(usernameField);
            showError(passwordField);
            return;
        }
        Tuple<Scene,UsersFormController> tuple = FXMLUtil.load("/view/usersForm.fxml");

        UsersFormController usersFormController = tuple.getSecond();
        usersFormController.initData(username);

        Scene scene = tuple.getFirst();

        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();


    }
    private void showError(TextField field) {
        if (!field.getStyleClass().contains("textfield-error")) {
            field.getStyleClass().add("textfield-error");
            errorLabel.setOpacity(1);
        }
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            field.getStyleClass().remove("textfield-error");
            errorLabel.setOpacity(0);
        });
        pause.play();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginBtn.setDefaultButton(true);
        this.userModel = Models.getUserModel();
    }


}

package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.FriendshipModel;
import models.UserModel;
import utils.Models;
import utils.StageManager;
import utils.observer.Observer;
import java.net.URL;
import java.util.ResourceBundle;

public class FriendsFormController implements Initializable, Observer {
    @FXML
    private Label usernameLabel;

    @FXML
    private BorderPane root;

    private UserModel userModel;
    private FriendshipModel friendshipModel;
    private final StageManager signoutAlertManager = new StageManager();


    public void setUsernameLabel(String username){
        usernameLabel.setText(username);
    }

    @FXML
    public void handleSignout(){
        signoutAlertManager.showConfirmationAlert(this::signout);
    }

    @FXML
    public void handleUsersWindow(){
        friendshipModel.removeObserver(this);
        userModel.removeObserver(this);
        Stage stage = (Stage) root.getScene().getWindow();
        StageManager.showUsersWindow(stage,usernameLabel.getText());
    }

    private void signout() {
        userModel.removeObserver(this);
        friendshipModel.removeObserver(this);
        Stage stage = (Stage) root.getScene().getWindow();
        StageManager.showSignoutWindow(stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userModel= Models.getUserModel();
        friendshipModel = Models.getFriendshipModel();
        userModel.addObserver(this);
        friendshipModel.addObserver(this);
    }
    @Override
    public void update(){

    }
}

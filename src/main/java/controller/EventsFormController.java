package controller;

import enums.ChangeEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.StageManager;
import utils.observer.NotificationHandler;
import utils.observer.Observer;

public class EventsFormController implements Observer {

    @FXML
    private Label usernameLabel;

    @FXML
    private BorderPane root;
    

    public void initData(String username){
        usernameLabel.setText(username);
        NotificationHandler.getInstance().addObserver(this);

        root.sceneProperty().addListener((observer,oldVal,newVal)->{
            if(newVal!=oldVal){
                NotificationHandler.getInstance().removeObserver(this);
            }
        });

    }

    public void handleFriendsWindow(){
        StageManager.showFriendsWindow(getStage(),usernameLabel.getText());
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    public void handleChatWindow(){
        StageManager.showChatWindow(getStage(),usernameLabel.getText(),null);
    }

    public void handleUsersWindow(){
        StageManager.showUsersWindow(getStage(),usernameLabel.getText());
    }
    public void handleSignout(){
        StageManager.showLoginWindow(getStage());
    }


    @Override
    public void update(ChangeEvent event) {

    }
}

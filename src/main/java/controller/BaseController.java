package controller;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import models.User;
import utils.StageManager;
import utils.observer.NotificationHandler;
import utils.observer.Observer;

public abstract class BaseController implements Observer {

    protected User currentUser;
    protected StageManager stageManager = new StageManager();

    protected void removeObservers() {
        NotificationHandler.getInstance().removeObserver(this);
    }

    protected abstract Stage getStage();

    @FXML
    protected void handleSignout() {
        stageManager.showConfirmationAlert(() -> {
            removeObservers();
            stageManager.showLoginWindow(getStage());
        });
    }

    @FXML
    protected void handleFriendsWindow() {
        removeObservers();
        stageManager.showFriendsWindow(getStage(), currentUser);
    }

    @FXML
    protected void handleChatWindow() {
        removeObservers();
        stageManager.showChatWindow(getStage(), currentUser, null);
    }

    @FXML
    protected void handleUsersWindow() {
        removeObservers();
        stageManager.showUsersWindow(getStage(), currentUser);
    }

    @FXML
    protected void handleEventsWindow() {
        removeObservers();
        stageManager.showEventsWindow(getStage(), currentUser);
    }
}
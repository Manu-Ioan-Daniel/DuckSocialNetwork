package utils;

import controller.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Event;
import models.User;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StageManager {
    private final Map<String, Stage> openStages = new HashMap<>();
    private void showStageOnce(String key, Supplier<Stage> stageSupplier) {
        Stage stage = openStages.get(key);
        if (stage != null) {
            stage.toFront();
            return;
        }
        stage = stageSupplier.get();
        openStages.put(key, stage);
        stage.setOnHidden(ev->openStages.remove(key));
        stage.show();
    }

    private void showStageReplace(String key, Supplier<Stage> stageSupplier) {
        Stage stage = openStages.get(key);
        if (stage != null) {
            stage.close();
        }
        stage = stageSupplier.get();
        openStages.put(key, stage);
        stage.setOnHidden(ev->openStages.remove(key));
        stage.show();
    }

    public void showErrorAlert(String message) {
        showStageReplace("errorAlert",()->Alert.errorAlert(message));
    }

    public void showConfirmationAlert(Runnable action) {
        showStageOnce("confirmationAlert",()->Alert.confirmationAlert(action));
    }
    public void showInformationAlert(String message){
        showStageReplace("informationAlert",()->Alert.informationAlert(message));
    }
    public void showLoginWindow(Stage stage){
        Tuple<Scene, LoginController> tuple = FXMLUtil.load(("/view/loginWindow.fxml"));
        Scene scene = tuple.getFirst();
        stage.setScene(scene);
        stage.centerOnScreen();
    }
    public void showFriendsWindow(Stage stage,User user){
        Tuple<Scene, FriendsFormController> tuple = FXMLUtil.load("/view/friendsForm.fxml");
        Scene scene = tuple.getFirst();
        stage.setScene(scene);
        stage.centerOnScreen();
        tuple.getSecond().initData(user);
    }
    public  void showUsersWindow(Stage stage,User user){
        Tuple<Scene, UsersFormController> tuple = FXMLUtil.load("/view/usersForm.fxml");
        Scene scene = tuple.getFirst();
        stage.setScene(scene);
        stage.centerOnScreen();
        tuple.getSecond().initData(user);
    }

    public  void showAddUserWindow(){
        showStageOnce("addUserWindow",()->{
            Tuple<Scene, AddUserFormController> tuple = FXMLUtil.load("/view/addUserForm.fxml");
            Scene scene = tuple.getFirst();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            return stage;
        });
    }

    public  void showChatWindow(Stage stage, User currentUser,String username2) {
        Tuple<Scene, ChatController> tuple = FXMLUtil.load("/view/chat.fxml");
        Scene scene = tuple.getFirst();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.toFront();
        tuple.getSecond().initData(currentUser);
        if(username2!=null){
            tuple.getSecond().setSelectedUser(username2);
        }
    }

    public  void showProfileWindow(Stage parentStage, User user, User loggedInUser){
        showStageReplace("profile",()->{
            Tuple<Scene, UserProfileController> tuple = FXMLUtil.load("/view/" + user.getType() + "Profile.fxml");
            Scene scene = tuple.getFirst();
            Stage stage = new Stage();
            stage.setScene(scene);
            UserProfileController userProfileController = tuple.getSecond();
            userProfileController.initData(parentStage,user, loggedInUser);
            return stage;
        });
    }

    public  void showEventsWindow(Stage stage, User user) {
        Tuple<Scene, EventsFormController> tuple = FXMLUtil.load("/view/eventsForm.fxml");
        Scene scene = tuple.getFirst();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.toFront();
        tuple.getSecond().initData(user);
    }

    public  void showAddEventWindow() {
        showStageOnce("addEventWindow",()->{
           Tuple<Scene, AddEventFormController> tuple = FXMLUtil.load("/view/addEventForm.fxml");
           tuple.getSecond().initData();
           Stage stage = new Stage();
           stage.setScene(tuple.getFirst());
           return stage;
        });
    }

    public  void showSendMessageWindow(Event event) {
        showStageOnce("sendMessageWindow", ()->{
            Tuple<Scene,SendMessageFormController> tuple =  FXMLUtil.load("/view/sendMessageForm.fxml");
            tuple.getSecond().initData(event);
            Stage stage = new Stage();
            stage.setScene(tuple.getFirst());
            return stage;
        });
    }
}

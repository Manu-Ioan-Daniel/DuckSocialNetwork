package utils;

import controller.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.User;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StageManager {
    private static final Map<String, Stage> openStages = new HashMap<>();
    private StageManager(){}
    private static void showStageOnce(String key, Supplier<Stage> stageSupplier) {
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

    private static void showStageReplace(String key, Supplier<Stage> stageSupplier) {
        Stage stage = openStages.get(key);
        if (stage != null) {
            stage.close();
        }
        stage = stageSupplier.get();
        openStages.put(key, stage);
        stage.setOnHidden(ev->openStages.remove(key));
        stage.show();
    }

    public static void showErrorAlert(String message) {
        showStageReplace("errorAlert",()->Alert.errorAlert(message));
    }

    public static void showConfirmationAlert(Runnable action) {
        showStageOnce("confirmationAlert",()->Alert.confirmationAlert(action));
    }
    public static void showInformationAlert(String message){
        showStageReplace("informationAlert",()->Alert.informationAlert(message));
    }
    public static void showLoginWindow(Stage stage){
        Tuple<Scene, LoginController> tuple = FXMLUtil.load(("/view/loginWindow.fxml"));

        Scene scene = tuple.getFirst();

        stage.setScene(scene);
        stage.centerOnScreen();
    }
    public static void showFriendsWindow(Stage stage,String username){
        Tuple<Scene, FriendsFormController> tuple = FXMLUtil.load("/view/friendsForm.fxml");
        tuple.getSecond().initData(username);
        stage.setScene(tuple.getFirst());
        stage.centerOnScreen();
    }
    public static void showUsersWindow(Stage stage,String username){
        Tuple<Scene, UsersFormController> tuple = FXMLUtil.load("/view/usersForm.fxml");
        tuple.getSecond().initData(username);
        stage.setScene(tuple.getFirst());
        stage.centerOnScreen();
    }

    public static void showAddUserWindow(){
        showStageOnce("addUserWindow",()->{
            Tuple<Scene, AddUserFormController> tuple = FXMLUtil.load("/view/addUserForm.fxml");
            Scene scene = tuple.getFirst();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            return stage;
        });
    }

    public static void showChatWindow(Stage stage, String username,String username2) {
        Tuple<Scene, ChatController> tuple = FXMLUtil.load("/view/chat.fxml");
        tuple.getSecond().initData(username);
        if(username2!=null){
            tuple.getSecond().setSelectedUser(username2);
        }
        stage.setScene(tuple.getFirst());
        stage.centerOnScreen();
        stage.toFront();
    }

    public static void showProfileWindow(Stage parentStage, User user, User loggedInUser){
        showStageReplace("profile",()->{

            Tuple<Scene, UserProfileController> tuple = FXMLUtil.load("/view/" + user.getType() + "Profile.fxml");

            UserProfileController userProfileController = tuple.getSecond();
            userProfileController.initData(parentStage,user, loggedInUser);

            Scene scene = tuple.getFirst();

            Stage stage = new Stage();
            stage.setScene(scene);

            return stage;
        });
    }
}

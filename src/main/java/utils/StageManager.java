package utils;

import controller.AddUserFormController;
import controller.FriendsFormController;
import controller.LoginController;
import controller.UsersFormController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
}

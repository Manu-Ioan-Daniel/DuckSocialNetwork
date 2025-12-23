package utils;

import controller.FriendsFormController;
import controller.LoginController;
import controller.UsersFormController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageManager {

    private Stage stage;

    public void showStage(Scene scene, boolean decorated) {
        if (stage != null) {
            stage.toFront();
            return;
        }
        stage = new Stage();
        stage.setScene(scene);
        if (!decorated) {
            stage.initStyle(StageStyle.UNDECORATED);
        }
        stage.setOnHidden(e -> stage = null);
        stage.show();
    }

    public void showErrorAlert(String message) {
        if (stage != null) {
            stage.close();
        }

        stage = Alert.errorAlert(message);
        stage.setOnHidden(e -> stage = null);
        stage.show();
    }

    public void showConfirmationAlert(Runnable action) {
        if (stage != null) {
            stage.close();
        }
        stage = Alert.confirmationAlert(action);
        stage.setOnHidden(e -> stage = null);
        stage.show();
    }
    public static void showSignoutWindow(Stage stage){
        Tuple<Scene, LoginController> tuple = FXMLUtil.load(("/view/loginWindow.fxml"));

        Scene scene = tuple.getFirst();

        stage.setScene(scene);
        stage.centerOnScreen();
    }
    public static void showFriendsWindow(Stage stage,String username){
        Tuple<Scene, FriendsFormController> tuple = FXMLUtil.load("/view/friendsForm.fxml");
        tuple.getSecond().setUsernameLabel(username);
        stage.setScene(tuple.getFirst());
        stage.centerOnScreen();
    }
    public static void showUsersWindow(Stage stage,String username){
        Tuple<Scene, UsersFormController> tuple = FXMLUtil.load("/view/usersForm.fxml");
        tuple.getSecond().initData(username);
        stage.setScene(tuple.getFirst());
        stage.centerOnScreen();
    }
}

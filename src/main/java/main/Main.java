package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage stage){
        try {
            openLoginWindow();
            openLoginWindow();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    private void openLoginWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loginWindow.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}

package controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.DraggableUtil;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfirmationAlertController implements Initializable {

    @FXML
    private VBox root;

    private Runnable action;

    public void setAction(Runnable action) {
        this.action = action;
    }
    @FXML
    private Button noBtn;

    @FXML
    private Button yesBtn;

    @FXML
    private void noAction() {
        ((Stage) noBtn.getScene().getWindow()).close();
    }
    @FXML
    private void yesAction() {
        action.run();
        ((Stage) yesBtn.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DraggableUtil.makeDraggable(root);
    }
}

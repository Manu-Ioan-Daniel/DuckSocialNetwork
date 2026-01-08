package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.DraggableUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class InformationAlertController implements Initializable {
    @FXML
    private Label messageLabel;

    @FXML
    private VBox root;

    @FXML
    private void handleOk() {
        ((Stage)root.getScene().getWindow()).close();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DraggableUtil.makeDraggable(root);
    }
}

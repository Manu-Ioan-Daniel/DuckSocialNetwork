package controller;

import domain.Duck;
import domain.Person;
import domain.User;
import enums.DuckType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.UserModel;
import utils.DraggableUtil;
import utils.Models;
import utils.StageManager;
import validation.UiUtils;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddUserFormController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private DatePicker date;

    @FXML
    private VBox duckFieldsVBox;

    @FXML
    private ComboBox<DuckType> duckTypeComboBox;

    @FXML
    private TextField emailField;

    @FXML
    private Slider empathySlider;

    @FXML
    private TextField name;

    @FXML
    private TextField occupation;

    @FXML
    private PasswordField passwordField;

    @FXML
    private VBox personFieldsVBox;

    @FXML
    private TextField resistance;

    @FXML
    private TextField speed;

    @FXML
    private TextField surname;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TextField usernameField;

    private UserModel userModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        duckFieldsVBox.setVisible(false);
        personFieldsVBox.setVisible(true);
        this.userModel = Models.getUserModel();
        date.setValue(LocalDate.now());
        DraggableUtil.makeDraggable(root);
        initTypeComboBox();
        initDuckTypeComboBox();

    }

    private void initTypeComboBox() {
        typeComboBox.getItems().add("Duck");
        typeComboBox.getItems().add("Person");
        typeComboBox.setValue("Person");
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isDuck = "Duck".equals(newVal);
            personFieldsVBox.setVisible(!isDuck);
            duckFieldsVBox.setVisible(isDuck);
        });
    }

    private void initDuckTypeComboBox() {
        duckTypeComboBox.getItems().add(DuckType.FLYING);
        duckTypeComboBox.getItems().add(DuckType.SWIMMING);
        duckTypeComboBox.setValue(DuckType.FLYING);
    }

    @FXML
    public void handleAdd() {
        if(!validInput()){
            return;
        }
        try {
            userModel.save(getUser());
            ((Stage)root.getScene().getWindow()).close();
        }catch(Exception ex){
            StageManager.showErrorAlert(ex.getMessage());
        }
    }

    @FXML
    public void handleClose() {
        ((Stage) root.getScene().getWindow()).close();
    }


    private User getUser() {
        User user;
        if(typeComboBox.getValue().equals("Duck")) {
            user = new Duck(
                    usernameField.getText(), emailField.getText(),passwordField.getText(),duckTypeComboBox.getValue(),
                    Double.parseDouble(speed.getText()),Double.parseDouble(resistance.getText()));
        }else{
            user = new Person(usernameField.getText(), emailField.getText(),passwordField.getText(),name.getText(),surname.getText(),
                    date.getValue(),occupation.getText(),(int)empathySlider.getValue());
        }
        return user;
    }

    private boolean isDuck(){
        return "Duck".equals(typeComboBox.getValue());
    }

    private boolean validInput() {
        if (isDuck())
            return UiUtils.checkInputs(usernameField, emailField, passwordField, speed, resistance);
        return UiUtils.checkInputs(usernameField, emailField, passwordField, name, surname, occupation);
    }
}


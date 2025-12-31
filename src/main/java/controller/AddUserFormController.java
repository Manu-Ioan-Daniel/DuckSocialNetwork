package controller;

import models.User;
import enums.DuckType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.UsersService;
import utils.DraggableUtil;
import utils.Services;
import utils.StageManager;
import utils.dtos.UserDTO;
import utils.factories.UserFactory;
import utils.UiUtils;

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

    private UsersService usersService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        duckFieldsVBox.setVisible(false);
        personFieldsVBox.setVisible(true);
        this.usersService = Services.getUsersService();
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
            usersService.save(getUser());
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
        UserDTO input =  new UserDTO();

        //user data

        input.setUsername(usernameField.getText());
        input.setEmail(emailField.getText());
        input.setPassword(passwordField.getText());
        input.setType(typeComboBox.getValue());

        //duck data

        input.setDuckType(duckTypeComboBox.getValue());
        input.setResistance(resistance.getText().isEmpty() ? null : Double.parseDouble(resistance.getText()));
        input.setSpeed(speed.getText().isEmpty() ? null : Double.parseDouble(speed.getText()));

        //person data

        input.setName(name.getText());
        input.setSurname(surname.getText());
        input.setOccupation(occupation.getText());
        input.setDateOfBirth(date.getValue());
        input.setEmpathyLevel((int) empathySlider.getValue());

        return UserFactory.getInstance().createUser(input);
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


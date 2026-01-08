package controller;

import enums.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Event;
import service.EventService;
import utils.Services;
import utils.StageManager;
import utils.UiUtils;
import utils.factories.EventFactory;

public class AddEventFormController {


    @FXML
    private ComboBox<EventType> eventTypeComboBox;

    @FXML
    private TextField nameField;

    @FXML
    private VBox root;

    private EventService eventService;
    private StageManager stageManager;

    public void initData() {
        initComboBox();
        this.eventService = Services.getEventService();
        this.stageManager = new StageManager();
    }

    private void initComboBox(){
        eventTypeComboBox.getItems().add(EventType.RACE_EVENT);
        eventTypeComboBox.setValue(EventType.RACE_EVENT);
    }


    @FXML
    private void handleAdd() {
        UiUtils.checkInputs(nameField);
        try {
            Event ev = EventFactory.getInstance().createEvent(nameField.getText(), eventTypeComboBox.getValue());
            eventService.addEvent(ev);
            ((Stage) root.getScene().getWindow()).close();
        }catch(Exception e){
            stageManager.showErrorAlert(e.getMessage());
        }
    }
    @FXML
    private void handleClose() {
        ((Stage) root.getScene().getWindow()).close();
    }

}

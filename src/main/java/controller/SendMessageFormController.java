package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Event;
import service.EventService;
import utils.Services;
import utils.UiUtils;

public class SendMessageFormController {


    @FXML
    private TextField messageField;

    @FXML
    private VBox root;

    private Event event;
    private EventService eventService;

    public void initData(Event event){
        this.event = event;
        this.eventService = Services.getEventService();
    }

    @FXML
    private void handleSend(){
        if(messageField.getText().isEmpty()) {
            UiUtils.addTemporaryStylesheet(messageField, "text-field-error");
            return;
        }
        eventService.sendMessageToSubscribers("You have received an event message from event called:  " + event.getName() + "\n" + messageField.getText(),event);
        ((Stage) root.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel(){
        ((Stage) root.getScene().getWindow()).close();
    }

}

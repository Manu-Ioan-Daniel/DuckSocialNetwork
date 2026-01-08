package controller;

import enums.ChangeEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.Event;
import models.User;
import service.EventService;
import utils.NotificationUtils;
import utils.Services;
import utils.StageManager;
import utils.dtos.EventSubscriberDTO;
import utils.observer.NotificationHandler;
import utils.observer.Observer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventsFormController extends BaseController implements Observer {

    @FXML
    private Label usernameLabel;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<Event> eventsTable;

    @FXML
    private TableColumn<Event, String> eventsColumn;

    @FXML
    private TableColumn<Event, String> typeColumn;

    @FXML
    private TableView<EventSubscriberDTO> registeredUsersTable;

    @FXML
    private TableColumn<EventSubscriberDTO,String> usernameColumn;

    @FXML
    private TableColumn<EventSubscriberDTO, String> dateColumn;

    private EventService eventService;
    

    public void initData(User currentUser){

        NotificationHandler.getInstance().addObserver(this);

        this.eventService = Services.getEventService();
        this.currentUser = currentUser;

        usernameLabel.setText(currentUser.getUsername());

        initTables();
    }

    private void initTables() {
        initEventsTable();
        initRegisteredUsersTable();
    }

    private void initEventsTable() {
        eventsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(cell-> new SimpleStringProperty(cell.getValue().getType()));
        eventsTable.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->{
            if(newValue!=null)
                refreshRegisteredUsersTable();
        }));
        refreshEventsTable();
    }

    private void initRegisteredUsersTable() {
        usernameColumn.setCellValueFactory(cell->{
            Long id = cell.getValue().getUserId();
            String username = eventService.findOneUser(id).map(User::getUsername).orElse("");
            return new SimpleStringProperty(username);
        });
        dateColumn.setCellValueFactory(cell->{
            LocalDateTime date = cell.getValue().getDate();
            return new SimpleStringProperty(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        });

    }

    private void refreshEventsTable(){
        eventsTable.setItems(FXCollections.observableList(eventService.findAllEvents()));
    }

    private void refreshRegisteredUsersTable() {
        Event event = getSelectedEvent();
        if (event == null) {
            registeredUsersTable.getItems().clear();
            return;
        }
        List<EventSubscriberDTO> subscribers = eventService.getSubscribers(event.getId());
        registeredUsersTable.setItems(FXCollections.observableList(subscribers));
    }
    private Event getSelectedEvent(){
        return eventsTable.getSelectionModel().getSelectedItem();
    }


    @FXML
    private void handleAdd(){
        stageManager.showAddEventWindow();
    }

    @FXML
    private void handleRegister(){
        if(!validEventSelection()) {
            stageManager.showErrorAlert("You did not select an event to register to!");
            return;
        }
        try {
            eventService.addSubscriber(getSelectedEvent(),currentUser);
        }catch(Exception e){
            stageManager.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    private void handleSendMessage(){
        Event event = getSelectedEvent();
        if(event == null)
            stageManager.showErrorAlert("You did not select an event!");
        stageManager.showSendMessageWindow(event);

    }

    private boolean validEventSelection(){
        return eventsTable.getSelectionModel().getSelectedItem() != null;
    }


    @Override
    protected Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    private void loadNotifications() {
        String notification = NotificationUtils.getAndClearNotifications(eventService.getNotifications(currentUser.getId()),eventService::deleteNotification);
        if(notification == null || notification.isEmpty())
            return;
        stageManager.showInformationAlert(notification);
    }

    @Override
    public void update(ChangeEvent event) {
        if(event == ChangeEvent.EVENT_SAVED)
            refreshEventsTable();

        else if(event == ChangeEvent.USER_DATA || event == ChangeEvent.USER_SUBSCRIBED)
            refreshRegisteredUsersTable();
        else if (event == ChangeEvent.NOTIFICATION)
            loadNotifications();

    }
}

package controller;

import enums.ChangeEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.RaceEvent;
import models.User;
import service.EventService;
import utils.Services;
import utils.StageManager;
import utils.observer.NotificationHandler;
import utils.observer.Observer;

public class EventsFormController implements Observer {

    @FXML
    private Label usernameLabel;

    @FXML
    private BorderPane root;

    @FXML
    private TableView<RaceEvent> eventsTable;

    @FXML
    private TableColumn<RaceEvent, String> eventsColumn;

    @FXML
    private TableView<User> registeredUsersTable;

    @FXML
    private TableColumn<User,String> usernameColumn;

    @FXML
    private TableColumn<User,String> dateColumn;

    private EventService eventService;
    

    public void initData(String username){
        usernameLabel.setText(username);

        NotificationHandler.getInstance().addObserver(this);

        this.eventService = Services.getEventService();

        initTables();
    }

    private void initTables() {
        initEventsTable();
        initRegisteredUsersTable();
    }

    private void initEventsTable() {
        eventsColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        refreshEventsTable();
    }

    private void initRegisteredUsersTable() {

        refreshRegisteredUsersTable();
    }

    public void refreshEventsTable(){
        eventsTable.setItems(FXCollections.observableList(eventService.findAll()));
    }

    public void refreshRegisteredUsersTable(){

    }

    public void handleFriendsWindow(){
        removeObservers();
        StageManager.showFriendsWindow(getStage(),usernameLabel.getText());
    }

    public void handleChatWindow(){
        removeObservers();
        StageManager.showChatWindow(getStage(),usernameLabel.getText(),null);
    }

    public void handleUsersWindow(){
        removeObservers();
        StageManager.showUsersWindow(getStage(),usernameLabel.getText());
    }

    public void handleSignout(){
        removeObservers();
        StageManager.showLoginWindow(getStage());
    }

    public void removeObservers(){
        NotificationHandler.getInstance().removeObserver(this);
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    @Override
    public void update(ChangeEvent event) {

    }
}

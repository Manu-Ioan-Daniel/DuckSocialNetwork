package controller;

import models.Notification;
import models.User;
import enums.ChangeEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.CommunityService;
import utils.NotificationUtils;
import utils.Services;
import utils.StageManager;
import utils.dtos.UserTableDTO;
import utils.observer.NotificationHandler;
import utils.observer.Observer;

import java.util.List;
import java.util.Objects;



public class UsersFormController extends BaseController implements Observer{
    @FXML
    private BorderPane root;

    @FXML
    private Label duckCount;

    @FXML
    private Label peopleCount;

    @FXML
    private Label userCount;

    @FXML
    private Label usernameLabel;

    @FXML
    private TableView<UserTableDTO> usersTableView;

    @FXML
    private TableColumn<UserTableDTO,String> usernameColumn;

    @FXML
    private TableColumn<UserTableDTO,String> emailColumn;

    @FXML
    private TableColumn<UserTableDTO,String> friendsColumn;

    @FXML
    private TableColumn<UserTableDTO,String> typeColumn;

    @FXML
    private Pagination pagination;

    private CommunityService communityService;

    private final static int USERS_PER_PAGE = 3;

    public void initData(User currentUser) {
        
        this.communityService = Services.getCommunityService();
        this.currentUser = currentUser;
        
        usernameLabel.setText(currentUser.getUsername());
        NotificationHandler.getInstance().addObserver(this);

        initUsersTable();
        loadLabelInfo();
        initPagination();
        loadNotifications();
    }

    private void initPagination() {
        loadPaginationPageCount();
        pagination.setPageFactory((index)->{
            refreshUsersTable(index);
            return new VBox();
        });
    }

    private void initUsersTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        friendsColumn.setCellValueFactory(new PropertyValueFactory<>("friendsString"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        usersTableView.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2){
                stageManager.showProfileWindow((Stage) root.getScene().getWindow(),communityService.findUser(usersTableView.getSelectionModel().getSelectedItem().getUsername()).orElseThrow(),currentUser);
            }
        });
    }

    private void loadLabelInfo(){
        userCount.setText(Integer.toString(communityService.getTotalUsers()));
        duckCount.setText(Integer.toString(communityService.getTotalDucks()));
        peopleCount.setText(Integer.toString(communityService.getTotalPeople()));
    }

    private void loadPaginationPageCount(){
        pagination.setPageCount(communityService.getPageCount(USERS_PER_PAGE));
    }

    private void refreshUsersTable(int pageIndex){
        usersTableView.setItems(FXCollections.observableList(communityService.findUsersFromPageAsDTO(pageIndex, USERS_PER_PAGE)));
    }


    @Override
    protected Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    @FXML
    private void handleAdd(){
        stageManager.showAddUserWindow();
    }

    @FXML
    private void handleDelete(){
        UserTableDTO selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if(selectedUser == null){
            stageManager.showErrorAlert("Please select a user");
            return;
        }
        if(selectedUser.getUsername().equals(usernameLabel.getText())){
            stageManager.showErrorAlert("You cannot delete yourself!");
            return;
        }
        communityService.deleteUser(selectedUser.getId());
    }


    private void loadNotifications(){
        String notifications = NotificationUtils.getAndClearNotifications(communityService.getNotifications(currentUser.getId()), (communityService::deleteNotification));
        if(notifications == null || notifications.isEmpty())
            return;
        stageManager.showInformationAlert(notifications);
    }


    @Override
    public void update(ChangeEvent event){

        if(ChangeEvent.USER_DATA == event){
            loadLabelInfo();
            loadPaginationPageCount();
            refreshUsersTable(pagination.getCurrentPageIndex());
        }
        else if(ChangeEvent.NOTIFICATION == event){
            loadNotifications();
        }
    }

}

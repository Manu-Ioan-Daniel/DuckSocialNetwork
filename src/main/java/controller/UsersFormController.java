package controller;

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
import utils.Services;
import utils.StageManager;
import utils.dtos.UserTableDTO;
import utils.observer.NotificationHandler;
import utils.observer.Observer;
import java.util.Objects;



public class UsersFormController implements Observer{
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

    private User currentUser;

    private final static int USERS_PER_PAGE = 3;

    public void initData(String username) {
        this.communityService = Services.getCommunityService();
        usernameLabel.setText(username);
        try {
            communityService.findUser(usernameLabel.getText()).ifPresent(user -> currentUser = user);
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        NotificationHandler.getInstance().addObserver(this);

        initUsersTable();
        loadLabelInfo();
        initPagination();
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
                StageManager.showProfileWindow((Stage) root.getScene().getWindow(),communityService.findUser(usersTableView.getSelectionModel().getSelectedItem().getUsername()).orElseThrow(),currentUser);
            }
        });
    }



    public void loadLabelInfo(){
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

    @FXML
    public void handleSignout(){
        StageManager.showConfirmationAlert(this::signout);
    }

    public void signout(){
        removeObservers();
        StageManager.showLoginWindow(getStage());
    }

    public void handleFriendsWindow(){
        removeObservers();
        StageManager.showFriendsWindow(getStage(),usernameLabel.getText());
    }

    public void handleChatWindow(){
        removeObservers();
        StageManager.showChatWindow(getStage(),usernameLabel.getText(),null);
    }
    
    @FXML
    public void handleEventsWindow(){
        removeObservers();
        StageManager.showEventsWindow(getStage(),usernameLabel.getText());
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    private void removeObservers(){
        NotificationHandler.getInstance().removeObserver(this);
    }

    @FXML
    public void handleAdd(){
        StageManager.showAddUserWindow();
    }

    @FXML
    public void handleDelete(){
        UserTableDTO selectedUser = usersTableView.getSelectionModel().getSelectedItem();
        if(selectedUser == null){
            StageManager.showErrorAlert("Please select a user");
            return;
        }
        if(selectedUser.getUsername().equals(usernameLabel.getText())){
            StageManager.showErrorAlert("You cannot delete yourself!");
            return;
        }
        communityService.deleteUser(selectedUser.getId());
    }


    private void loadNotifications(){
        if(Objects.equals(communityService.getLastFriendRequest().getId().getSecond(), currentUser.getId()))
            StageManager.showInformationAlert("You have received a friend request!");
    }


    @Override
    public void update(ChangeEvent event){
        if(ChangeEvent.SENT_FRIEND_REQUEST == event){
            loadNotifications();
        }
        else if(ChangeEvent.USER_DATA == event){
            loadLabelInfo();
            loadPaginationPageCount();
            refreshUsersTable(pagination.getCurrentPageIndex());
        }
    }

}

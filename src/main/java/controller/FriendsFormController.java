package controller;

import domain.FriendRequest;
import domain.User;
import enums.ChangeEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import service.FriendsService;
import utils.Services;
import utils.StageManager;
import utils.observer.NotificationHandler;
import utils.observer.Observer;
import java.net.URL;
import java.util.ResourceBundle;

public class FriendsFormController implements Initializable, Observer {

    @FXML
    private Button friendReqBtn;

    @FXML
    private TableView<FriendRequest> toFriendReqTable;

    @FXML
    private TableColumn<FriendRequest, String> toColumn;

    @FXML
    private TableColumn<FriendRequest, String> toStatusColumn;

    @FXML
    private TableColumn<FriendRequest, String> fromColumn;

    @FXML
    private TableColumn<FriendRequest, String> fromStatusColumn;

    @FXML
    private TableView<FriendRequest> fromFriendReqTable;

    @FXML
    private TableColumn<User,String> friendsColumn;

    @FXML
    private TableColumn<User,String> othersColumn;

    @FXML
    private TableView<User> friendsTable;

    @FXML
    private TableView<User> othersTable;

    @FXML
    private Button removeFriendBtn;

    @FXML
    private BorderPane root;

    @FXML
    private Label usernameLabel;

    private FriendsService friendsService;

    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.friendsService = Services.getFriendsService();
        NotificationHandler.getInstance().addObserver(this);
    }

    public void initData(String username){
        usernameLabel.setText(username);
        friendsService.getUser(username).ifPresent(user -> {currentUser=user;});
        initFriendsTable();
        initOthersTable();
        initToFriendReqTable();
        initFromFriendReqTable();
    }

    private void initOthersTable() {
        othersColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        refreshOthersTable();
        othersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                friendsTable.getSelectionModel().clearSelection();
                removeFriendBtn.setVisible(false);
                friendReqBtn.setVisible(true);
            }
        });
    }

    private void initFriendsTable() {
        friendsColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        refreshFriendsTable();
        friendsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                othersTable.getSelectionModel().clearSelection();
                removeFriendBtn.setVisible(true);
                friendReqBtn.setVisible(false);
            }
        });
    }

    private void initToFriendReqTable() {
        toColumn.setCellValueFactory(cell-> new SimpleStringProperty(friendsService.getToUsername(cell.getValue())));
        toStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        refreshToFriendReqTable();
    }

    private void initFromFriendReqTable(){
        fromColumn.setCellValueFactory(cell-> new SimpleStringProperty(friendsService.getFromUsername(cell.getValue())));
        fromStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        refreshFromFriendReqTable();
    }

    private void refreshFriendsTable(){
        friendsTable.setItems(friendsService.getFriends(currentUser.getId()));
    }

    private void refreshOthersTable(){
        othersTable.setItems(friendsService.getOthers(currentUser.getId()));
    }

    private void refreshToFriendReqTable(){
        toFriendReqTable.setItems(friendsService.findSentFriendRequests(currentUser.getId()));
    }

    private void refreshFromFriendReqTable(){
        fromFriendReqTable.setItems(friendsService.findReceivedFriendRequests(currentUser.getId()));
    }

    @FXML
    public void removeFriend(){
        if(!validSelection(friendsTable)){
            StageManager.showErrorAlert("You did not select any friend to remove!");
            return;
        }
        try {
            User user = friendsTable.getSelectionModel().getSelectedItem();
            friendsService.deleteFriendRequest(currentUser.getId(),user.getId());
            friendsService.deleteFriend(currentUser.getId(), user.getId());
        }catch(Exception e){
            StageManager.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    public void friendRequest(){
        if(!validSelection(othersTable)){
            StageManager.showErrorAlert("You did not select any user to send a friend request to!");
            return;
        }
        User selectedUser = othersTable.getSelectionModel().getSelectedItem();
        try{
            friendsService.saveFriendRequest(currentUser.getId(), selectedUser.getId());
        }catch (Exception e){
            StageManager.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    public void cancelFriendRequest(){
        if(!validSelection(toFriendReqTable)){
            StageManager.showErrorAlert("You did not select any friend request to cancel!");
            return;
        }
        try {
            friendsService.cancelFriendRequest(toFriendReqTable.getSelectionModel().getSelectedItem());
        }catch (Exception e){
            StageManager.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    public void acceptFriendRequest(){
        if(!validSelection(fromFriendReqTable)){
            StageManager.showErrorAlert("You did not select any friend request to accept!");
            return;
        }
        try {
            friendsService.acceptFriendRequest(fromFriendReqTable.getSelectionModel().getSelectedItem());
        }catch (Exception e){
            StageManager.showErrorAlert(e.getMessage());
        }

    }

    @FXML
    public void denyFriendRequest(){
        if(!validSelection(fromFriendReqTable)){
            StageManager.showErrorAlert("You did not select any friend request to deny!");
            return;
        }
        try {
            friendsService.denyFriendRequest(fromFriendReqTable.getSelectionModel().getSelectedItem());
        }catch (Exception e){
            StageManager.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    public void handleSignout(){
        StageManager.showConfirmationAlert(this::signout);
    }

    private void signout() {
        NotificationHandler.getInstance().removeObserver(this);
        Stage stage = (Stage) root.getScene().getWindow();
        StageManager.showLoginWindow(stage);
    }

    @FXML
    public void handleUsersWindow(){
        NotificationHandler.getInstance().removeObserver(this);
        Stage stage = (Stage) root.getScene().getWindow();
        StageManager.showUsersWindow(stage,usernameLabel.getText());
    }

    public void handleChatWindow(){
        NotificationHandler.getInstance().removeObserver(this);
        Stage  stage = (Stage) root.getScene().getWindow();
        StageManager.showChatWindow(stage,usernameLabel.getText());
    }

    private boolean validSelection(TableView<?> tableView){
        return tableView.getSelectionModel().getSelectedItem() != null;
    }

    @Override
    public void update(ChangeEvent event){
        refreshFriendsTable();
        refreshOthersTable();
        refreshToFriendReqTable();
        refreshFromFriendReqTable();
    }
}
package controller;

import javafx.collections.FXCollections;
import models.FriendRequest;
import models.User;
import enums.ChangeEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import service.CommunityService;
import utils.Services;
import utils.StageManager;
import utils.observer.NotificationHandler;
import utils.observer.Observer;

public class FriendsFormController implements Observer {

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
    private TableColumn<User, String> friendsColumn;

    @FXML
    private TableColumn<User, String> othersColumn;

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

    private CommunityService communityService;

    private User currentUser;

    public void initData(String username)
    {
        usernameLabel.setText(username);

        this.communityService = Services.getCommunityService();
        communityService.findUser(username)
                .ifPresentOrElse(
                        user -> currentUser = user,
                        () -> { throw new RuntimeException("Current user not found!"); }
                );


        NotificationHandler.getInstance().addObserver(this);

        initFriendsTable();
        initOthersTable();
        initToFriendReqTable();
        initFromFriendReqTable();
    }

    private void initOthersTable() {
        othersColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        refreshOthersTable();
        othersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                friendsTable.getSelectionModel().clearSelection();
                removeFriendBtn.setVisible(false);
                friendReqBtn.setVisible(true);
            }
        });
    }

    private void initFriendsTable() {
        friendsColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        refreshFriendsTable();
        friendsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                othersTable.getSelectionModel().clearSelection();
                removeFriendBtn.setVisible(true);
                friendReqBtn.setVisible(false);
            }
        });
    }

    private void initToFriendReqTable() {
        toColumn.setCellValueFactory(cell -> new SimpleStringProperty(communityService.getToUsername(cell.getValue())));
        toStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        refreshToFriendReqTable();
    }

    private void initFromFriendReqTable() {
        fromColumn.setCellValueFactory(cell -> new SimpleStringProperty(communityService.getFromUsername(cell.getValue())));
        fromStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        refreshFromFriendReqTable();
    }

    private void refreshFriendsTable() {
        friendsTable.setItems(FXCollections.observableList(communityService.getFriends(currentUser.getId())));
    }

    private void refreshOthersTable() {
        othersTable.setItems(FXCollections.observableList(communityService.getOthers(currentUser.getId())));
    }

    private void refreshToFriendReqTable() {
        toFriendReqTable.setItems(FXCollections.observableList(communityService.findSentFriendRequests(currentUser.getId())));
    }

    private void refreshFromFriendReqTable() {
        fromFriendReqTable.setItems(FXCollections.observableList(communityService.findReceivedFriendRequests(currentUser.getId())));
    }

    private void runSafe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            StageManager.showErrorAlert(e.getMessage());
        }
    }

    private boolean validSelection(TableView<?> tableView) {
        return tableView.getSelectionModel().getSelectedItem() != null;
    }

    @FXML
    public void removeFriend() {
        if (!validSelection(friendsTable)) {
            StageManager.showErrorAlert("You did not select any friend to remove!");
            return;
        }
        runSafe(() -> {
            User user = friendsTable.getSelectionModel().getSelectedItem();
            communityService.deleteFriendRequest(currentUser.getId(), user.getId());
            communityService.deleteFriend(currentUser.getId(), user.getId());
        });
    }

    @FXML
    public void friendRequest() {
        if (!validSelection(othersTable)) {
            StageManager.showErrorAlert("You did not select any user to send a friend request to!");
            return;
        }
        User user = othersTable.getSelectionModel().getSelectedItem();
        runSafe(() -> communityService.saveFriendRequest(currentUser.getId(), user.getId()));
    }

    @FXML
    public void cancelFriendRequest() {
        if (!validSelection(toFriendReqTable)) {
            StageManager.showErrorAlert("You did not select any friend request to cancel!");
            return;
        }
        FriendRequest fr = toFriendReqTable.getSelectionModel().getSelectedItem();
        runSafe(() -> communityService.cancelFriendRequest(fr));
    }

    @FXML
    public void acceptFriendRequest() {
        if (!validSelection(fromFriendReqTable)) {
            StageManager.showErrorAlert("You did not select any friend request to accept!");
            return;
        }
        FriendRequest fr = fromFriendReqTable.getSelectionModel().getSelectedItem();
        runSafe(() -> communityService.acceptFriendRequest(fr));
    }

    @FXML
    public void denyFriendRequest() {
        if (!validSelection(fromFriendReqTable)) {
            StageManager.showErrorAlert("You did not select any friend request to deny!");
            return;
        }
        FriendRequest fr = fromFriendReqTable.getSelectionModel().getSelectedItem();
        runSafe(() -> communityService.denyFriendRequest(fr));
    }

    @FXML
    public void handleSignout() {
        StageManager.showConfirmationAlert(this::signout);
    }

    private void signout() {
        removeObservers();
        StageManager.showLoginWindow(getStage());
    }
    @FXML
    public void handleUsersWindow() {
        removeObservers();
        StageManager.showUsersWindow(getStage(), usernameLabel.getText());
    }
    @FXML
    public void handleChatWindow() {
        removeObservers();
        StageManager.showChatWindow(getStage(), usernameLabel.getText(),null);
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

    @Override
    public void update(ChangeEvent event) {
        if(event == ChangeEvent.USER_DATA || event == ChangeEvent.FRIENDSHIP_DATA) {
            refreshFriendsTable();
            refreshOthersTable();
        }
        else if(event == ChangeEvent.SENT_FRIEND_REQUEST || event == ChangeEvent.FRIEND_REQUEST_DATA) {
            refreshOthersTable();
            refreshToFriendReqTable();
            refreshFromFriendReqTable();
        }
    }
}

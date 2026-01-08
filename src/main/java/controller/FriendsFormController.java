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
import utils.NotificationUtils;
import utils.Services;
import utils.observer.NotificationHandler;
import utils.observer.Observer;

public class FriendsFormController extends BaseController implements Observer {

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
    

    public void initData(User currentUser)
    {

        this.communityService = Services.getCommunityService();
        this.currentUser = currentUser;

        usernameLabel.setText(currentUser.getUsername());

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

    @Override
    protected Stage getStage(){
        return (Stage) root.getScene().getWindow();
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
            stageManager.showErrorAlert(e.getMessage());
        }
    }

    private boolean validSelection(TableView<?> tableView) {
        return tableView.getSelectionModel().getSelectedItem() != null;
    }

    @FXML
    private void removeFriend() {
        if (!validSelection(friendsTable)) {
            stageManager.showErrorAlert("You did not select any friend to remove!");
            return;
        }
        runSafe(() -> {
            User user = friendsTable.getSelectionModel().getSelectedItem();
            communityService.deleteFriendRequest(currentUser.getId(), user.getId());
            communityService.deleteFriend(currentUser.getId(), user.getId());
        });
    }

    @FXML
    private void friendRequest() {
        if (!validSelection(othersTable)) {
            stageManager.showErrorAlert("You did not select any user to send a friend request to!");
            return;
        }
        User user = othersTable.getSelectionModel().getSelectedItem();
        runSafe(() -> communityService.saveFriendRequest(currentUser.getId(), user.getId()));
    }

    @FXML
    private void cancelFriendRequest() {
        if (!validSelection(toFriendReqTable)) {
            stageManager.showErrorAlert("You did not select any friend request to cancel!");
            return;
        }
        FriendRequest fr = toFriendReqTable.getSelectionModel().getSelectedItem();
        runSafe(() -> communityService.cancelFriendRequest(fr));
    }

    @FXML
    private void acceptFriendRequest() {
        if (!validSelection(fromFriendReqTable)) {
            stageManager.showErrorAlert("You did not select any friend request to accept!");
            return;
        }
        FriendRequest fr = fromFriendReqTable.getSelectionModel().getSelectedItem();
        runSafe(() -> communityService.acceptFriendRequest(fr));
    }

    @FXML
    private void denyFriendRequest() {
        if (!validSelection(fromFriendReqTable)) {
            stageManager.showErrorAlert("You did not select any friend request to deny!");
            return;
        }
        FriendRequest fr = fromFriendReqTable.getSelectionModel().getSelectedItem();
        runSafe(() -> communityService.denyFriendRequest(fr));
    }


    private void loadNotifications() {
        String notification = NotificationUtils.getAndClearNotifications(communityService.getNotifications(currentUser.getId()),communityService::deleteNotification);
        if(notification == null || notification.isEmpty())
            return;
        stageManager.showInformationAlert(notification);
    }

    @Override
    public void update(ChangeEvent event) {
        if(event == ChangeEvent.USER_DATA || event == ChangeEvent.FRIENDSHIP_DATA) {
            refreshFriendsTable();
            refreshOthersTable();
        }
        else if(event == ChangeEvent.FRIEND_REQUEST_DATA) {
            refreshOthersTable();
            refreshToFriendReqTable();
            refreshFromFriendReqTable();
        }
        else if(event == ChangeEvent.NOTIFICATION){
            loadNotifications();
        }
    }

}

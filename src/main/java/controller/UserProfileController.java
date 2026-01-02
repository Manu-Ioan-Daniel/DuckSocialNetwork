package controller;

import enums.ChangeEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.User;
import service.ChatService;
import service.CommunityService;
import utils.Services;
import utils.StageManager;
import utils.UiUtils;
import utils.observer.NotificationHandler;
import utils.observer.Observer;


public class UserProfileController implements Observer {

    @FXML
    private Label bottomLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView friendReqBtnImage;

    @FXML
    private Label friendsLabel;

    @FXML
    private Label messagesLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private BorderPane root;

    private CommunityService communityService;
    private ChatService chatService;

    private User currentUser;
    private User loggedInUser;

    private Stage parentStage;

    public void initData(Stage parentStage, User user, User loggedInUser){

        this.communityService = Services.getCommunityService();
        this.chatService = Services.getChatService();
        this.currentUser = user;
        this.parentStage = parentStage;
        this.loggedInUser = loggedInUser;

        root.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if(newScene == null){
                NotificationHandler.getInstance().removeObserver(this);
            }
        });

        loadLabels();
        loadFriendReqBtnImage();
        NotificationHandler.getInstance().addObserver(this);

    }

    private void loadLabels(){
        loadDescriptionLabel();
        loadFriendsLabel();
        loadMessagesLabel();
        usernameLabel.setText(currentUser.getUsername());
        bottomLabel.setText("ducksocialnetwork.com/~" +  currentUser.getUsername());
    }

    private void loadMessagesLabel() {
        messagesLabel.setText(chatService.getMessageCount(currentUser.getId()) + " Messages");
    }

    private void loadFriendsLabel() {
        friendsLabel.setText(communityService.getFriends(currentUser.getId()).size() + " Friends");
    }

    private void loadDescriptionLabel() {
        descriptionLabel.setText(currentUser.getDescription());
    }

    private void loadFriendReqBtnImage() {
        UiUtils.loadFriendReqBtnImage(friendReqBtnImage,communityService.findFriendRequest(currentUser.getId(),loggedInUser.getId()).orElse(null));
    }

    @FXML
    public void handleFriendReq(){
        try {
            communityService.saveFriendRequest(loggedInUser.getId(), currentUser.getId());
        }catch (Exception e){
            StageManager.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    public void handleChatBtn(){
        if(loggedInUser.getId().equals(currentUser.getId())){
            StageManager.showErrorAlert("You cannot chat with yourself!Make some friends :(!");
            return;
        }
        StageManager.showChatWindow(parentStage,loggedInUser.getUsername(), currentUser.getUsername());
    }

    @Override
    public void update(ChangeEvent event) {
        if(ChangeEvent.MESSAGE_EVENT == event){
            loadMessagesLabel();
        }else if(ChangeEvent.FRIENDSHIP_DATA == event || ChangeEvent.SENT_FRIEND_REQUEST == event || ChangeEvent.FRIEND_REQUEST_DATA == event){
            loadFriendsLabel();
            loadFriendReqBtnImage();
        }else if (ChangeEvent.USER_DATA == event){
            if(communityService.findUser(currentUser.getUsername()).isEmpty())
                ((Stage) root.getScene().getWindow()).close();

        }
    }

}

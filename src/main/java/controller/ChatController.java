package controller;

import models.Message;
import enums.ChangeEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.ChatService;
import utils.Services;
import utils.StageManager;
import utils.UiChatUtils;
import utils.observer.NotificationHandler;
import utils.observer.Observer;


public class ChatController implements Observer {

    @FXML
    private BorderPane root;

    @FXML
    private VBox usernamesBox;

    @FXML
    private VBox messagesBox;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField messageField;

    @FXML
    private ScrollPane messagesScrollPane;

    @FXML
    private Label replyMessageLabel;


    private Message repliedToMessage;
    private ChatService chatService;

    public void initData(String username) {
        this.chatService = Services.getChatService();
        usernameLabel.setText(username);
        NotificationHandler.getInstance().addObserver(this);
        messagesScrollPane.vvalueProperty().bind(messagesBox.heightProperty());
        loadUsernames();
    }

    private void loadUsernames(){
        usernamesBox.getChildren().clear();
        UiChatUtils.addUsernamesToVBox(usernamesBox,chatService.getUsernames().stream().filter(username->!usernameLabel.getText().equals(username)).toList(),this::loadConversation);

    }
    private void loadConversation(){
        messagesBox.getChildren().clear();
        UiChatUtils.renderMessages(messagesBox,chatService.buildChatItems(usernameLabel.getText(),getSelectedUsername()),this::setRepliedToMessage);
        replyMessageLabel.setVisible(false);
    }

    @FXML
    public void sendMessage(){
        if(messageField.getText().isEmpty()){
            return;
        }

        chatService.saveMessage(usernameLabel.getText(),getSelectedUsername(), messageField.getText(),isReplying() ? repliedToMessage : null);
        replyMessageLabel.setVisible(false);
        messageField.clear();
    }

    private boolean isReplying(){
        return replyMessageLabel.isVisible();
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

    @FXML
    public void handleFriendsWindow(){
        NotificationHandler.getInstance().removeObserver(this);
        Stage stage = (Stage) root.getScene().getWindow();
        StageManager.showFriendsWindow(stage,usernameLabel.getText());
    }

    private String getSelectedUsername(){
        return UiChatUtils.getSelectedUsername(usernamesBox);
    }

    private void loadNotifications(){
        if(chatService.getLastFriendRequest().getId().getSecond().equals(chatService.getUser(usernameLabel.getText()).getId())){
            StageManager.showInformationAlert("You just received a friend request!");
        }
    }

    public void setRepliedToMessage(Message message){
        repliedToMessage = message;
        replyMessageLabel.setText("Replying to : " + message.getMessage());
        replyMessageLabel.setVisible(true);
        scrollToBottom();
    }

    private void scrollToBottom(){
        messagesScrollPane.vvalueProperty().unbind();
        messagesScrollPane.vvalueProperty().set(1);
        messagesScrollPane.vvalueProperty().bind(messagesBox.heightProperty());
    }

    @Override
    public void update(ChangeEvent event) {
        if(event.equals(ChangeEvent.USER_DATA)){
            loadUsernames();
        }else if(event.equals(ChangeEvent.MESSAGE_EVENT)){
            loadConversation();
        }else if(event.equals(ChangeEvent.SENT_FRIEND_REQUEST)){
            loadNotifications();
        }
    }
}

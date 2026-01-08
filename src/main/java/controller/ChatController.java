package controller;

import javafx.stage.Stage;
import models.Message;
import enums.ChangeEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.User;
import service.ChatService;
import utils.NotificationUtils;
import utils.Services;
import utils.StageManager;
import utils.UiChatUtils;
import utils.observer.NotificationHandler;
import utils.observer.Observer;


public class ChatController extends BaseController implements Observer {

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

    public void initData(User currentUser) {
        this.chatService = Services.getChatService();
        this.currentUser = currentUser;

        usernameLabel.setText(currentUser.getUsername());
        loadUsernames();

        NotificationHandler.getInstance().addObserver(this);

        messagesScrollPane.vvalueProperty().bind(messagesBox.heightProperty());


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


    private void loadNotifications() {
        String notifications = NotificationUtils.getAndClearNotifications(chatService.getNotifications(currentUser.getId()),chatService::deleteNotification);
        if(notifications == null || notifications.isEmpty())
            return;
        stageManager.showInformationAlert(notifications);
    }


    @FXML
    private void sendMessage(){
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


    public void setRepliedToMessage(Message message){
        repliedToMessage = message;
        replyMessageLabel.setText("Replying to : " + message.getMessage());
        replyMessageLabel.setVisible(true);
        scrollToBottom();
    }

    public void setSelectedUser(String username2) {
        UiChatUtils.setSelectedUsername(usernamesBox,username2);
        loadConversation();
    }

    private String getSelectedUsername(){
        return UiChatUtils.getSelectedUsername(usernamesBox);
    }

    @Override
    protected Stage getStage() {
        return (Stage) root.getScene().getWindow();
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
            loadConversation();
        }else if(event.equals(ChangeEvent.MESSAGE_EVENT)){
            loadConversation();
        }
        else if(event.equals(ChangeEvent.NOTIFICATION)){
            loadNotifications();
        }
    }



}

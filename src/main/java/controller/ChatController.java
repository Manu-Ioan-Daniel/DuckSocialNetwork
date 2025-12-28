package controller;

import enums.ChangeEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    private final ChatService chatService = Services.getChatService();

    public void initData(String username) {
        usernameLabel.setText(username);
        NotificationHandler.getInstance().addObserver(this);
        loadUsernames();
    }

    private void loadUsernames(){
        usernamesBox.getChildren().clear();
        UiChatUtils.addUsernamesToVBox(usernamesBox,chatService.getUsernames().stream().filter(username->!usernameLabel.getText().equals(username)).toList(),this::loadConversation);

    }
    private void loadConversation(){
        messagesBox.getChildren().clear();
        UiChatUtils.renderMessages(messagesBox,chatService.buildChatItems(usernameLabel.getText(),getSelectedUsername()));
    }

    @FXML
    public void sendMessage(){
        if(messageField.getText().isEmpty()){
            return;
        }
        chatService.saveMessage(usernameLabel.getText(),getSelectedUsername(), messageField.getText());
        messageField.clear();
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

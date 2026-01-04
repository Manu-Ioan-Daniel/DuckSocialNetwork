package utils;

import models.*;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class UiChatUtils {

    public static void addUsernamesToVBox(VBox box, List<String> usernames, Runnable action){



        for(String username : usernames){

            ImageView userIcon = new ImageView();
            userIcon.setFitHeight(61);
            userIcon.setFitWidth(58);
            userIcon.setImage(new Image("/images/userIcon3.png"));

            Label usernameLabel = new Label(username);

            HBox usernameBox = new HBox();
            usernameBox.setAlignment(Pos.CENTER_LEFT);
            usernameBox.setPadding(new Insets(2));
            usernameBox.setSpacing(10);
            usernameBox.setUserData(username);
            usernameBox.getChildren().add(userIcon);
            usernameBox.getChildren().add(usernameLabel);
            usernameBox.setOnMouseClicked(e ->{
                for(Node node : box.getChildren()){
                    node.getStyleClass().remove("selected");
                }
                usernameBox.getStyleClass().add("selected");
                action.run();
            });

            box.getChildren().add(usernameBox);
        }
    }

    public static String getSelectedUsername(VBox usernamesBox){
        for(Node node : usernamesBox.getChildren()){
            if(node.getStyleClass().contains("selected")){
                return (String) node.getUserData();
            }
        }
        return null;
    }

    public static void setSelectedUsername(VBox usernamesBox,String username){
        for(Node node : usernamesBox.getChildren()){
            node.getStyleClass().remove("selected");
            if(node.getUserData().equals(username)){
                node.getStyleClass().add("selected");
            }
        }
    }

    private static HBox usernameHeader(String username,boolean isMine){
        ImageView userIcon = new ImageView();
        userIcon.setFitHeight(28);
        userIcon.setFitWidth(28);
        userIcon.setImage(new Image("/images/userIcon3.png"));

        Label usernameLabel = new Label(username);

        HBox usernameHeader =  new HBox();
        usernameHeader.setSpacing(10);
        usernameHeader.setAlignment(Pos.TOP_RIGHT);
        usernameHeader.setNodeOrientation(isMine ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.INHERIT);
        usernameHeader.getChildren().add(userIcon);
        usernameHeader.getChildren().add(usernameLabel);
        return usernameHeader;
    }

    private static VBox createChatBox(ChatHeaderItem ch){

        VBox box = new VBox();
        box.setAlignment(ch.isMine() ? Pos.TOP_RIGHT : Pos.TOP_LEFT);
        box.setSpacing(3);
        box.getChildren().add(usernameHeader(ch.username(),ch.isMine()));
        box.setFillWidth(false);

        return box;
    }

    private static StackPane createMessageRow(ChatMessageItem cm, Consumer<Message> consumer) {

        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(pane, cm.isMine() ?  new Insets(0,35,0,40) : new Insets(0,0,0,40));
        pane.setPadding(new Insets(0,5,0,5));


        Label message = new Label(cm.message().getMessage());
        message.setOnMouseClicked(e->{
            if(e.getClickCount() == 2){
                consumer.accept(cm.message());
            }
        });

        VBox box = new VBox();
        if(cm instanceof ChatReplyMessageItem cr){
            Label reply = new Label(cr.replyMessage());
            reply.getStyleClass().add("reply");
            box.getChildren().add(reply);
        }
        box.getChildren().add(message);
        pane.getChildren().add(box);
        StackPane.setMargin(box,new Insets(0,35,0,0));
        Label time = new Label(cm.message().getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        time.getStyleClass().add("time-label");
        StackPane.setAlignment(time,Pos.BOTTOM_RIGHT);
        pane.getChildren().add(time);

        pane.getStyleClass().add(cm.isMine() ? "sent-message" : "received-message");

        return pane;
    }

    public static void renderMessages(VBox messagesBox, List<ChatItem> chatItems, Consumer<Message> consumer) {
        VBox chatBox = null;
        for(ChatItem chatItem : chatItems){
            if(chatItem instanceof ChatHeaderItem ch){
                chatBox = createChatBox(ch);
                messagesBox.getChildren().add(chatBox);
            }else if (chatItem instanceof ChatMessageItem cm && chatBox != null) {
                chatBox.getChildren().add(createMessageRow(cm,consumer));
            }
        }
    }

}

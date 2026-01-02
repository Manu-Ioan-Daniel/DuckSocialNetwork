package utils;


import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import models.FriendRequest;


public class UiUtils {
   public static boolean checkInputs(TextField... fields){
       boolean valid=true;
       for(TextField field : fields) {
           if(field.getText().isEmpty()){
               addTemporaryStylesheet(field,"text-field-error");
               valid=false;
           }
       }
       return valid;
   }
   public static void addTemporaryStylesheet(Node node,String stylesheet){
       if (!node.getStyleClass().contains(stylesheet)) {
           node.getStyleClass().add(stylesheet);
       }
       PauseTransition pause = new PauseTransition(Duration.seconds(1));
       pause.setOnFinished(e -> node.getStyleClass().remove(stylesheet));
       pause.play();
   }

    public static void loadFriendReqBtnImage(ImageView friendReqBtnImage, FriendRequest friendRequest) {
        if(friendRequest == null){
            Image image = new Image("/images/friendRequest.png");
            friendReqBtnImage.setImage(image);
        }else if (friendRequest.getStatus().equals("pending")){
            Image image = new Image("/images/pendingFriendRequest.png");
            friendReqBtnImage.setImage(image);
        }else if (friendRequest.getStatus().equals("accepted")){
            Image image = new Image("/images/friendIcon.png");
            friendReqBtnImage.setImage(image);
        }
    }
}

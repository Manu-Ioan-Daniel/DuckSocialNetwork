package validation;


import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.util.Duration;

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
}

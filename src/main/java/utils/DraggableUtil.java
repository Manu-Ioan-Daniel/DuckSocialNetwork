package utils;

import javafx.scene.Node;
import javafx.stage.Stage;

public class DraggableUtil {
    private DraggableUtil(){}
    public static void makeDraggable(Node node){
        final Delta dragDelta = new Delta();
        node.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if(newScene == null) return;
            node.setOnMousePressed(e->{
                Stage stage = (Stage) node.getScene().getWindow();
                dragDelta.x = stage.getX() - e.getScreenX();
                dragDelta.y = stage.getY() - e.getScreenY();
            });
            node.setOnMouseDragged(e->{
                Stage stage = (Stage) node.getScene().getWindow();
                stage.setX(e.getScreenX() + dragDelta.x);
                stage.setY(e.getScreenY() + dragDelta.y);
            });
        });

    }

    private static class Delta{
        double x;
        double y;
    }
}

package sample.utils;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import static javafx.geometry.Pos.BASELINE_LEFT;

/**
 * Created by Francisco José A. C. Souza on 30/12/14.
 */
public class Utils {
    public static HBox makeBalloon(String message, boolean fromNetwork) {
        HBox balloonVBox = new HBox();
        Label messageLabel = new Label();

        messageLabel.setMaxWidth(230);

        messageLabel.setText(message);
        messageLabel.setStyle("-fx-background-color: transparent; -fx-padding:10 10 10 18;");
        messageLabel.setTextAlignment(TextAlignment.LEFT);
        messageLabel.setFont(new Font(14));

        if (message.length() > 3)
            messageLabel.setWrapText(true);

        balloonVBox.getChildren().add(messageLabel);
        balloonVBox.setMaxWidth(250);

        if(fromNetwork){
            balloonVBox.setStyle(
                    "-fx-background-image: url('sample/images/balloons/bubbleFromPartner.png'); " +
                    "-fx-background-repeat: stretch; " +
                    "-fx-background-size: stretch; " +
                    "-fx-background-position: center;" +
                    "-fx-padding: 10, 10, 10, 10;"
            );
        }
        else{
            balloonVBox.setStyle(
                    "-fx-background-image: url('sample/images/balloons/bubbleFromMe.png'); " +
                    "-fx-background-repeat: stretch; " +
                    "-fx-background-size: stretch; " +
                    "-fx-background-position: center;" +
                    "-fx-padding: 10, 10, 10, 10;"
            );
        }

        balloonVBox.setAlignment(BASELINE_LEFT);
        return balloonVBox;
    }
}
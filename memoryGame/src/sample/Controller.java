package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import sample.communication.GameCommands;
import sample.communication.SendCommandRunnable;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    @FXML
    Button conectarOponente;
    @FXML
    GridPane imagesGridPane;
    @FXML
    Label pontosPlayer1Label;
    @FXML
    Label pontosPlayer2Label;
    @FXML
    Label vezDoJogadorLabel;

    @FXML
    Label ipAddressLabel;


    @FXML void connectOpponent(ActionEvent actionEvent){
        Singleton.INSTANCE.opponentIPAddress = JOptionPane.showInputDialog(null,
                "Endereço IP do Oponente");
        Singleton.INSTANCE.opponentIMServerPort = Singleton.INSTANCE.getPortNumber();

        Singleton.INSTANCE.sendStart();

        this.conectarOponente.setDisable(true);
        JOptionPane.showMessageDialog(null,
                "Conectando a oponente, por favor, aguarde!");
    }



    @FXML
    public void clickImagesGridPane(Event event){
        if(event.getTarget().getClass().equals(ImageView.class)){

            final ImageView targetImageView;
            String currentOpenedImageId;

            targetImageView = (ImageView) event.getTarget();
            currentOpenedImageId = Singleton.INSTANCE.getCardImagePath(targetImageView);

            if(!Singleton.INSTANCE.rightPairs.contains(currentOpenedImageId)) {
                targetImageView.setImage(new Image(currentOpenedImageId));
            }

            //Testa se é a primeira carta a ser aberta pelo usuário
            if(Singleton.INSTANCE.lastOpenedCard == null){
                Singleton.INSTANCE.lastOpenedCard = targetImageView;

                Singleton.INSTANCE.sendFlip(
                        Singleton.INSTANCE.getGridPaneRowIndexForChildNode(targetImageView),
                        Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(targetImageView));

            }
            else {
                //Se não for a primeira carta clicada, testar se o usuario clicou na mesma 
                if (!Singleton.INSTANCE.lastOpenedCard.equals(targetImageView)) {
                    String lastOpenedCardImageId;
                    lastOpenedCardImageId = Singleton.INSTANCE.getCardImagePath(Singleton.INSTANCE.lastOpenedCard);

                    Singleton.INSTANCE.sendFlip(
                            Singleton.INSTANCE.getGridPaneRowIndexForChildNode(targetImageView),
                            Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(targetImageView));

                    //Checar se as duas cartas tem a mesma f
                    if (lastOpenedCardImageId.equals(currentOpenedImageId)) {

                        if (!Singleton.INSTANCE.rightPairs.contains(currentOpenedImageId)){
                            //Incrementa os pontos do player1 e mostra-os na respectiva label
                            Singleton.INSTANCE.rightPairs.add(currentOpenedImageId);
                            Singleton.INSTANCE.pontosPlayer1++;
                            Singleton.INSTANCE.updatePontosPlayer1();

                            targetImageView.setImage(new Image("sample/images/cards/blank.png"));
                            targetImageView.getStyleClass().clear();
                            targetImageView.getStyleClass().add("correctcard");

                            Singleton.INSTANCE.lastOpenedCard.setImage(new Image("sample/images/cards/blank.png"));
                            Singleton.INSTANCE.lastOpenedCard.getStyleClass().clear();
                            Singleton.INSTANCE.lastOpenedCard.getStyleClass().add("correctcard");
                        }

                        Singleton.INSTANCE.checkWinner();
                        Singleton.INSTANCE.lastOpenedCard = null;

                    }
                    //Se não possuirem a mesma figura, mostra ambas cartas por 2s e as vira de novo.
                    else {

                        if (!Singleton.INSTANCE.rightPairs.contains(lastOpenedCardImageId) && !Singleton.INSTANCE.rightPairs.contains(currentOpenedImageId)) {
                            Timeline timeline;

                            this.imagesGridPane.setDisable(true);
                            timeline = this.makeTimeline(Singleton.INSTANCE.lastOpenedCard, targetImageView);
                            timeline.play();
                        }

                    }
                }
            }
        }
    }

    private Timeline makeTimeline(final ImageView firstCard, final ImageView secondCard){
        EventHandler<ActionEvent> eventHandler;
        KeyFrame keyFrame;

        eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Hora de Virar a Carta");

                GridPane imageGrid = (GridPane) Singleton.INSTANCE.scene.lookup("#imagesGridPane");
                firstCard.setImage(new Image("sample/images/cards/cardback.png"));
                secondCard.setImage(new Image("sample/images/cards/cardback.png"));
                Singleton.INSTANCE.lastOpenedCard = null;
                imageGrid.setDisable(true);
            }
        };

        keyFrame = new KeyFrame(Duration.seconds(2), eventHandler);
        return new Timeline(keyFrame);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        Singleton.INSTANCE.magicNumber = random.nextLong();
        Singleton.INSTANCE.rightPairs = new ArrayList<String>();

        Singleton.INSTANCE.balloons = FXCollections.observableArrayList();

        Singleton.INSTANCE.pontosPlayer1 = 0;
        Singleton.INSTANCE.pontosPlayer2 = 0;

        this.imagesGridPane.setDisable(true);

        for (Node node: this.imagesGridPane.getChildren()){
            node.getStyleClass().addAll("imageview", "imageview:hover");
        }

        //this.messagesListView.setItems(Singleton.INSTANCE.balloons);
        this.ipAddressLabel.setText( String.format("Endereço IP: %s:%d", Singleton.INSTANCE.localIPAddress, Singleton.INSTANCE.localIMServerPort) );
    }
}

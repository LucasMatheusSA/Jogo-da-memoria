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
    // Declaração de funções 
    @FXML
    Button conectarOponente; //Declarando butão do oponente 
    @FXML
    GridPane imagesGridPane; // Declarando Grid das cartas
    @FXML
    Label pontosPlayer1Label; // Declarando variavel para pontos do player 1
    @FXML
    Label pontosPlayer2Label; // Declarando variavel para pontos do player 2
    @FXML
    Label ipAddressLabel; // Declarando variavel de ip

    @FXML void connectOpponent(ActionEvent actionEvent){ // Função para conectar ocm oponente 
        Singleton.INSTANCE.opponentIPAddress = JOptionPane.showInputDialog(null,
                "Endereço IP do Oponente"); // Resgata o edereço Ip do oponente e quarda em opponentIPAddress
        Singleton.INSTANCE.opponentIMServerPort = Singleton.INSTANCE.getPortNumber();// Resgata a porta do oponente em opponentIMServerPort

        Singleton.INSTANCE.sendStart(); // Starta a conexão com o oponente

        this.conectarOponente.setDisable(true); 
        JOptionPane.showMessageDialog(null,
                "Conectando a oponente, por favor, aguarde!"); //mensagem de conexão com o oponente
    }

    @FXML
    public void clickImagesGridPane(Event event){ // função com regras do jogo
        if(event.getTarget().getClass().equals(ImageView.class)){ //Checa se houve evento em alguma das cartas 

            final ImageView targetImageView; 
            String currentOpenedImageId;

            targetImageView = (ImageView) event.getTarget(); //carrega qual imagem teve a interação 
            currentOpenedImageId = Singleton.INSTANCE.getCardImagePath(targetImageView); // carrega imagem com interação do opoente 

            if(!Singleton.INSTANCE.rightPairs.contains(currentOpenedImageId)) { // 
                targetImageView.setImage(new Image(currentOpenedImageId));
            }

            //Testa se é a primeira carta a ser aberta pelo usuário
            if(Singleton.INSTANCE.lastOpenedCard == null){
                Singleton.INSTANCE.lastOpenedCard = targetImageView; // se não houve nenhuma carta aberta ele carrega a carta clicada 

                Singleton.INSTANCE.sendFlip( // e vira ela
                        Singleton.INSTANCE.getGridPaneRowIndexForChildNode(targetImageView),
                        Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(targetImageView));

            }
            else {
                //Se não for a primeira carta clicada, testar se o usuario clicou na mesma 
                if (!Singleton.INSTANCE.lastOpenedCard.equals(targetImageView)) {
                    String lastOpenedCardImageId;
                    lastOpenedCardImageId = Singleton.INSTANCE.getCardImagePath(Singleton.INSTANCE.lastOpenedCard);

                    Singleton.INSTANCE.sendFlip(// e vira ela
                            Singleton.INSTANCE.getGridPaneRowIndexForChildNode(targetImageView),
                            Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(targetImageView));

                    //Checar se as duas cartas tem a mesma face
                    if (lastOpenedCardImageId.equals(currentOpenedImageId)) {

                        if (!Singleton.INSTANCE.rightPairs.contains(currentOpenedImageId)){
                            //Incrementa os pontos do player1 e mostra-os na respectiva label
                            Singleton.INSTANCE.rightPairs.add(currentOpenedImageId);
                            Singleton.INSTANCE.pontosPlayer1++;
                            Singleton.INSTANCE.updatePontosPlayer1();
                            
                            //Deixa marcadas as cartas acertadas 
                            targetImageView.setImage(new Image("sample/images/cards/blank.png"));
                            targetImageView.getStyleClass().clear();
                            targetImageView.getStyleClass().add("correctcard");

                            Singleton.INSTANCE.lastOpenedCard.setImage(new Image("sample/images/cards/blank.png"));
                            Singleton.INSTANCE.lastOpenedCard.getStyleClass().clear();
                            Singleton.INSTANCE.lastOpenedCard.getStyleClass().add("correctcard");
                        }

                        Singleton.INSTANCE.checkWinner();// checa se ganhou 
                        Singleton.INSTANCE.lastOpenedCard = null; // Limpa o campo de lastOponenteCard

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

        this.ipAddressLabel.setText( String.format("Endereço IP: %s:%d", Singleton.INSTANCE.localIPAddress, Singleton.INSTANCE.localIMServerPort) );
    }
}

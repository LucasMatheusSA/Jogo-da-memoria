package sample;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import sample.communication.GameCommands;
import sample.communication.SendCommandRunnable;

import javax.swing.*;
import java.util.ArrayList;


public enum Singleton {
    INSTANCE;
    public Scene scene;

    public String opponentIPAddress = null;
    public String opponentName = "Player 02";

    public int opponentIMServerPort = 0;

    public String localIPAddress;
    public String localPlayerName;
    public int localIMServerPort;

    public ArrayList<String> imagesIds;
    public ArrayList<String> rightPairs;
    public ObservableList<HBox> balloons;

    public ImageView lastOpenedCard = null;
    public int pontosPlayer1;
    public int pontosPlayer2;
    
    public String JogadorDaVez;

    public long magicNumber;
    public boolean startSent = false;
    
    public String vezDoJogadorLabel;



    public String getCardImagePath(ImageView targetImageView){
        int currentOpenedCardRowIndex;
        int currentOpenedCardColumnIndex;
        int currentOpenedCardVectorCardsIdsIndex;

        currentOpenedCardRowIndex = Singleton.INSTANCE.getGridPaneRowIndexForChildNode(targetImageView);
        currentOpenedCardColumnIndex = Singleton.INSTANCE.getGridPaneColumnIndexForChildNode(targetImageView);
        currentOpenedCardVectorCardsIdsIndex = currentOpenedCardRowIndex * 6 + currentOpenedCardColumnIndex;

        return Singleton.INSTANCE.imagesIds.get(currentOpenedCardVectorCardsIdsIndex);

    }

    

    public int getGridPaneRowIndexForChildNode(Node targetNode){
        int rowIndex;

        try {
            rowIndex = GridPane.getRowIndex(targetNode).intValue();
        }catch (NullPointerException nullPointerException){
            rowIndex = 0;
        }

        return rowIndex;
    }

    public int getGridPaneColumnIndexForChildNode(Node targetNode){
        int columnIndex;

        try {
            columnIndex = GridPane.getColumnIndex(targetNode).intValue();
        }catch (NullPointerException nullPointerException){
            columnIndex = 0;
        }

        return columnIndex;
    }

    public void updatePontosPlayer1(){

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label pontosPlayer1Label = (Label) Singleton.INSTANCE.scene.lookup("#pontosPlayer1Label");
                if (Singleton.INSTANCE.localPlayerName.length() > 10)
                    pontosPlayer1Label.setText(String.format("%s...: %03d", Singleton.INSTANCE.localPlayerName.substring(0, 10), Singleton.INSTANCE.pontosPlayer1));
                else
                    pontosPlayer1Label.setText(String.format("%s: %03d", Singleton.INSTANCE.localPlayerName, Singleton.INSTANCE.pontosPlayer1));

            }
        });

    }

    public void updatePontosPlayer2(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label pontosPlayer2Label = (Label) Singleton.INSTANCE.scene.lookup("#pontosPlayer2Label");
                if (Singleton.INSTANCE.localPlayerName.length() > 10)
                    pontosPlayer2Label.setText(String.format("%s...: %03d", Singleton.INSTANCE.opponentName.substring(0, 10), Singleton.INSTANCE.pontosPlayer2));
                else
                    pontosPlayer2Label.setText(String.format("%s: %03d", Singleton.INSTANCE.opponentName, Singleton.INSTANCE.pontosPlayer2));

            }
        });
    }

    
    public void updateVezDoJogador(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label vezDoJogadorLabel = (Label) Singleton.INSTANCE.scene.lookup("#vezDoJogadorLabel");
                if (Singleton.INSTANCE.localPlayerName.length() > 10)
                    vezDoJogadorLabel.setText(String.format("%s...: %03d", Singleton.INSTANCE.opponentName.substring(0, 10), Singleton.INSTANCE.localPlayerName));
                else
                    vezDoJogadorLabel.setText(String.format("%s: %03d", Singleton.INSTANCE.opponentName, Singleton.INSTANCE.localPlayerName));
                
            }
        });
    }
    
    public int getPortNumber(){
        boolean isOK = true;
        int portNumber = 0;

        do{
            try{
                portNumber = Integer.parseInt(JOptionPane.showInputDialog(null,
                        "Qual a porta do servidor de mensagens?"));

            }catch (NumberFormatException e){
                isOK = false;
            }
        }while(!isOK);

        return portNumber;
    }

    public void sendStart() {
        SendCommandRunnable sendCommandRunnable;
        Thread thread;
        String message;

        message = String.format("%s %s %d %s %s",
                GameCommands.START.toString(),
                Singleton.INSTANCE.localIPAddress,
                Singleton.INSTANCE.localIMServerPort,
                Long.toString(Singleton.INSTANCE.magicNumber),
                Singleton.INSTANCE.localPlayerName);

        System.out.println("sent message: " + message);

        sendCommandRunnable = new SendCommandRunnable(message);
        thread = new Thread(sendCommandRunnable);
        thread.start();
        Singleton.INSTANCE.startSent = true;
    }

    public void sendFlip(int rowIndex, int columnIndex){
        String flipMessage;
        SendCommandRunnable flipRunnable;

        flipMessage = String.format("FLIP %d %d", rowIndex, columnIndex);
        flipRunnable = new SendCommandRunnable(flipMessage);
        Thread t = new Thread(flipRunnable);
        t.start();
    }

    public void sendDeck(){
        String message;
        SendCommandRunnable sendCommandRunnable;
        Thread thread;

        message = String.format("%s ", GameCommands.DECK.toString());
        for(String cardPath : Singleton.INSTANCE.imagesIds){
            message = message.concat(String.format("%s ", cardPath));
        }

        sendCommandRunnable = new SendCommandRunnable(message);
        thread = new Thread(sendCommandRunnable);
        thread.start();
    }

    /*
     * Shared Game Methods
     */

    public void checkWinner(){
        if(Singleton.INSTANCE.pontosPlayer1 + Singleton.INSTANCE.pontosPlayer2 == 12){
            String message;

            if (Singleton.INSTANCE.pontosPlayer1 > Singleton.INSTANCE.pontosPlayer2){
                message = String.format("Parabéns, %s", Singleton.INSTANCE.localPlayerName);
            }
            else {
                if (Singleton.INSTANCE.pontosPlayer1 < Singleton.INSTANCE.pontosPlayer2) {
                    message = String.format("Parabéns, %s", Singleton.INSTANCE.opponentName);

                }
                else {
                    message = String.format("%s e %s empataram!");
                }
            }

            JOptionPane.showMessageDialog(null, message);
        }
    }
}

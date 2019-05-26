package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.communication.CommandServerRunnable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main extends Application {
    private static CommandServerRunnable messagesServerRunnable;

    @Override
    public void start(Stage primaryStage) throws Exception{
        setup();
        bootIMServer();

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Singleton.INSTANCE.scene = new Scene(root);
        Singleton.INSTANCE.scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());

        Singleton.INSTANCE.updatePontosPlayer1();
        Singleton.INSTANCE.updatePontosPlayer2();
        
        Singleton.INSTANCE.updateVezDoJogador();

        primaryStage.setTitle("Jogo da Memoria");
        primaryStage.setScene(Singleton.INSTANCE.scene);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                messagesServerRunnable.cancel();
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.show();
    }


    private static void setup(){


        Singleton.INSTANCE.localPlayerName = JOptionPane.showInputDialog(null, 
                "Qual o seu nome?");

        Singleton.INSTANCE.localIPAddress = JOptionPane.showInputDialog(null,
                "Qual o seu endereço IP??");
        Singleton.INSTANCE.localIMServerPort = Singleton.INSTANCE.getPortNumber();
        Singleton.INSTANCE.imagesIds = new ArrayList<String>();

        for (int i = 1; i<13; i++){
            Singleton.INSTANCE.imagesIds.add(String.format("sample/images/cards/card%02d.png", i));
            Singleton.INSTANCE.imagesIds.add(String.format("sample/images/cards/card%02d.png", i));
        }

        Collections.shuffle(Singleton.INSTANCE.imagesIds, new Random());
    }

    private static void bootIMServer(){
        messagesServerRunnable = new CommandServerRunnable();
        Thread messagesServerThread = new Thread(messagesServerRunnable);
        messagesServerThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

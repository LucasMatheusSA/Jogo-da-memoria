package sample.communication;

import javafx.concurrent.Task;
import sample.Singleton;
import sample.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class SendCommandRunnable extends Task<Void> {
    Socket socketToServer;
    String messageToSent;

    public SendCommandRunnable(String message) {
        this.messageToSent = message;
    }

    @Override
    protected Void call() throws Exception {
        try {
            this.socketToServer = new Socket(Singleton.INSTANCE.opponentIPAddress, Singleton.INSTANCE.opponentIMServerPort); // cria uma conexao 
            DataOutputStream dataOutputStream = new DataOutputStream(this.socketToServer.getOutputStream()); // cria um novo objeto para escrita de dados 
            dataOutputStream.writeUTF(this.messageToSent); //Grava uma string no fluxo de saída subjacente

            dataOutputStream.close(); // fecha a mensagens escrita 
            this.socketToServer.close(); // encerra a conexao 
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void succeeded(){
        super.succeeded();
        int messageFirstBlankSpaceIndex;
        String command;
        messageFirstBlankSpaceIndex = this.messageToSent.indexOf(" "); //Retorna o índice dentro dessa sequência da primeira ocorrência da subseqüência especificada.
        command = this.messageToSent.substring(0, messageFirstBlankSpaceIndex);//salva a mensagens ate a primeira palavra 

    }

    @Override
    protected void failed(){ // teste fe falha , joga uma exeção em caso de falha 
        super.failed();

        getException().printStackTrace();

    }
}

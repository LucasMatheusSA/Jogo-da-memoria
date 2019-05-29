package sample.communication;

import javafx.concurrent.Task;
import sample.Singleton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//variaveis 
public class CommandServerRunnable extends Task<Void>{
    private ServerSocket serverSocket; // responsável por atender pedidos via rede e em determinada porta
    private Socket socket;// manter a comunicação entre o cliente e o servidor
    private boolean alive;

    public CommandServerRunnable(){
        this.alive = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    protected Void call() throws Exception {
        try {

            this.serverSocket = new ServerSocket(Singleton.INSTANCE.localIMServerPort);// cria uma conexão e passa a porta padrao, informada pelo usuarios
            Singleton.INSTANCE.localIMServerPort = this.serverSocket.getLocalPort();// salva a porta criada para o singleton para poder ficar solicitando dela 

            System.out.println("IM Server no Ar...\n");
            System.out.println(String.format("Endereço IP: %s", Singleton.INSTANCE.localIPAddress));// exibe o IP 
        }catch (IOException e){
            e.printStackTrace();
        }

        while(alive){
            try {
                socket = serverSocket.accept(); // irá tratar da comunicação com o cliente, assim que um pedido de conexão chegar ao servidor e a conexão for aceita
                Thread updateMessagesHistory = new Thread(new ManageMessage(this.socket)); // cria uma thread para mensagens
                updateMessagesHistory.start(); // starta a thread de mensagens
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


}

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
            this.socketToServer = new Socket(Singleton.INSTANCE.opponentIPAddress, Singleton.INSTANCE.opponentIMServerPort);
            DataOutputStream dataOutputStream = new DataOutputStream(this.socketToServer.getOutputStream());
            dataOutputStream.writeUTF(this.messageToSent);

            dataOutputStream.close();
            this.socketToServer.close();
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
        messageFirstBlankSpaceIndex = this.messageToSent.indexOf(" ");
        command = this.messageToSent.substring(0, messageFirstBlankSpaceIndex);

        if(command.equals(GameCommands.MSG.toString()))
            Singleton.INSTANCE.balloons.add(Utils.makeBalloon(this.messageToSent.substring(messageFirstBlankSpaceIndex + 1), false));
    }

    @Override
    protected void failed(){
        super.failed();

        getException().printStackTrace();

    }
}

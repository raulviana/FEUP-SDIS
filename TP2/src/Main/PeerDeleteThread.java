package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PeerDeleteThread extends Thread {

    private int port;
    private String peerIP = "localhost";
    private String filename;

    public PeerDeleteThread(String port, String fileName) {
        this.port = Integer.parseInt(port);
        this.filename = fileName;
    }

    @Override
    public void run() {
        try {
            
            Socket peerSocket = new Socket(peerIP, port); 
        
            DataOutputStream peerOutput = new DataOutputStream(peerSocket.getOutputStream());
            peerOutput.writeUTF("DELETEFILE " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


import java.io.IOException;
import java.net.*;


public class Server{
   
    public static void main(String[] args) throws IOException{
        if(args.length != 1)
            System.out.println("Usage: java Server <port number> ");

        int port = Integer.parseInt(args[2]);

        DatagramSocket socket = new DatagramSocket(port);
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, adress, port);

        socket.send(packet);
    } 
}


import java.io.IOException;
import java.net.*;


public class Server{
   
    public static void main(String[] args) throws IOException{
        if(args.length != 1){
            System.out.println("Usage: java Server <port number> ");
            System.exit(1);
        }
         

        int port = Integer.parseInt(args[0]);
        byte[] request = new byte[256];

        DatagramSocket socket = new DatagramSocket(port);
        DatagramPacket packet = new DatagramPacket(request, request.length);

        socket.receive(packet);
        String pedido = new String(request, 0, packet.getLength());
        System.out.println("pedido: " + pedido);

        socket.close();
    } 
}
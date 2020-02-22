

import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException{
        if(args.length != 4)
            System.out.println("Usage: java Client <host> <port> <oper> <opnd>");
        
        //Prepare message
        int port = Integer.parseInt(args[1]);
        InetAddress adress = InetAddress.getByName(args[0]);
        String message = args[2] + " " + args[3];
        System.out.println(message);
        byte[] sbuf = message.getBytes();
        
        
        
        DatagramSocket socket = new DatagramSocket(port, adress);
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, adress, port);

   // socket.send(packet);

        socket.close();
    }
}


import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException{
        if(args.length != 4){
            System.out.println("Usage: java Client <host> <port> <oper> <opnd>");
            System.exit(1);
        }
        

        //Prepare message
        int port = Integer.parseInt(args[1]);
        InetAddress adress = InetAddress.getByName(args[0]);
        String message = args[2] + " " + args[3];
        byte[] sbuf = message.getBytes();
        byte[] rbuf = new byte[256];
        
        
        
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, adress, port);

        socket.send(packet);
        
        
        DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);
        socket.receive(rpacket);
        String response = new String(rbuf, 0, packet.getLength());

        socket.close();

        System.out.println("Client: " + args[2] + " " + args[3] + " : " + response);

    }
}


import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException{
        if(args.length != 4){
            System.out.println("Usage: java Client <mcast_addr> <mcast_port> <oper> <opnd>");
            System.exit(1);
        }
        

        //Prepare message
    //try{

        int multicast_port = Integer.parseInt(args[1]);
        InetAddress multicast_adress = InetAddress.getByName(args[0]);
        String message = args[2] + " " + args[3];
        byte[] sbuf = message.getBytes();
        byte[] rbuf = new byte[256];
        byte[] mbuf = new byte[256];
        
        
        MulticastSocket msocket = new MulticastSocket();
        DatagramPacket mpacket = new DatagramPacket(mbuf, mbuf.length);
        msocket.joinGroup(multicast_adress);
        msocket.setLoopbackMode(true);
        msocket.setTimeToLive(1);
        msocket.receive(mpacket);
        String serverIP = new String(rbuf, 0, mpacket.getLength());
        System.out.println("Server IP: " + serverIP);

        msocket.close();
    }
        /*
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, adress, port);

        socket.send(packet);
        
        
        DatagramPacket rpacket = new DatagramPacket(rbuf, rbuf.length);
        socket.receive(rpacket);
        String response = new String(rbuf, 0, packet.getLength());

        socket.close();

        System.out.println("Client: " + args[2] + " " + args[3] + " : " + response);
    }
    catch(SocketException ex){
        System.out.println("Timeout error: " + ex.getMessage());
    }
    catch(IOException ex){
        System.out.println("Client error: " + ex.getMessage());
    }
    }*/
}
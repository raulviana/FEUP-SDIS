

import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException{
        if(args.length != 4){
            System.out.println("Usage: java Client <mcast_addr> <mcast_port> <oper> <opnd>");
            System.exit(1);
        }
        

        //Link to multicast group
        int multicast_port = Integer.parseInt(args[1]);
        InetAddress multicast_adress = InetAddress.getByName(args[0]);
        byte[] mbuf = new byte[256];
        byte[] rbuf = new byte[256];

     try{
        MulticastSocket socket = new MulticastSocket(multicast_port);
        DatagramPacket packet = new DatagramPacket(mbuf, mbuf.length);
        socket.joinGroup(multicast_adress);
        socket.setLoopbackMode(true);
        socket.setTimeToLive(1);

        socket.receive(packet);

        socket.leaveGroup(multicast_adress);
        

        String response = new String(mbuf, 0, packet.getLength());
        int serverPort = packet.getPort();
        InetAddress serverIP = packet.getAddress();
        socket.close();
       
       
        String request = args[2] + " " + args[3];
        byte[] sbuf = request.getBytes();
       
        DatagramSocket rsocket = new DatagramSocket();
        DatagramPacket rpacket = new DatagramPacket(sbuf, sbuf.length, serverIP, serverPort);

        rsocket.send(rpacket);
    
   
        
        DatagramPacket apacket = new DatagramPacket(rbuf, rbuf.length);
        rsocket.receive(apacket);
        String server_reply = new String(rbuf, 0, apacket.getLength());

        rsocket.close();

        System.out.println("Client: " + args[2] + " " + args[3] + " : " + response);
    }
    catch(SocketException ex){
        System.out.println("Timeout error: " + ex.getMessage());
    }
    catch(IOException ex){
        System.out.println("Client error: " + ex.getMessage());
    }
    }
}
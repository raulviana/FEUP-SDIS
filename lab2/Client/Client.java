

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
        byte[] sbuf = new byte[256];

        MulticastSocket socket = new MulticastSocket(multicast_port);
        DatagramPacket packet = new DatagramPacket(mbuf, mbuf.length);
        socket.joinGroup(multicast_adress);
        socket.setLoopbackMode(true);
        socket.setTimeToLive(1);

        socket.receive(packet);

        socket.leaveGroup(multicast_adress);
        

        String response = new String(mbuf, 0, packet.getLength());
        InetAddress serverPort = InetAddress.getByName(response);
        InetAddress serverĨP = packet.getAddress();
        socket.close();
        System.out.println(serverĨP.getHostAddress());





     //   byte[] sbuf = message.getBytes();
     //  
        
    
        /*
        

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
    }*/
    }
}
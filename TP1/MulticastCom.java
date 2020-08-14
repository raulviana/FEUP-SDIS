
import java.io.IOException;
import java.net.*;
import java.util.Arrays;


public class MulticastCom implements Runnable {

    private int PORT;
    private InetAddress adress;

    public MulticastCom(String IAdress, int port){
        
        try{
            PORT = port;
            adress = InetAddress.getByName(IAdress);
        }
        catch(UnknownHostException e){
            e.printStackTrace();
        }
    }


    public void sendMessage(byte[] message){
        try{
            DatagramSocket send_socket = new DatagramSocket();
            DatagramPacket out_packet = new DatagramPacket(message, message.length, adress, PORT);
            send_socket.send(out_packet);
        }
        catch (IOException e){ 
            e.printStackTrace();
        }
    }

    public void run() {
        
        byte[] buffer = new byte[65000];

        try{
            
            MulticastSocket receiveSocket = new MulticastSocket(PORT);
            
            receiveSocket.joinGroup(adress);
            receiveSocket.setLoopbackMode(true);
            receiveSocket.setTimeToLive(1);
            
            while(true){
                
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                receiveSocket.receive(packet);
                byte[] receivedMessage = Arrays.copyOf(buffer, packet.getLength());
                Peer.getExecutor().execute(new IncomingManager(receivedMessage));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
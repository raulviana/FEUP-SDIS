

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Arrays;
import java.util.concurrent.*;



public class Server{
   
    public static void main(String[] args) throws IOException{
        if(args.length != 3){
            System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port> ");
            System.exit(1);
        }
        

        //Preparing 
        HashMap<String, String> dns = new HashMap<>();
        int port = Integer.parseInt(args[0]);
        InetAddress madress = InetAddress.getByName(args[1]);
        InetAddress serverIP = InetAddress.getLocalHost();
        int mport = Integer.parseInt(args[2]);
        String multicast_message = args[0];
        byte[] request = new byte[256];
        
        

        byte[] mbuf = multicast_message.getBytes();
        DatagramSocket msocket = new DatagramSocket();
        DatagramPacket mpacket = new DatagramPacket(mbuf, mbuf.length, madress, mport);
        

        ScheduledExecutorService multicast_loop = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
                        try {
                            msocket.send(mpacket);
                            System.out.println("multicast: " + madress.getHostAddress() + " " + mport + " : " + serverIP.getHostAddress() + " " + port);
                        }
                        catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    };
        int initialDelay = 0;
        int period = 1;
        multicast_loop.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);

    while(true){
        try{
        DatagramSocket socket = new DatagramSocket(port);
        DatagramPacket packet = new DatagramPacket(request, request.length);

        socket.receive(packet);
        String pedido = new String(request, 0, packet.getLength());
 
        //list = {request_type, request1, request2_if_exists}
        String[] list = pedido.split(" ", 100);
        String rmessage = new String();
        
        switch (list[0]){
            case "LOOKUP":
                rmessage = dns.get(list[1]);
                
                if(rmessage == null){
                    rmessage = "ERROR";
                }
                System.out.println("Server: " + list[0] + " " + list[1]);
                break;
            case "REGISTER":
                dns.put(list[1], list[2]);
                rmessage = "OK";
                System.out.println("Server: " + list[0] + " " + list[1] + " " + list[2]);
                break;
            default:
                //Must handle error
                System.exit(1);
        }

        InetAddress radress = packet.getAddress();
        int rport = packet.getPort();
        byte[] rdata = rmessage.getBytes();
        DatagramPacket rpacket = new DatagramPacket(rdata, rdata.length, radress, rport);
        socket.send(rpacket);
    
        socket.close();
        }
        catch(SocketException ex){
            System.out.println("Socket error: " + ex.getMessage());
        }
        catch(IOException ex){
            System.out.println("I/0 error: " + ex.getMessage());
        }
    }
    } 
}



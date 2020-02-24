

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Arrays;

public class Server{
   
    public static void main(String[] args) throws IOException{
        if(args.length != 1){
            System.out.println("Usage: java Server <port number> ");
            System.exit(1);
        }
         
        HashMap<String, String> dns = new HashMap<>();
        int port = Integer.parseInt(args[0]);
        byte[] request = new byte[256];
while(true){
        DatagramSocket socket = new DatagramSocket(port);
        DatagramPacket packet = new DatagramPacket(request, request.length);

        socket.receive(packet);
        String pedido = new String(request, 0, packet.getLength());
 
        //List = {request_type, list_of_requests}
        String[] list = pedido.split(" ", 100);
        String rmessage = new String();
        
        switch (list[0]){
            case "LOOKUP":
                rmessage = dns.get(list[1]);
                
                if(rmessage == null){
                    rmessage = "ERROR";
                }
                break;
            case "REGISTER":
                dns.put(list[1], list[2]);
                rmessage = "OK";
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

        System.out.println(Arrays.asList(dns));
    }
    } 
}
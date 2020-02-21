

import java.io.IOException;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException{
        if(args.length != 5)
            System.out.println("Usage: java Client <host> <port> <oper> <opnd>");

        //Prepare message
        DatagramSocket socket = new DatagramSocket();
        
    }
}
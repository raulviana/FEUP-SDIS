
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class App{

    private App() {}

    public static void main(String[] args){
        try{

            if (args.length >= 5 || args.length == 0){
                System.out.println("Error!");
                System.out.println("Usage: java App <peer_access_point> <sub_protocol> <opnd_1> <opnd_2>");
                System.exit(1);
            }

            String remote_object = args[0];
            String protocol = args[1];
            
            Registry registry = LocateRegistry.getRegistry("localhost");
            RMIinterface stub = (RMIinterface) registry.lookup(remote_object);
            
            switch(protocol){
                case "BACKUP":
                    if(args.length != 4){
                        System.out.println("ERROR");
                        System.out.println("Usage: App <peer_access_point> BACKUP <file path> <replication degree>");
                        System.exit(1);
                    }
                    String filePath = args[2];
                    int replication_degree = Integer.parseInt(args[3]);
                    stub.backup(filePath, replication_degree);
                    break;
                case "RESTORE":
                    if(args.length != 3){
                        System.out.println("ERROR");
                        System.out.println("Usage: App <peer_access_point> RESTORE <file path>");
                        System.exit(1);
                    }
                    String file_path = args[2];
                    stub.restore(file_path);
                    break;
                case "DELETE":
                    if(args.length != 3){
                        System.out.println("ERROR");
                        System.out.println("Usage: App <peer_access_point> DELETE <file path>");
                        System.exit(1);
                    }
                    String filepath = args[2];
                    stub.delete(filepath);
                    break;
                case "STATE":
                    stub.state();
                    break;
                case "RECLAIM":
                    int spaceReclaimed = Integer.valueOf(args[2]);
                    stub.reclaim(spaceReclaimed);
                    break;
            }
        }
        catch(Exception e){
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }
}
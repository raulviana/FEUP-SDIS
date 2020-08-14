
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;

//import javax.swing.text.html.HTMLDocument.Iterator;

public class Peer implements RMIinterface {
    private static Double version;
    private static int peer_ID;
    private static String access_Point;
    private static ScheduledThreadPoolExecutor executor;
    private static MulticastCom MC_channel;
    private static MulticastCom MDB_channel;
    private static MulticastCom MDR_channel;
    private static HandleStorage storage;

    public Peer(String MC_control, int MC_control_port, String MDB_data, int MDB_data_port, String MDR_recovery,
            int MDR_recovery_port, int peer_id, String access_P) {

        peer_ID = peer_id;
        access_Point = access_P;
        MC_channel = new MulticastCom(MC_control, MC_control_port);
        MDB_channel = new MulticastCom(MDB_data, MDB_data_port);
        MDR_channel = new MulticastCom(MDR_recovery, MDR_recovery_port);

        // thread pool()
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(25);
    }

    public static ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static String get_accessPoint() {
        return access_Point;
    }

    public static int getPeer_ID() {
        return peer_ID;
    }

    public static HandleStorage getStorage() {
        return storage;
    }

    public static MulticastCom getMC() {
        return MC_channel;
    }

    public static MulticastCom getMDB() {
        return MDB_channel;
    }

    public static MulticastCom getMDR() {
        return MDR_channel;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 9) {
            System.out.println("Error!");
            System.out.println(
                    "Usage: Peer <version> <peer_id> <access_point> <MC_IP_address> <MC_port> <MDB_IP_address> <MDB_port> <MDR_IP_address> <MDR_port>");
            System.exit(1);
        }
        version = Double.parseDouble(args[0]);
        peer_ID = Integer.parseInt(args[1]);
        String accessP = args[2];
        String MC_control = args[3];
        int MC_control_port = Integer.parseInt(args[4]);
        String MDB_data = args[5];
        int MDB_data_port = Integer.parseInt(args[6]);
        String MDR_recovery = args[7];
        int MDR_recovery_port = Integer.parseInt(args[8]);

        try {
            Peer obj = new Peer(MC_control, MC_control_port, MDB_data, MDB_data_port, MDR_recovery, MDR_recovery_port,
                    peer_ID, accessP);

            loadStorage();

            RMIinterface stub = (RMIinterface) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(accessP, stub);

            executor.execute(MC_channel);
            executor.execute(MDB_channel);
            executor.execute(MDR_channel);

            System.err.println("Server " + peer_ID + " ready");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        // Save before shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(Peer::saveStorage));
    }

    public synchronized void backup(String file_path, int replication_degree) {

        FileInfo file = new FileInfo(file_path, replication_degree); // file is splited and chunks are added

        storage.addFile(file);

        for (Chunk chunk : file.getChunks()) {
            chunk.setWantedReplicationDegree(replication_degree);

            String header = "PUTCHUNK " + version + " " + peer_ID + " " + file.getID() + " " + chunk.getID() + " "
                    + chunk.get_RD_asked() + "\r\n\r\n"; // \r\n\r\n equals <CRFL>
            System.out.println("SENT: " + header);

            String code = file.getID() + "_" + String.valueOf(chunk.getID());
            if (!storage.getRemoteOcurrences().containsKey(code)) {
                storage.getRemoteOcurrences().put(code, 0);
            }


        System.out.println("IN backup: " + storage.getRemoteOcurrences().size());

            byte[] msgHeader;
            try {
                msgHeader = header.getBytes("US-ASCII");
                byte[] msgBody = chunk.getData();
                byte[] message = new byte[msgBody.length + msgHeader.length];
                System.arraycopy(msgHeader, 0, message, 0, msgHeader.length);
                System.arraycopy(msgBody, 0, message, msgHeader.length, msgBody.length);
                MDB_channel.sendMessage(message);

                Thread.sleep(500);
                Peer.getExecutor().schedule(
                        new RepeatPutChunkMessage(message, 1, file.getID(), chunk.getID(), replication_degree), 1,
                        TimeUnit.SECONDS);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InterruptedException i) {
                i.printStackTrace();
            }
        }
    }

    public void restore(String file_path) {
        boolean found = false;
        // storage.getProcessingChunks().clear();
        for (int i = 0; i < storage.getFileInfos().size(); i++) {
            if (storage.getFileInfos().get(i).getFile().getPath().equals(file_path)) { // Make sure this is the
                                                                                       // initiator peer
                int size = storage.getFileInfos().get(i).getChunks().size();
                for (int j = 0; j < size; j++) { // Ask for all chunks of this file
                    String header = "GETCHUNK " + String.valueOf(version) + " " + String.valueOf(peer_ID) + " "
                            + storage.getFileInfos().get(i).getID() + " "
                            + String.valueOf(storage.getFileInfos().get(i).getChunks().get(j).getID());
                    String code = storage.getFileInfos().get(i).getID() + "_"
                            + String.valueOf(storage.getFileInfos().get(i).getChunks().get(j).getID());
                    storage.addPeerWantedChunk(code);
                    try {
                        byte[] headerBytes = header.getBytes("US-ASCII");
                        MC_channel.sendMessage(headerBytes);
                        System.out.println("SENT: " + header);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                found = true;
            }
        }
        if (!found)
            System.out.println("ERROR: File not backed up or this is not its initiator peer");
    }

    public void delete(String file_path) {
        int number_ensure_deletion = 6;
        for (int i = 0; i < storage.getFileInfos().size(); i++) {

            if (storage.getFileInfos().get(i).getPath().equals(file_path)) {
                String header = "DELETE " + String.valueOf(version) + " " + String.valueOf(peer_ID) + " "
                        + storage.getFileInfos().get(i).getID() + "\r\n\r\n";

                for (int j = 0; j < number_ensure_deletion; j++) {
                    System.out.println("SEND: " + header);
                    try {
                        MC_channel.sendMessage(header.getBytes("US-ASCII"));

                        Thread.sleep(500);
                        Peer.getExecutor().schedule(new RepeatDeleteMessage(header, 1), 1,
                            TimeUnit.SECONDS);
                    } catch (UnsupportedEncodingException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (int j = 0; j < storage.getFileInfos().get(i).getChunks().size(); j++) {
                    String key = storage.getFileInfos().get(i).getID() + "_"
                            + String.valueOf(storage.getFileInfos().get(i).getChunks().get(j).getID());
                    storage.removeOcurence(key);
                }
                storage.getFileInfos().remove(i);
                break;
            }
        }
    }

    public void state() {
        System.out.println("\n******************************************");
        System.out.println("           " + access_Point + " STATE    ");

        System.out.println("\n---Peer's backed up files---\n");

        if (storage.getFileInfos().size() == 0) {
            System.out.println(" (i) This peer has no files backed up");
        } else {
            for (int i = 0; i < storage.getFileInfos().size(); i++) {

                System.out.println("File pathname: " + storage.getFileInfos().get(i).getFile().getPath());
                System.out.println("File ID: " + storage.getFileInfos().get(i).getID());
                System.out.println("File replication degree: "
                        + String.valueOf(storage.getFileInfos().get(i).getReplicationDegree()));
                System.out.println("\n\t---File's chunks---\n");
                for (int j = 0; j < storage.getFileInfos().get(i).getChunks().size(); j++) {
                    System.out.println("\tChunk ID: " + storage.getFileInfos().get(i).getChunks().get(j).getID());
                    String code = storage.getFileInfos().get(i).getID() + "_"
                            + storage.getFileInfos().get(i).getChunks().get(j).getID();
                    System.out.println(
                            "\tChunk perceived replication degree: " + storage.getRemoteOcurrences().get(code) + "\n");
                }
            }
        }
        System.out.println("------------------");
        System.out.println("-----------------------------");

        System.out.println("\n---Chunks stored in this Peer---\n");
        if (storage.getStoredChunks().size() == 0) {
            System.out.println(" (i) This peer has no stored chunks");
        } else {
            for (int i = 0; i < storage.getStoredChunks().size(); i++) {
                String code = storage.getStoredChunks().get(i).getFileID() + "_"
                        + storage.getStoredChunks().get(i).getID();
                System.out.println("Chunk ID: " + storage.getStoredChunks().get(i).getID());
                System.out.println("Size: " + storage.getStoredChunks().get(i).getSize());
                System.out.println(
                        "Chunk's perceived replication degree: " + storage.getRemoteOcurrences().get(code) + "\n");
            }
        }
        System.out.println("---------------------------------\n");
        System.out.println("******************************************\n");
    }

    private static void saveStorage() {
        try {
            System.out.println("  Shutting down.. ");
            // saves chunk in disk
            String directory = Peer.get_accessPoint();
            Files.createDirectories(Paths.get(directory));
            String filename = directory + "/storage.save";
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(storage);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadStorage() {
        try {
            String filename = Peer.get_accessPoint() + "/storage.save";
            File file = new File(filename);
            if (!file.exists()) {
                storage = new HandleStorage();
                return;
            }

            FileInputStream fileIn = new FileInputStream(filename);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            storage = (HandleStorage) objIn.readObject();
            objIn.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void reclaim(int newSpaceAvailable) {
        System.out.println("RECLAIM: old space - " + storage.getSpaceAvailable());
        System.out.println(storage.getRemoteOcurrences().size());
        int spaceToFree = Peer.storage.getOccupiedSpace() - newSpaceAvailable; 

        if (spaceToFree > 0) {
            storage.fillCurrRDChunks();
            storage.getStoredChunks().sort(Collections.reverseOrder());

            int deletedChunksSpace = 0; 

            for (Iterator<Chunk> iter = storage.getStoredChunks().iterator(); iter.hasNext(); ) {
                Chunk chunk = iter.next();
                if (deletedChunksSpace < spaceToFree) {
                    deletedChunksSpace = deletedChunksSpace + chunk.getSize();

                    String header = "REMOVED " + version + " " + peer_ID + " " + chunk.getFileID() + " " + chunk.getID() + "\r\n\r\n";
                    System.out.println("Sent " + "REMOVED " + version + " " + peer_ID + " " + chunk.getFileID() + " " + chunk.getID());
                    try {
                        MC_channel.sendMessage(header.getBytes("US-ASCII"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String filename = Peer.getPeer_ID() + "/" + chunk.getFileID() + "_" + chunk.getID();
                    File file = new File(filename);
                    file.delete();
                    Peer.getStorage().decStoredOccurrences(chunk.getFileID(), chunk.getID()); //decrements the stored occurrences of this chunk
                    iter.remove();
                } else {
                    break;
                }
            }

            storage.setSpaceAvailable(newSpaceAvailable - storage.getOccupiedSpace()); //updates the available space to the new value minus the space occupied by the chunks that weren't deleted
            System.out.println("RECLAIM: new space - " + storage.getSpaceAvailable());
        }

    }
    
}
    
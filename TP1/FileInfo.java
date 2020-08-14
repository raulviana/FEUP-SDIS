
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;



public class FileInfo implements java.io.Serializable {

    private static final long serialVersionUID = -1022456524366451498L;
    
    private String id;
    private File file;
    private int replicationDegree;
    private ArrayList<Chunk> chunks;

    public FileInfo(String path, int replicationDegree){
        this.file = new File(path);
        this.id = generateID();
        this.replicationDegree = replicationDegree;
        this.chunks = new ArrayList<>();
        splitFile();
    }

    private String generateID(){
        String fileName = this.file.getName();
        String dateSaved = String.valueOf(this.file.lastModified());
        String fileID = fileName + " " + dateSaved;
        //hashing 
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fileID.getBytes("UTF-8"));
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hex_number = new StringBuilder(number.toString(16));
            while(hex_number.length() < 32){
                hex_number.insert(0, '0');
            }
            return hex_number.toString();
        }
        catch(Exception er){
            throw new RuntimeException(er);
        }
        
    }

    void splitFile() {
        int chunk_number = 0;
        int CHUNK_SIZE = 64000;
        byte[] buffer = new byte[CHUNK_SIZE];
        try{
            FileInputStream fileIS = new FileInputStream(this.file);
            BufferedInputStream bufferIS = new BufferedInputStream(fileIS);

            int bytesCount;
            while((bytesCount = bufferIS.read(buffer)) > 0) {
                byte[] body = Arrays.copyOf(buffer, bytesCount);

                chunk_number++;
                Chunk chunk = new Chunk(chunk_number, this.getID(), body, bytesCount);
                this.chunks.add(chunk);
                buffer = new byte[bytesCount];
            }

            fileIS.close();
            bufferIS.close();
            
            //last chunk with 0 length
            if (this.file.length() % CHUNK_SIZE == 0) {
                Chunk chunk = new Chunk(chunk_number,this.getID(), null, 0);
                this.chunks.add(chunk);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public ArrayList<Chunk> getChunks(){
        return this.chunks;
    }

    public String getID(){
        return this.id;
    }

    public String getPath(){
        return this.file.getPath();
    }
    
    public File getFile(){
        return this.file;
    }

    public int getReplicationDegree(){
        return this.replicationDegree;
    }

    public String toString(){
        return "FileInfo\nID: " + this.id + "\npath: " + this.file.toPath() + "\nchunks: " + this.chunks.size();
        
    }

}


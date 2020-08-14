

public class Chunk implements java.io.Serializable{
    
    private static final long serialVersionUID = 7180088430798187294L;
    private int id;
    private byte[] content;
    private String fileID;
    private int replicationDegree_asked;
    private int size;

    public Chunk(int id, String fileID, byte[] content, int size){
        this.id = id;
        this.fileID = fileID;
        this.content = content;
        this.size = size;
    }

    public Chunk(int id, String fileID, int desiredReplicationDegree, int size) {
        this.id = id;
        this.replicationDegree_asked = desiredReplicationDegree;
        this.fileID = fileID;
        this.size = size;
    }

    public int getSize(){
        return this.size;
    }

    public int get_RD_asked(){
        return this.replicationDegree_asked;
    }

    public void setWantedReplicationDegree(int replication_degree){
        this.replicationDegree_asked = replication_degree;
    }

    public int getID(){
        return this.id;
    }

    public String getFileID(){
        return this.fileID;
    } 

    public byte[] getData(){
        return this.content;
    }

    public String toString(){
        return "id: " + this.id + " fileID: " + this.fileID + " size: " + this.size;
        
    }
}
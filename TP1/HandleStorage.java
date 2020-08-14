import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;


public class HandleStorage implements java.io.Serializable{

    private static final long serialVersionUID = 1456L;

    private ArrayList<FileInfo> files;

    private ArrayList<Chunk> storedChunks;

    //storedChunks<String file_id_chunkNR,  Integer >
    private ConcurrentHashMap<String, Integer> remoteOcurrences;
   
    //wantedChunks<String fileID_chunkchunkNR, Strig received: strue/false>
    private ConcurrentHashMap<String, String> peerWantedChunks;

    //chunks received during restore process
    private HashSet<Chunk> processingChunks;
    
    private ConcurrentHashMap<String, Integer> storedOccurrences;

    private int spaceInDisk;

    
    public HandleStorage () {
        this.files = new ArrayList<>();
        this.storedChunks = new ArrayList<>();
        this.remoteOcurrences = new ConcurrentHashMap<>();
        this.peerWantedChunks = new ConcurrentHashMap<>();
        this.processingChunks = new HashSet<Chunk>();
        this.spaceInDisk = 1000000;
    }

    public void addFile(FileInfo file){
        boolean found = false;
        
        for(int i = 0; i < this.files.size(); i++){
            if(isSameFile(this.files.get(i), file)){
                found = true;
            }
        }
        if(! found) {
            this.files.add(file);
        }
    }

    public int getFilesNumber() {
        return this.files.size();
    }

    public ArrayList<FileInfo> getFileInfos(){
        return files;
    }

    public ArrayList<Chunk> getStoredChunks(){
        return this.storedChunks;
    }

    public ConcurrentHashMap<String, Integer> getRemoteOcurrences(){
        return this.remoteOcurrences;
    }

    public synchronized int getSpaceAvailable(){
        return this.spaceInDisk;
    }

    public synchronized void setSpaceAvailable(int spaceInDisk) {
        this.spaceInDisk = spaceInDisk;
    }

    public synchronized int getOccupiedSpace(){
        int occupiedSpace = 0;
        for (Chunk storedChunk : this.storedChunks) {
            occupiedSpace = occupiedSpace + storedChunk.getSize();
        }
        return occupiedSpace;
    }

    public synchronized HashSet<Chunk> getProcessingChunks(){
        return this.processingChunks;
    }
    
    public synchronized  boolean addToStoredChunks(Chunk chunk){

        for (int i = 0; i < storedChunks.size(); i++){

            if (storedChunks.get(i).getID() == chunk.getID() && storedChunks.get(i).getFileID().equals(chunk.getFileID())){
                return false;
            } 
        }
        this.storedChunks.add(chunk);
        return true;
    }

    public synchronized void decAvailableSpace(int size){
        this.spaceInDisk -= size;
    }

    public synchronized void addAvaiableSpace(int size){
        this.spaceInDisk += size;
    }
    
    public synchronized void addRemote(String fileID, String chunkNR){
        String code = fileID + "_" + chunkNR;
        if(! Peer.getStorage().getRemoteOcurrences().containsKey(code)){
            Peer.getStorage().getRemoteOcurrences().put(code, 1);
        }
        else{
            int ocurrences = this.remoteOcurrences.get(code);
            ocurrences += 1;
            this.remoteOcurrences.replace(code, ocurrences);
        }
    }

    public synchronized void addPeerWantedChunk(String fileID_ChunkNR){
        this.peerWantedChunks.put(fileID_ChunkNR, "false");
    }

    public void addProcessingChunks(Chunk chunk){
        
        this.processingChunks.add(chunk);
    }

	public void removeOcurence(String key) {
        this.remoteOcurrences.remove(key);
	}

	public void deleteChunks(String fileID) {
        for(int i = 0; i < this.storedChunks.size(); i++){
            if(fileID.equals(this.storedChunks.get(i).getFileID())){
                String filename = Peer.get_accessPoint() + "/" + fileID + "_" + this.storedChunks.get(i).getID();
                File file = new File(filename);
                file.delete();
                String code = fileID + "_" + String.valueOf(this.storedChunks.get(i).getID());
                addAvaiableSpace(this.storedChunks.get(i).getSize());
                removeOcurence(code);
                this.storedChunks.remove(i);
                i--;
            }
        }
	}

    public void cleanProcessingChunks(){
        this.processingChunks.clear();
    }

    public boolean isSameFile(FileInfo f1, FileInfo f2){
        return f2.getID().equals(f1.getID());
    }

    public synchronized void decStoredOccurrences(String fileID, int chunkNr) {
        String fileID_ChunkNR = fileID + "_" + String.valueOf(chunkNr);
        int real_number = this.getRemoteOcurrences().get(fileID_ChunkNR) - 1;
        this.getRemoteOcurrences().replace(fileID_ChunkNR, real_number);
    }

    public void fillCurrRDChunks() {
       System.out.println(this.remoteOcurrences.size());
        for (Chunk storedChunk : this.storedChunks) {
            String fileID_Chunkid  = storedChunk.getFileID() + "_" + String.valueOf(storedChunk.getID());
            
            storedChunk.setWantedReplicationDegree(this.remoteOcurrences.get(fileID_Chunkid));
        }
    }
}

package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

import Main.Peer;

public class FileUtil {

    /**
     * Split a file into a list of byte[]
     * @param fileName The name of the file to split
     * @param size Size of each parts 
     * @return The list of the byte[].
     * @throws Exception 
     */
    public static List<byte[]> split(String fileName, int size) throws Exception {
        //read the file
        byte[] fileContent = Files.readAllBytes(new File(fileName).toPath());
        //System.out.println("Splitter: " + fileContent.length);
        List<byte[]> res = new ArrayList<>();
        if(fileContent.length<size) {
            res.add(fileContent);
            return res;
        }
        
        float dparts = (float)fileContent.length/(float)size;
        int osize = size; //original size
        int parts = (int)Math.ceil(dparts); //number of complete parts

        double remainder = dparts-(parts-1); //percentage of the last part;
                
        //if the file is smaller than the part size, use just one part
        if(fileContent.length<size) {
            size = fileContent.length;
            parts = 1;
        }
        int i;
        for(i=0;i<parts;i++) {
            if(i==parts-1) { //adjust the size of the last part
                size = (int)(size*remainder) ;
            }
            byte[] part = new byte[size];
            
            System.arraycopy(fileContent, i*osize, part, 0, size);
            res.add(part);
        }
        
        if(fileContent.length>size && fileContent.length%size==0) {
            res.add(new byte[0]);
        }
        
        return res;     
    }
        
    /**
     * Save a file to disk. 
     * @param content The content of the file to save
     * @param filename The name of the file
     * @throws IOException 
     */
    public static void save(byte[] content, String filename) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(new File(filename))) {
            stream.write(content);
        }
    }
    
    /**
     * Read a file into a byte[]
     * @param name The name of the file
     * @return The read byte[]
     * @throws IOException 
     */
    public static byte[] read(String name) throws IOException {
        return Files.readAllBytes(new File(name).toPath());
    }
    
    /**
     * Merge a list of byte[] into a single byte[]
     * @param parts
     * @return 
     */
    public static byte[] merge(List<byte[]> parts) {
        int total = 0;
        //calculate the length of the final file
        for(byte[] b : parts) {
            total += b.length;
        }
        
        //allocate in memory space for the new file
        byte[] res = new byte[total];
        
        
        int index = 0;
        //read all parts
        for(int i=0;i<parts.size();i++) {
            System.arraycopy(parts.get(i), 0, res, index, parts.get(i).length);
            index += parts.get(i).length;
        }        
        return res;
    }
     
    /**
     * Generate a unique file ID
     * @param filename
     * @param version
     * @return 
     */ 
    /*
    public static String genId(String modified, String filename) {
        String id = filename+modified;
        return Hash.hash(id);
    }
    
    public static String toBase64(byte[] arr) {
        return Base64.getEncoder().encodeToString(arr);
    }
    
    public static byte[] toArray(String str) {    	
        return str.getBytes();
    }

   
    public static void saveMap(String filePath,Map<String, BackedUpFile> backedUpFiles) {
    	try {
    	new File(Peer.peerId).mkdirs();
    		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath));
		out.writeObject(backedUpFiles);
		out.close();
    	}catch(Exception e) {e.printStackTrace();}
    }
    
    public static Map<String,BackedUpFile> loadMap(String filePath) {
    	Map<String,BackedUpFile> temp=null;
    	try {
    		File file= new File(filePath);
    		if(file.exists()) {
		    	ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file));
		    	temp=(Map<String,BackedUpFile>)ois.readObject();
		        ois.close();
    		}
	        
		}catch(Exception e) {e.printStackTrace();}	
		return temp;
    }
    */
	public static int calculateFolderSize(String folderPath) {
		
		File backedUpFilesFolder= new File(folderPath);
		
		if(backedUpFilesFolder.exists()==false)
			return 0;
		
		File[] listOfBackedUpFiles=backedUpFilesFolder.listFiles();
		
		int totalFolderSize=0;
		for(int i=0;i<listOfBackedUpFiles.length;i++)
		{
			totalFolderSize+=listOfBackedUpFiles[i].length();
		}
		return totalFolderSize;
		
	}
	
    public static boolean storeFile(int peer, String fileId, byte[] data) {
        boolean success = true;
        try {
        	new File(peer+"/"+"backedup files"+"/"+fileId).mkdirs();
            FileUtil.save(data, peer+"/"+"backedup files"+"/"+fileId);    
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return success;        
    }
    
    /**
     * Read a chunk, given its id and number
     * @param fileId
     * @param chunkNo
     * @return 
     */
    public static byte[] readChunk(String fileId, int chunkNo) {
        byte[] res = null;
        try {
            res = FileUtil.read(fileId+"_"+chunkNo+".part");
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return res;
    }
    
    public static List<String> deleteAllFiles(String path) {
        List<String> deletedFiles = new ArrayList<String>();
        
        File folder = new File(path);
        if(! folder.exists()){
            System.out.println("Warning: No peer folder!");
            return deletedFiles;
        }
        File listofFiles[] = folder.listFiles();
        if(listofFiles == null){
            System.out.println("Warning: No files to delete!");
            return deletedFiles;
        }
        for(File file : listofFiles){
            deletedFiles.add(file.getName());
            file.delete();
        }
        
        
        return deletedFiles;
    }

    
    public static List<String> deleteFiles(int allowedSpace, int port) {
        
        List<String> deletedFiles  = new ArrayList<>();
        
        if(Peer.getCurrentStorageInBytes() <= Peer.maxAllowedStorageInBytes)
        	return deletedFiles;
        
            File folder = new File(String.valueOf(port) + "/" + "backedup files/");
        if(! folder.exists()){
            System.out.println("Warning: No peer folder!");
            return deletedFiles;
        }
        
        File listofFiles[] = folder.listFiles();
     
        for(File file : listofFiles){
            //Knapsack algorithm perfect application..
            if(Peer.getCurrentStorageInBytes() > Peer.maxAllowedStorageInBytes){
                System.out.println(file.toString());
                
                file.delete();
                deletedFiles.add(file.getName());
            }
            else 
            	break;
        }
        
        
        return deletedFiles;
    }
}

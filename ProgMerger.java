/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/8/12
 * Time: 11:20 AM
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map; 

public class ProgMerger {
    public static void main(String args[]) throws IOException{
        // Two arguments:  prog directory and archive root
        String progDir=args[0];
        String archiveRoot=args[1];
        
        File progDirFl=new File(progDir);
        File archiveDirFl=new File(archiveRoot);

        // Traverse and build a list of existing files
        Map<String,String> fileList=traverseDir(archiveDirFl);

        //  Find files in prog that correspond to files in the archive
        for (File fl:progDirFl.listFiles()){
            if (fileList.keySet().contains(fl.getName())){
                // copy the file from the prog dir to its place in the archive
                File addonFl=new File(fileList.get(fl.getName())+File.separator+fl.getName());
                addonFl.delete();

                FileInputStream fis=new FileInputStream(fl);
                FileOutputStream fos=new FileOutputStream(addonFl);
                byte fileContents[]=new byte[(int)fl.length()];
                fis.read(fileContents);
                fos.write(fileContents);
                fis.close();
                fos.close();
                System.out.println(addonFl.toString());
            }
        }
    }
    
    public static Map<String,String> traverseDir(File dir){
        Map<String,String> flDirs=new HashMap<String, String>();
        if (!dir.isDirectory()){
            System.err.println("Not a directory.");
            return null;
        }
        for (File fl:dir.listFiles()){
            if (fl.getName().contains(".git")){
                continue;
            }
            
            if (fl.isDirectory()){
                flDirs.putAll(traverseDir(fl));
                continue;
            }
            flDirs.put(fl.getName(),dir.getAbsolutePath());
        }                                     
        
        return flDirs;
    }
}

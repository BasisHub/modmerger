/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/8/12
 * Time: 1:44 PM
 */

import java.io.*;
public class CopyBackCallpoints {
    
    public static void copyBack(String modDirectory,String archiveDirectory) throws IOException{
        File modDirFile=new File(modDirectory);
        File archiveDirFile=new File(archiveDirectory);
        
        for (File modFl:modDirFile.listFiles()){
            File archiveFl=new File(archiveDirFile.getAbsoluteFile()+File.separator+modFl.getName());
            
            if (archiveFl.exists()){
                CallPointSegments modCPS=new CallPointSegments(modFl.getAbsolutePath());
                CallPointSegments archiveCPS=new CallPointSegments(archiveFl.getAbsolutePath());
                
                for (String callpointName:archiveCPS.keySet()){
                    if (callpointName.contains(".B") || callpointName.contains("CUSTOM")){
                        if (modCPS.keySet().contains(callpointName)){
                            modCPS.put(callpointName,archiveCPS.get(callpointName));    
                        }
                    }
                }
                modCPS.save();
            }
        }
    }
    
    public static void main(String args[]) throws IOException {
        if (args.length!=2){
            System.err.println("<mods cdf directory> <archive cdf directory>");
        }
        
        String modDirectory=args[0];
        String archiveDirectory=args[1];

        copyBack(modDirectory,archiveDirectory);
    }

}

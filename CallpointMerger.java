import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/3/12
 * Time: 10:56 AM
 * 
 * Takes a mod CallPointSegments and a core CallPointSegments and integrates the mod into the core.
 */
public class CallpointMerger {

    public void mergeCallpoints (CallPointSegments mod, CallPointSegments core) throws FileNotFoundException,IOException {

        // String codeBlockName="";
        for (String codeBlockName:mod.codeBlockList) {
            core.insert(codeBlockName,mod.get(codeBlockName));
        }
        if (core.getChanged()) {
            core.save();
        }
    }
    
    public void crawlDirectories(String modDirectoryName, String coreDirectoryName) throws IOException {
        File modDirectory=new File(modDirectoryName);
        File coreDirectory=new File(coreDirectoryName);
        
        if (!modDirectory.isDirectory()) {
            throw new Error(modDirectoryName+" is not a directory.");
        }
        
        if (!coreDirectory.isDirectory()){
            throw new Error(coreDirectoryName+" is not a directory.");
        }
        
        List modFiles= Arrays.asList(modDirectory.list());
        List coreFiles=Arrays.asList(coreDirectory.list());

        File file=null;
        File coreFile=null;
        for (Object fobj:modFiles){
            
            file=new File(modDirectory+File.separator+(String)fobj);
            coreFile=new File(coreDirectoryName+File.separator+file.getName());
            
            System.out.println(coreFile.getAbsolutePath()+":  Exists in core? "+coreFile.exists());
            if (coreFile.exists()){
                mergeCallpoints(newCallPointSegments(file.getCanonicalPath()), newCallPointSegments(coreFile.getCanonicalPath()));
            }
        }
    }
    
    protected CallPointSegments newCallPointSegments(String fileName) throws IOException {
        return new CallPointSegments(fileName);
    }

    public static void main(String args[]) throws Exception {
        
        CallpointMerger c=new CallpointMerger();
        if (args.length == 2) {
            c.crawlDirectories(args[0],args[1]);
        }
        else {
            throw new Exception("Wrong number of parameters. Need <mod directory> <core directory>");
        }
    }

}

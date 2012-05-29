/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/2/12
 * Time: 5:52 PM
 */
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CallPointSegments extends HashMap<String,CallPointSegment>{
    
    public ArrayList<String> codeBlockList =new ArrayList<String>(); 
    protected String fileName;
    protected boolean changed=false;
    protected enum State{SEEKING,GATHERING}

    protected CallPointSegments(){}
    public CallPointSegments(String cdfFileName) throws FileNotFoundException,IOException{
    
        CallPointSegment callPointSegment;
        
        //Open and Populate ourselves from a CDF file
        fileName=cdfFileName;
        File fl=new File(cdfFileName);

        if (!fl.exists()){
            throw new FileNotFoundException(cdfFileName+" could not be found.");
        }

        // Parse the file and populate the list
        // Read entire file into a string
        FileInputStream fis=new FileInputStream(fl);
        byte fileBytes[]=new byte[(int)fl.length()]; 
        fis.read(fileBytes);
        fis.close();

        String fileContents=new String(fileBytes);
        String lines[]=fileContents.split("\n");

        String codeBlockName="";
        String codeBlock="";
        State state=State.SEEKING;
        for (String currentLine:lines ){
            if (currentLine.contains("[[")) {
                
                if (state==State.GATHERING) {
                    codeBlockList.add(codeBlockName);
                    callPointSegment=newCallPointSegment(codeBlockName,codeBlock);
                    put(codeBlockName, callPointSegment);
                    codeBlock="";
                    codeBlockName="";
                }
            
                // Create the first line of the code block
                codeBlock=codeBlock.concat(currentLine+"\n");
                
                // Extract the name for the new code block
                codeBlockName=currentLine.substring(currentLine.indexOf("[[") + 2, currentLine.indexOf("]]"));
                
                // Set the current state
                state=State.GATHERING;
                continue;
            }

            if (state==State.SEEKING){
                continue;
            }
            
            if (state==State.GATHERING){
                codeBlock=codeBlock.concat(currentLine+"\n");
            }
        }
        if (state==State.GATHERING) {
            codeBlockList.add(codeBlockName);
            callPointSegment=newCallPointSegment(codeBlockName,codeBlock);
            put(codeBlockName, callPointSegment);
        }
    
    }
    
    public void insert(String codeBlockName, CallPointSegment callPointSegment){
        String correspondingCodeBlockName="";

        // This is where you apply the git archive rules
        if (containsKey(codeBlockName)) {
            correspondingCodeBlockName=codeBlockName;
        }
        else {
            correspondingCodeBlockName=codeBlockName.substring(0,codeBlockName.length()-2);
        }

        // Handle before callpoints containing a skip and custom callpoints
        if ((codeBlockName.endsWith(".B") && containsKey(correspondingCodeBlockName) && callPointSegment.getCodeBlock().contains("SKIP\"")) ||
           (codeBlockName.contains("<CUSTOM>"))) {
            changed=true;
            remove(correspondingCodeBlockName);
            put(codeBlockName,callPointSegment);
            System.out.println("\t"+codeBlockName+": processed");
            codeBlockList.set(codeBlockList.indexOf(correspondingCodeBlockName),codeBlockName);
            return;
        }
    }
    
    public void save() throws FileNotFoundException,IOException {
        // Derive the name of the new file and erase if it already exists
        File newFile=new File(fileName);
        if (newFile.exists()){
            newFile.delete();
        }

        BufferedWriter fos=new BufferedWriter(new FileWriter(newFile));
        for (String codeBlockName:codeBlockList){
            String codeBlockContent=get(codeBlockName).getCodeBlock();
            fos.write(codeBlockContent);
        }

        fos.close();
    }
    
    protected CallPointSegment newCallPointSegment(String codeBlockName, String codeBlock){
        return new CallPointSegment(codeBlockName,codeBlock);
    }

    public boolean getChanged(){
        return changed;
    }
    
}

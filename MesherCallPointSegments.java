import java.io.*;

/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/22/12
 * Time: 5:16 PM
 */
public class MesherCallPointSegments extends CallPointSegments {
    protected MesherCallPointSegments() {}

    public MesherCallPointSegments(String fileName) throws IOException {
        super(fileName);
    }

    public void insert(String codeBlockName, CallPointSegment callPointSegment){
        if (callPointSegment.isCustom()) {
            MesherCallPointSegment mcpsToBeInserted=(MesherCallPointSegment)callPointSegment;
            
            // Find the corresponding codeblock
            String correspondingCodeBlockName=codeBlockName;
            if (!containsKey(correspondingCodeBlockName)) {
                if (codeBlockName.endsWith(".B") || codeBlockName.endsWith(".A")){
                    correspondingCodeBlockName=codeBlockName.substring(0,codeBlockName.length()-2);
                }
            }
            
            MesherCallPointSegment correspondingMcps=(MesherCallPointSegment)get(correspondingCodeBlockName);
            if (correspondingMcps==null){
                super.insert(codeBlockName,callPointSegment);
                return;
            }
            
            // Correlate the subroutines within the custom callpoints
            CustomCallpointSubroutines correspondingSubs=correspondingMcps.getCustomCallpointSubroutines();
            CustomCallpointSubroutines subsToBeInserted=mcpsToBeInserted.getCustomCallpointSubroutines();
            correspondingSubs.customCallpointHeader=subsToBeInserted.customCallpointHeader;
            for (String subroutineNameToBeInserted:subsToBeInserted.subroutineList){
                correspondingSubs.insert(subroutineNameToBeInserted,subsToBeInserted.get(subroutineNameToBeInserted));   
            }
            if (correspondingSubs.getChanged()){
                changed=true;
                ((MesherCallPointSegment) callPointSegment).customCallpointSubroutines=correspondingSubs;
            }
            remove(correspondingCodeBlockName);
            put(codeBlockName,callPointSegment);
            codeBlockList.set(codeBlockList.indexOf(correspondingCodeBlockName),codeBlockName);
            
        }
        else {
            super.insert(codeBlockName,callPointSegment);
            return;
        }
    }

    public void save() throws IOException {
        // Derive the name of the new file and erase if it already exists
        File newFile=new File(fileName);
        if (newFile.exists()){
            newFile.delete();
        }

        BufferedWriter fos=new BufferedWriter(new FileWriter(newFile));
        for (String codeBlockName:codeBlockList){
            MesherCallPointSegment cps=(MesherCallPointSegment)get(codeBlockName);
            if (cps.isCustom()) {
                fos.write(cps.getCustomCallpointSubroutines().customCallpointHeader);
                for (String callpointName:cps.getCustomCallpointSubroutines().subroutineList){
                    fos.write(cps.getCustomCallpointSubroutines().get(callpointName));
                }
            }
            else {
                String codeBlockContent=cps.getCodeBlock();
                fos.write(codeBlockContent);
            }
        }

        fos.close();
    }

    protected CallPointSegment newCallPointSegment(String codeBlockName, String codeBlock){
        return new MesherCallPointSegment(codeBlockName,codeBlock);
    }
}

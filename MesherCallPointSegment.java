/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/21/12
 * Time: 9:40 PM
 */
public class MesherCallPointSegment extends CallPointSegment {
    protected CustomCallpointSubroutines customCallpointSubroutines;

    protected MesherCallPointSegment(){}
    public MesherCallPointSegment(String name, String codeBlock){
        super(name,codeBlock);
        if (name.contains("<CUSTOM")){
            customCallpointSubroutines=new CustomCallpointSubroutines(codeBlock);
        }
    }

    public String getCodeBlock(){
        if (!isCustom()){
            return super.getCodeBlock();
        }
        else {
            // Construct the custom code block
            String codeBlock="";
            codeBlock=codeBlock.concat(customCallpointSubroutines.customCallpointHeader);
            for (String subroutineName:customCallpointSubroutines.subroutineList) {
                codeBlock=codeBlock.concat(customCallpointSubroutines.get(subroutineName));
            }

            return codeBlock;
        }
    }

    public CustomCallpointSubroutines getCustomCallpointSubroutines() {
        return customCallpointSubroutines;
    }
}
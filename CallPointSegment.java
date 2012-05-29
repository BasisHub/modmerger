/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/21/12
 * Time: 9:26 PM
 */
public class CallPointSegment {
    private String name;
    private String codeBlock;

    protected CallPointSegment(){}

    public CallPointSegment(String name, String codeBlock){
        this.setName(name);
        this.setCodeBlock(codeBlock);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeBlock() {
        return codeBlock;
    }

    public void setCodeBlock(String codeBlock) {
        this.codeBlock = codeBlock;
    }

    public boolean isCustom(){
        return name.contains("<CUSTOM");
    }

}

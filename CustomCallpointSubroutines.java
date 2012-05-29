/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/2/12
 * Time: 5:52 PM
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class CustomCallpointSubroutines extends HashMap<String,String>{

    public ArrayList<String> subroutineList =new ArrayList<String>();
    String fileName;
    boolean changed=false;
    enum State{SEEKING,NAMING,GATHERING}

    protected String customCallpointHeader="";

    private static final Pattern subroutineNamePattern = Pattern.compile("[a-zA-Z0-9_]+:");
    private static final Pattern includePattern = Pattern.compile("^[ \t]*\\#include .*");
    private static final Pattern lineBeginsWithREMPattern = Pattern.compile("[ \t]*[Rr][Ee][Mm]");
    
    
    private CustomCallpointSubroutines(){}
    public CustomCallpointSubroutines(String customCallpointSegment) {

        String lines[]=customCallpointSegment.split("\n");

        String subroutineName ="";
        String subroutine="";
        State state=State.SEEKING;
        Matcher subroutineNameMatcher;
        Matcher includeMatcher;
        for (String currentLine:lines ){
            if (currentLine.startsWith("rem =====")) {

                if (state==State.GATHERING) {
                    subroutineList.add(subroutineName);
                    put(subroutineName, subroutine);
                    subroutine="";
                    subroutineName ="";
                }

                if (state==State.SEEKING || state==State.GATHERING) {
                    state=State.NAMING;
                    subroutine=subroutine.concat(currentLine+"\n");
                    continue;
                }
                
                if (state==State.NAMING) {
                    state=State.GATHERING;
                    subroutine=subroutine.concat(currentLine+"\n");
                    continue;
                }
            }

            if (state==State.NAMING) {

                subroutine=subroutine.concat(currentLine+"\n");

                if (lineBeginsWithREMPattern.matcher(currentLine).lookingAt()){
                    continue;
                }

                subroutineNameMatcher=subroutineNamePattern.matcher(currentLine);
                if (subroutineNameMatcher.find()){
                    subroutineName=currentLine.substring(subroutineNameMatcher.start(),subroutineNameMatcher.end()-1);
                    System.out.println("subroutine: "+subroutineName);
                    continue;
                }
                
                includeMatcher=includePattern.matcher(currentLine);
                if (includeMatcher.lookingAt()){
                    if (subroutineName=="") {
                        subroutineName=currentLine.substring(includeMatcher.start(),includeMatcher.end());
                        System.out.println("subroutine: "+subroutineName);
                    }
                    continue;
                }

            }

            if (state==State.GATHERING) {
                subroutine=subroutine.concat(currentLine+"\n");
            }

            if (state==State.SEEKING) {
                customCallpointHeader=customCallpointHeader.concat(currentLine+"\n");
            }
        }

        if (state==State.GATHERING) {
            subroutineList.add(subroutineName);
            put(subroutineName, subroutine);
        }

    }

    public void insert(String subroutineName, String subroutine){

        if (subroutineName==null || subroutineName.equals("") || subroutineName.equals(" ")){
            return;
        }

        String correspondingSubroutineName="";

        // Look for a corresponding subroutine name
        if (containsKey(subroutineName)) {
            correspondingSubroutineName=subroutineName;
        }
        else {
            // Find the corresponding subroutine name if the subroutine ends in a suffix
            String suffixes[]={"_after", "_before", "_me"};
            for (String suffix:suffixes){                 
                if (subroutineName.endsWith(suffix)){
                    correspondingSubroutineName=subroutineName.substring(0,subroutineName.length()-suffix.length());
                    break;
                }
            }
        }

        // Handle before callpoints containing a skip and custom callpoints
        if (correspondingSubroutineName.length()>0) {
            if (remove(correspondingSubroutineName)==null) {
                System.out.println("\t"+subroutineName+": not found");
                return;
            }
            changed=true;
            put(subroutineName, subroutine);
            subroutineList.set(subroutineList.indexOf(correspondingSubroutineName),subroutineName);
            System.out.println("\t"+subroutineName+": processed");
            return;
        }
    }


    public boolean getChanged(){
        return changed;
    }

}

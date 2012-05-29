import java.io.File;
import java.io.IOException;
import java.util.MissingFormatArgumentException;

/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/23/12
 * Time: 10:42 PM
 */
public class MesherCopyBackCallpoints extends CopyBackCallpoints {
    public void copyBackCallpoints(File modFl, File archiveFl) throws IOException {

        MesherCallPointSegments modCPS=new MesherCallPointSegments(modFl.getAbsolutePath());
        MesherCallPointSegments archiveCPS=new MesherCallPointSegments(archiveFl.getAbsolutePath());

        for (String callpointName:archiveCPS.keySet()){

            if (callpointName.contains("<CUSTOM")){

                // Copy back the subroutines
                MesherCallPointSegment modMcps=(MesherCallPointSegment)modCPS.get(callpointName);
                MesherCallPointSegment archiveMcps=(MesherCallPointSegment)archiveCPS.get(callpointName);

                if (modMcps==null){
                    System.out.println("Missing: "+callpointName);
                    continue;
                }

                CustomCallpointSubroutines modSubroutines=modMcps.getCustomCallpointSubroutines();
                CustomCallpointSubroutines archiveSubroutines=archiveMcps.getCustomCallpointSubroutines();

                for (String subroutineName:modSubroutines.subroutineList){
                    if (archiveSubroutines.containsKey(subroutineName)){
                        modSubroutines.put(subroutineName,archiveSubroutines.get(subroutineName));
                    }
                }
                archiveMcps.customCallpointSubroutines=modSubroutines;
                modCPS.put(callpointName,archiveMcps);
                modCPS.save();
                continue;
            }

            if (callpointName.contains(".B")){
                if (modCPS.keySet().contains(callpointName)){
                    modCPS.put(callpointName,archiveCPS.get(callpointName));
                    modCPS.save();
                }
                continue;
            }

        }
    }

    public static void main(String args[]) throws IOException {
        MesherCopyBackCallpoints mcbc=new MesherCopyBackCallpoints();
        if (args.length!=2){
            System.err.println("<mods cdf directory> <archive cdf directory>");
        }

        String modDirectory=args[0];
        String archiveDirectory=args[1];

        mcbc.copyBack(modDirectory,archiveDirectory);
    }
}

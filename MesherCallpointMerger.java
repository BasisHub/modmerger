import java.io.IOException;

/**
 * Copyright Basis International, Ltd.
 * User: shaney
 * Date: 5/23/12
 * Time: 11:28 AM
 */
public class MesherCallpointMerger extends CallpointMerger {

    protected CallPointSegments newCallPointSegments(String fileName) throws IOException {
        return new MesherCallPointSegments(fileName);
    }

    public static void main(String args[]) throws Exception {

        MesherCallpointMerger c=new MesherCallpointMerger();
        if (args.length == 2) {
            c.crawlDirectories(args[0],args[1]);
        }
        else {
            throw new Exception("Wrong number of parameters. Need <mod directory> <core directory>");
        }
    }


}

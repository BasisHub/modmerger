#!/bin/bash --debug

javac *.java 

rm CallpointMerger.jar CopyBackCallpoints.jar CopyBackProgFiles.jar ProgMerger.jar

jar cvmf CallpointMerger.MANIFEST.MF  CallpointMerger.jar *.class
jar cvmf CopyBackCallpoints.MANIFEST.MF CopyBackCallpoints.jar *.class 
jar cvmf CopyBackProgFiles.MANIFEST.MF CopyBackProgFiles.jar *.class
jar cvmf ProgMerger.MANIFEST.MF ProgMerger.jar *.class 

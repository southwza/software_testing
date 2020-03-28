package edu.utexas.testing.ee382c;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import edu.utexas.testing.ee382c.entities.JavaBlock;

public class CodeCoverage {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            usage();
            System.exit(0);
        }
        CodeCoverage codeCoverage = new CodeCoverage();
        codeCoverage.calculateCoverage(args[0], args[1]);

    }
    public static void usage() {
        System.out.println("usage: CodeCoverage <unit test file> <target file>");
    }


    private JavaBlock parseJavaFile(String targetFileName) throws Exception {
        File targetFile = new File(targetFileName);
        if (!targetFile.exists()) {
            throw new Exception(targetFileName + " does not exist");
        }
        //TODO: may want to add some validation to make sure we are reading a .java file
        List<String> lines = Files.readAllLines(targetFile.toPath());

        // TODO: Clear out all comments here as there can be braces and semicolons in
        // the comments that will break the parsing...
        JavaBlock javaClass = new JavaBlock(lines);
        return javaClass;
    }


    public void calculateCoverage(String unitTestFileName, String targetFileName) throws Exception {
        JavaBlock javaClass = parseJavaFile(targetFileName);
        // now we have a hierarchical data structure, we should be able to create an
        // annotated version of the target file where we will insert logging statements
        // prior to each node. Then we can run the unit tests against the annotated
        // target file and process the results to calculate coverage

        String annotatedJavaString = javaClass.toString(true);
        System.out.println("annotated String:");
        System.out.println(annotatedJavaString);

        //TODO: Stuff to do:
        // - Need to write out the annotated Java String
        // - Need to compile the annotated Java class
        // - Need to parse the unit test file for individual methods
        // - Execute each individual unit test against the annotated target class
        // Not sure how we're going to handle dependencies... Maybe will just use depend on the java classpath variable being set to find dependencies
    }

}

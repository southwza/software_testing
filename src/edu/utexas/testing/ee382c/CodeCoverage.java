package edu.utexas.testing.ee382c;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import edu.utexas.testing.ee382c.entities.ParserResults;
import edu.utexas.testing.ee382c.utils.JavaParserUtil;

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

    public void calculateCoverage(String unitTestFileName, String targetFileName) throws Exception {
        File unitTestFile = new File(unitTestFileName);
        File targetFile = new File(targetFileName);
        validateExecution(unitTestFile, targetFile);

        ParserResults parserResults = JavaParserUtil.parseTarget(targetFile);

        //Create a temporary working directory
        Path tempDir = Files.createTempDirectory("CodeCoverage");
        System.out.println("Created temporary directory to build and execute junit tests: " + tempDir);

        //create an annotated version of the target file in our temp directory
        String annotatedJavaString = parserResults.toString();
        File annotatedJavaFile = new File(tempDir.toFile(), (targetFile.getName()));
        Files.write(annotatedJavaFile.toPath(), annotatedJavaString.getBytes());

        //copy the junit test file to the temp directory
        Files.copy(unitTestFile.toPath(), tempDir.resolve(unitTestFile.getName()));



        //TODO: Stuff to do:
        // - Parse the JUnit test file to identify test methods
        // - Copy these dependencies into the temp directory:
        //   -SingleJUnitTestRunner.java
        //   -lib/junit.jar
        //   -lib/org.hamcreast.core...jar
        // - Use 'javac' to compile compile the three .java files into .class files:
        // - For each test method found in the JUnit file, execute the SingleJUnitTestRunner
        // - Parse the output of each execution and store the coverage results.
    }

    private void validateExecution(File unitTestFile, File targetFile) throws Exception {
        validateJavaFile(unitTestFile);
        validateJavaFile(targetFile);
        //make sure java and javac are in the path
        for (String executable : Arrays.asList("javac", "java")) {
            String executableFilename = findExecutable(executable);
            if (executableFilename == null) {
                throw new Exception("Couldn't locate '" + executable + "' in the PATH.");
            }
            System.out.println("Using '" + executableFilename + "' for analysis.");
        }
    }

    private String findExecutable(String executableName) {
        String executablePath = Stream.of(System.getenv("PATH").split(File.pathSeparator))
                .map(Paths::get)
                .filter(path -> Files.exists(path.resolve(executableName)))
                .findFirst()
                .map(path -> path.resolve(executableName).toString())
                .orElse(null);
        return executablePath.toString();
    }

    private void validateJavaFile(File javaFile) throws FileNotFoundException {
        if (!javaFile.exists()) {
            throw new FileNotFoundException(javaFile.getAbsolutePath() + " does not exist");
        }
    }

}

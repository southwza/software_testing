package edu.utexas.testing.ee382c;

import edu.utexas.testing.ee382c.entities.JUnitTests;
import edu.utexas.testing.ee382c.entities.ParserResults;
import edu.utexas.testing.ee382c.utils.JavaParserUtil;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

//import sun.tools.jar.CommandLine;

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

        // Find each JUnit test method in the test file
        JUnitTests jUnitTests = JavaParserUtil.parseJUnitFile(unitTestFile);

        // Create a temporary working directory
        Path tempDir = Files.createTempDirectory("CodeCoverage");
        System.out.println("Created temporary directory to build and execute junit tests: " + tempDir);

        // Copy dependencies into the temp directory
        copyProjectResourceToDest("junit.jar", tempDir);
        copyProjectResourceToDest("org.hamcrest.core_1.3.0.jar", tempDir);
        copyProjectResourceToDest("SingleJUnitTestRunner.java", tempDir);

        // Create an annotated version of the target file in our temp directory
        String annotatedJavaString = parserResults.toString();
        File annotatedJavaFile = new File(tempDir.toFile(), (targetFile.getName()));
        Files.write(annotatedJavaFile.toPath(), annotatedJavaString.getBytes());

        // Copy the junit test file to the temp directory
        Files.copy(unitTestFile.toPath(), tempDir.resolve(unitTestFile.getName()));

        // Compile all files in temp folder
        // http://commons.apache.org/proper/commons-exec/tutorial.html
        String line = "javac -cp " + tempDir + "/junit.jar:./ " +
                tempDir + "/SingleJUnitTestRunner.java " +
                tempDir + "/" + unitTestFile.getName() + " " +
                tempDir + "/" + targetFile.getName();
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        int exitValue = executor.execute(cmdLine);

        //TODO: Stuff to do:
        // - Use 'javac' to compile compile the three .java files into .class files:
        //   - change directory to temp dir
        //   - execute: javac -cp ./junit.jar:./ SingleJUnitTestRunner.java
        //   - execute: javac -cp ./junit.jar:./ <junit test file>
        //   - execute: javac -cp ./junit.jar:./ <target file>
        //   - make sure .class files have been created for each of these .java files
        // - For each test method found in the JUnit file, (jUnitTests.getTestMethods()) execute the SingleJUnitTestRunner
        //   - java -cp ./junit.jar:. SingleJUnitTestRunner <JUnit class name>#testScalene
        //     - I think the class name needs to be prepended with the package if it is defined in the JUnit test file.
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
        return executablePath;
    }

    private void validateJavaFile(File javaFile) throws FileNotFoundException {
        if (!javaFile.exists()) {
            throw new FileNotFoundException(javaFile.getAbsolutePath() + " does not exist");
        }
    }

    private void copyProjectResourceToDest(String fileName, Path destination) throws IOException {
        String sourceFile = fileName.replace("java", "jav_");
        InputStream projectResourceStream = ClassLoader.getSystemResourceAsStream(sourceFile);
        FileUtils.copyInputStreamToFile(projectResourceStream, destination.resolve(fileName).toFile());
    }

}

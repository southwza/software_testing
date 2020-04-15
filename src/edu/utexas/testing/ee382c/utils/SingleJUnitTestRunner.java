package edu.utexas.testing.ee382c.utils;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

//This class is borrowed from this StackOverflow message:
//https://stackoverflow.com/questions/9288107/run-single-test-from-a-junit-class-using-command-line
//The idea is that we want to be able to run one junit test at a time from the
//test class so we can calculate coverage for that class alone.
public class SingleJUnitTestRunner {
    public static void main(String... args) throws ClassNotFoundException {
        String[] classAndMethod = args[0].split("#");
        Request request = Request.method(Class.forName(classAndMethod[0]),
                classAndMethod[1]);

        Result result = new JUnitCore().run(request);
        System.exit(result.wasSuccessful() ? 0 : 1);
    }
}

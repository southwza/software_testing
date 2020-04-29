package edu.utexas.testing.ee382c.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import edu.utexas.testing.ee382c.entities.JUnitTests;
import edu.utexas.testing.ee382c.entities.JavaStatement;
import edu.utexas.testing.ee382c.entities.CoverageResults;

public class JavaParserUtil {

    public static CoverageResults parseTarget(File targetFile) throws FileNotFoundException {
        CoverageResults results = new CoverageResults();
        CompilationUnit cu = StaticJavaParser.parse(targetFile);
        ModifierVisitor<CoverageResults> statementAnnotationPrepender = new StatementAnnotationPrepender();
        statementAnnotationPrepender.visit(cu, results);
        results.setCompilationUnit(cu);
        results.setTargetClass(targetFile.getName());
        return results;
    }

    public static JUnitTests parseJUnitFile(File jUnitFile) throws FileNotFoundException {
        JUnitTests jUnitTests = new JUnitTests();
        CompilationUnit cu = StaticJavaParser.parse(jUnitFile);
        List<String> testMethods = new ArrayList<String>();
        VoidVisitorAdapter<List<String>> testMethodFinder = new TestMethodFinder();
        testMethodFinder.visit(cu, testMethods);
        jUnitTests.setTestMethods(testMethods);
        return jUnitTests;
    }

    private static class TestMethodFinder extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> testMethods) {
            super.visit(md, testMethods);
            if (md.isAnnotationPresent("Test")) {
                testMethods.add(md.getNameAsString());
            }
        }
    }

    private static class StatementAnnotationPrepender extends ModifierVisitor<CoverageResults> {
        private int statementSequence = 0;
        private ArrayList<JavaStatement> methodStatements = new ArrayList<JavaStatement>();
        
        @Override
        public MethodDeclaration visit(MethodDeclaration md, CoverageResults parserResults) {
            super.visit(md, parserResults);
            //The parent method doesn't get visited until after the statements...
            String methodName = md.getNameAsString();
            methodStatements.forEach(statement -> {
                statement.setParentMethod(methodName);
                parserResults.addStatement(statement);
                });
            methodStatements = new ArrayList<JavaStatement>();
            return md;
        }
        @Override
        public BlockStmt visit(BlockStmt block, CoverageResults arg) {
            super.visit(block, arg);

            int statementIndex = 0;
            while (statementIndex < block.getStatements().size()) {
                Statement statement = block.getStatement(statementIndex);
                Position statementEnd = statement.getEnd().orElse(null);
                Statement annotation = StaticJavaParser.parseStatement("System.out.println(\"Statement id#" + statementSequence + " ending at line:" + statementEnd.line + " was covered.\");");
                block.addStatement(statementIndex, annotation);

                JavaStatement javaStatement = new JavaStatement();
                javaStatement.setStatementId(statementSequence);
                javaStatement.setLineNumber(statementEnd.line);
                methodStatements.add(javaStatement);

                statementIndex++;
                statementIndex++;
                statementSequence++;
            }
            return block;
        }
    }
}

package edu.utexas.testing.ee382c.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.github.javaparser.Position;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;

import edu.utexas.testing.ee382c.entities.JavaStatement;
import edu.utexas.testing.ee382c.entities.ParserResults;

public class JavaParserUtil {

    public static ParserResults parseTarget(File targetFile) throws FileNotFoundException {
        ParserResults results = new ParserResults();
        CompilationUnit cu = StaticJavaParser.parse(targetFile);
        ModifierVisitor<ParserResults> statementAnnotationPrepender = new StatementAnnotationPrepender();
        statementAnnotationPrepender.visit(cu, results);
        results.setCompilationUnit(cu);
        return results;
    }

    private static class StatementAnnotationPrepender extends ModifierVisitor<ParserResults> {
        private int statementSequence = 0;
        private ArrayList<JavaStatement> methodStatements = new ArrayList<JavaStatement>();
        
        @Override
        public MethodDeclaration visit(MethodDeclaration md, ParserResults parserResults) {
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
        public BlockStmt visit(BlockStmt block, ParserResults arg) {
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

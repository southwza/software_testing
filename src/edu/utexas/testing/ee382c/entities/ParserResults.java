package edu.utexas.testing.ee382c.entities;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;

public class ParserResults {

    private CompilationUnit compilationUnit;
    private List<JavaStatement> statements = new ArrayList<JavaStatement>();

    @Override
    public String toString() {
        return compilationUnit.toString(CompilationUnit.getToStringPrettyPrinterConfiguration());
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public List<JavaStatement> getStatements() {
        return statements;
    }

    public void addStatement(JavaStatement statement) {
        statements.add(statement);
    }
}

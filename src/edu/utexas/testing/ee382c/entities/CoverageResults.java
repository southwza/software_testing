package edu.utexas.testing.ee382c.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;

public class CoverageResults {

    private CompilationUnit compilationUnit;
    private List<JavaStatement> statements = new ArrayList<JavaStatement>();
    private Map<String, List<JavaStatement>> methodCoverage = new HashMap<String, List<JavaStatement>>();
    private String targetClass;

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

    public Map<String, List<JavaStatement>> getMethodCoverage() {
        return methodCoverage;
    }

    public void setMethodCoverage(Map<String, List<JavaStatement>> methodCoverage) {
        this.methodCoverage = methodCoverage;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public JavaStatement getStatementById(Long statementId) {
        JavaStatement result = statements.stream() //
                .filter(statement -> statementId.equals(statement.getStatementId())) //
                .findFirst().orElse(null);
        return result;
    }

    public void displayResults(CoverageResults parserResults) {
        System.out.println(System.lineSeparator() + "### RESULTS: ###");
        //Print total coverage first
        List<JavaStatement> totalStatementsCovered = methodCoverage.values().stream() //
                .flatMap(list -> list.stream()) //
                .distinct()
                .collect(Collectors.toList());
        String coverageRatio = String.format("%.4f", (totalStatementsCovered.size() + 0.0) / statements.size());
        System.out.println("Total Statements: " + statements.size());
        System.out.println("Covered Statements: " + totalStatementsCovered.size());
        System.out.println("Coverage: " + coverageRatio + System.lineSeparator());

        //Print method coverage
        for(String method : methodCoverage.keySet()) {
            List<JavaStatement> statementsCovered = methodCoverage.get(method);
            coverageRatio = String.format("%.4f", (statementsCovered.size() + 0.0) / statements.size());
            System.out.println("Coverage results for JUnit test method: " + method);
            System.out.println("    Coverage of " + targetClass + ": " + coverageRatio + System.lineSeparator());
        }

        //Order the list of all statements by line number
        statements = statements.stream() //
                .sorted(Comparator.comparing(JavaStatement::getLineNumber)) //
                .collect(Collectors.toList());

        //Break the list into groups of adjacent statements
        List<List<JavaStatement>> unCoveredGroups = new ArrayList<List<JavaStatement>>();
        List<JavaStatement> unCoveredGroup = new ArrayList<JavaStatement>();
        for (int i = 0; i < statements.size(); i++) {
            JavaStatement statement = statements.get(i);
            if (totalStatementsCovered.contains(statement)) {
                unCoveredGroups.add(unCoveredGroup);
                unCoveredGroup = new ArrayList<JavaStatement>();
            } else {
                unCoveredGroup.add(statement);
            }
        }
        unCoveredGroups.add(unCoveredGroup);
        //remove any empty groups from the list:
        unCoveredGroups = unCoveredGroups.stream() //
                .filter(group -> group.size() > 0) //
                .collect(Collectors.toList());

        for (List<JavaStatement> statementGroup : unCoveredGroups) {
            if (statementGroup.size() == 1) {
                System.out.println("Uncovered statement at line: " + statementGroup.get(0).getLineNumber());
            } else {
                System.out.println("Uncovered statements from line: " + statementGroup.get(0).getLineNumber()
                        + " to line: " + statementGroup.get(statementGroup.size() - 1).getLineNumber());
            }
        }

    }
}

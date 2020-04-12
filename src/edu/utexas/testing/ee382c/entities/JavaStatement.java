package edu.utexas.testing.ee382c.entities;

public class JavaStatement {

    private long statementId;
    private String parentMethod;
    private int lineNumber;

    public long getStatementId() {
        return statementId;
    }
    public void setStatementId(long statementId) {
        this.statementId = statementId;
    }
    public String getParentMethod() {
        return parentMethod;
    }
    public void setParentMethod(String parentMethod) {
        this.parentMethod = parentMethod;
    }
    public int getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}

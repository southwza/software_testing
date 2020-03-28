package edu.utexas.testing.ee382c.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaBlock {
    static Integer idSeed = 0;

    //Unique identifier for this statement
    private Integer blockId = idSeed++;

    //The line number on which the code starts
    private Integer lineNumber = null;

    //Child nodes that compose this block
    private List<JavaBlock> children = new ArrayList<JavaBlock>();

    private JavaBlock parent = null;

    //Content excluding child statements
    private List<String> statementContent = null;
    public JavaBlock(List<String> lines) {
        this(0, null, lines, null);
    }

    public JavaBlock(int statementLineOffset, List<String> statementContent, List<String> childContent,
            JavaBlock parent) {
        lineNumber = statementLineOffset;
        this.statementContent = statementContent;
        this.parent = parent;

        //Uncomment this to debug the parsing of statements
//        if (statementContent != null) {
//            System.out.println("Statement #" + blockId + " at line " + lineNumber + ":");
//            System.out.println(statementContent.stream().collect(Collectors.joining(System.lineSeparator())));
//        }

        if (childContent == null) {
            // No child content means this block is just composed of a single statement; we
            // don't have to parse any further
            return;
        }

        //initialize this with a negative(invalid) value
        long childStatementStartLine = -1;

        // TODO: make parsing more robust
        // For now, any of these conditions will break the parsing routine:
        // 1)more than one statement per line.
        // 2)block statements that don't use braces
        // 3)block statements that open and close on the same line (if (true){...})
        // 4)String literals that contain braces or semicolons
        // 5)Any content that appears on the same line as a closing brace...
        //
        // Of course, we could format the target file before parsing, but then we have
        // the problem that we wouldn't be able to accurately report the line numbers

        long openBraceCount = 0;
        long closeBraceCount = 0;
        int blockStart = -1;

        for (int lineIndex = 0; lineIndex < childContent.size(); lineIndex++) {
            String line = childContent.get(lineIndex);
            if (line.isEmpty()) {
                continue;
            }
            if (childStatementStartLine < 0) {
                childStatementStartLine = lineIndex;
            }
            openBraceCount += line.chars().filter(c -> c == '{').count();
            closeBraceCount += line.chars().filter(c -> c == '}').count();
            boolean isStatement = line.contains(";");

            // TODO: because of limited parsing capability at this point, just considering
            // these scenarios for now:
            if (isStatement && blockStart < 0) {
                //1) This is a simple statement, add it to the list of children
                List<String> statement = childContent.subList((int)childStatementStartLine, lineIndex + 1);
                children.add(new JavaBlock(statementLineOffset + lineIndex + 1, statement, null, this));
                childStatementStartLine = -1;
            } else if(openBraceCount > closeBraceCount && blockStart < 0) {
                //2) Capture the start of a block statement
                blockStart = lineIndex;
            } else if(openBraceCount == closeBraceCount && blockStart >= 0) {
                //3) Capture the end of a block statement
                List<String> childStatementContent = childContent.subList((int)childStatementStartLine, blockStart + 1);
                List<String> childChildContent = childContent.subList(blockStart + 1, lineIndex);
                children.add(new JavaBlock(statementLineOffset + blockStart + 1, childStatementContent,
                        childChildContent, this));
                blockStart = -1;
                childStatementStartLine = -1;
            }
        }
    }
    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean annotated) {
        String content = "";

        // If parent, grandparent or great-grandparent is null, then we are outside of a
        // method and will not annotate
        if (annotated && parent != null && parent.parent != null && parent.parent.parent != null) {
            //TODO: change this so that it logs to a file which we can parse later...
            content += "System.out.println(\"Statement id#" + blockId + " was covered.\");" + System.lineSeparator();
        }

        if (statementContent != null) {
            content += statementContent.stream() //
                    .collect(Collectors.joining(System.lineSeparator())) //
                    .concat(System.lineSeparator());
        }

        if (!children.isEmpty()) {
            //Recursive call for child nodes to return their content
            for (JavaBlock child : children) {
                content += child.toString(annotated);
            }
            if (statementContent != null) {
                //close the block statement
                //TODO:maybe fix the indentation of the closing brace if we want prettier output
                content += "}" + System.lineSeparator();
            }
        }
        return content;

    }
}
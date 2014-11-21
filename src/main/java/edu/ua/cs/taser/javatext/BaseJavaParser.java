package edu.ua.cs.taser.javatext;

import edu.ua.cs.taser.common.AntlrParser;
import edu.ua.cs.taser.document.JavaEntity;
import edu.ua.cs.taser.lang.Strings;
import edu.ua.cs.taser.token.JavaToken;
import edu.ua.cs.taser.token.TokenType;
import edu.ua.cs.taser.token.Tokens;
import edu.ua.cs.taser.util.Lists;
import edu.ua.cs.taser.util.Stacks;
import edu.ua.cs.taser.util.position.Position;
import edu.ua.cs.taser.util.position.PositionedComparator;
import edu.ua.cs.taser.util.position.Positions;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Extended by the class generated from the JavaParser.g file.
 */
public abstract class BaseJavaParser extends AntlrParser {

    protected StringBuilder cUnit = new StringBuilder();

    protected List<JavaEntity> entities = new LinkedList<JavaEntity>();
    protected List<JavaToken> tokens = new LinkedList<JavaToken>();

    protected Deque<Integer> anonStack = new ArrayDeque<Integer>();
    protected Deque<JavaEntity> entityStack = new LinkedList<JavaEntity>();
    protected Deque<String> formalsStack = new ArrayDeque<String>();
    protected Deque<String> qualifiedNameStack = new ArrayDeque<String>();

    protected List<JavaToken> comments = new LinkedList<JavaToken>();
    protected List<String> typeArguments = new ArrayList<String>();
    protected List<String> typeParameters = new ArrayList<String>();

    protected JavaToken lastVariableDeclaratorId;
    protected JavaToken lastTokenAdded;

    protected boolean isCollectingTypeParts = false;
    protected List<JavaToken> typeParts = new ArrayList<JavaToken>();

    protected boolean isCollectingCreatedNameParts = false;
    protected List<JavaToken> createdNameParts = new ArrayList<JavaToken>();

    public String getCompilationUnit() {
        return cUnit.toString();
    }

    public List<JavaEntity> getEntities() {
        return entities;
    }

    public List<JavaToken> getTokens() {
        return tokens;
    }

    protected BaseJavaParser(TokenStream input) {
        super(input);
    }

    protected BaseJavaParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    protected void addComment(JavaToken comment) {
        addComment(comment, PositionedComparator.positionedComparator());
    }

    protected void addComment(JavaToken comment, PositionedComparator cmp) {
        Lists.insort(comments, comment, cmp);
        Lists.insort(tokens, comment, cmp);
    }

    protected void addEntity(JavaEntity entity) {
        addEntity(entity, PositionedComparator.positionedComparator());
    }

    protected void addEntity(JavaEntity entity, PositionedComparator cmp) {
        Lists.insort(entities, entity, cmp);
    }

    protected void addToken(JavaToken token) {
        addToken(token, PositionedComparator.positionedComparator());
    }

    protected void addToken(JavaToken token, PositionedComparator cmp) {
        Lists.insort(tokens, token, cmp);
        lastTokenAdded = token;
        if (isCollectingTypeParts) {
            Lists.insort(typeParts, token, cmp);
        }
        if (isCollectingCreatedNameParts) {
            Lists.insort(createdNameParts, token, cmp);
        }
    }

    protected void appendString(String s) {
        String last = Stacks.pop(qualifiedNameStack);
        Stacks.push(qualifiedNameStack, last + s);
        JavaEntity top = Stacks.top(entityStack);
        top.setName(top.getName() + s);
    }

    protected static Position calculateEndPos(Position startPos, String text) {
        int endLine = startPos.getLine() + Strings.countMatches(text, "\n");
        int endCol = startPos.getColumn() + text.length() - 1;
        int lastNl = text.lastIndexOf('\n');
        if (lastNl >= 0) {
            endCol = text.substring(lastNl + 1).length();
        }
        return Positions.position(endLine, endCol + 1);
    }

    protected void connectCommentsToEntities() {
        int numEntities = entities.size();
        for (JavaToken comment : comments) {
            Position range = comment.getPosition();
            Position start = range.getStart();
            int sLine = start.getLine();
            int sCol = start.getColumn();
            int innermost_match = 0;
            for (int i = 1; i < numEntities; i++) {
                JavaEntity entity = entities.get(i);
                Position nrange = entity.getPosition();
                Position nstart = nrange.getStart();
                int nsLine = nstart.getLine();
                int nsCol = nstart.getColumn();
                if ((sLine > nsLine) || ((sLine == nsLine) && (sCol >= nsCol))) {
                    Position end = range.getEnd();
                    int eLine = end.getLine();
                    int eCol = end.getColumn();
                    Position nend = nrange.getEnd();
                    int neLine = nend.getLine();
                    int neCol = nend.getColumn();
                    if ((eLine < neLine) || ((eLine == neLine) && (eCol <= neCol))) {
                        innermost_match = i;
                    }
                } else {
                    break;
                }
            }
            comment.setEntity(entities.get(innermost_match));
        }
    }

    protected static String createGenericsString(List<String> types) {
        String g = "<" + Strings.join(types, ',') + ">";
        types.clear();
        return g;
    }

    protected String createFormalsString() {
        String f = "(" + Strings.join(formalsStack.descendingIterator(), ',') + ")";
        formalsStack.clear();
        return f;
    }

    protected String createTypeArgumentsString() {
        return createGenericsString(typeArguments);
    }

    protected String createTypeParametersString() {
        return createGenericsString(typeParameters);
    }

    protected String createQualifiedName() {
        return Strings.join(qualifiedNameStack.descendingIterator(), '.');
    }

    protected static Position findFirstPosition(Position... positions) {
       List<Position> sorted = new ArrayList<Position>();
       for (Position position : positions) {
            Lists.insort(sorted, position);
       }
       return sorted.get(0);
    }

    protected static JavaToken makeComment(org.antlr.runtime.Token antlrToken, TokenType type) {
        String text = antlrToken.getText().trim();
        Position sp = Positions.position(antlrToken.getLine(), antlrToken.getCharPositionInLine() + 1);
        return Tokens.newJavaToken(text, type, Positions.rangePosition(sp, calculateEndPos(sp, text)));
    }

    protected static JavaToken makeToken(org.antlr.runtime.Token antlrToken, TokenType type, JavaEntity entity) {
        return makeToken(antlrToken, type, entity, antlrToken.getText());
    }

    protected static JavaToken makeToken(org.antlr.runtime.Token antlrToken, TokenType type, JavaEntity entity, String text) {
        int line = antlrToken.getLine();
        int col = antlrToken.getCharPositionInLine() + 1;
        return Tokens.newJavaToken(text, type, Positions.rangePosition(line, col, line, col + text.length()), entity);
    }
}

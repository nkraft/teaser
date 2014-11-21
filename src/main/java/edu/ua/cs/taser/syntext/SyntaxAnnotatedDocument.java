package edu.ua.cs.taser.syntext;

import edu.ua.cs.taser.document.EntityType;

import java.util.LinkedList;
import java.util.List;

public class SyntaxAnnotatedDocument {

    private final String name;
    private final List<SyntaxAnnotatedToken> tokens;
    private final EntityType type;

    public SyntaxAnnotatedDocument(final EntityType type, final String name) {
        this(type, name, new LinkedList<SyntaxAnnotatedToken>());
    }

    public SyntaxAnnotatedDocument(final EntityType type, final String name, final List<SyntaxAnnotatedToken> tokens) {
        this.type = type;
        this.name = name;
        this.tokens = tokens;
    }

    public String getName() {
        return name;
    }

    public List<SyntaxAnnotatedToken> getTokens() {
        return tokens;
    }

    public EntityType getType() {
        return type;
    }
}

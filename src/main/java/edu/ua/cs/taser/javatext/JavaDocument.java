package edu.ua.cs.teaser.javatext;

import edu.ua.cs.teaser.document.JavaEntity;
import edu.ua.cs.teaser.token.JavaToken;

import java.util.ArrayList;
import java.util.List;

public class JavaDocument {

    private final List<JavaEntity> entities;
    private final String cUnit;
    private final String jls;
    private final List<JavaToken> tokens;
    private final List<String> rewrites;

    public JavaDocument(final String cUnit, final String jls, final List<JavaEntity> entities, final List<JavaToken> tokens) {
        this(cUnit, jls, entities, tokens, new ArrayList<String>());
    }

    public JavaDocument(final String cUnit, final String jls, final List<JavaEntity> entities, final List<JavaToken> tokens, final List<String> rewrites) {
        this.cUnit = cUnit;
        this.entities = entities;
        this.tokens = tokens;
        this.jls = jls;
        this.rewrites = rewrites;
    }

    public void addRewrite(String name) {
        rewrites.add(name);
    }

    public List<JavaEntity> getEntities() {
        return entities;
    }

    public String getCompilationUnit() {
        return cUnit;
    }

    public String getJavaLanguageSpec() {
        return jls;
    }

    public List<JavaToken> getTokens() {
        return tokens;
    }

    public List<String> getRewrites() {
        return rewrites;
    }
}

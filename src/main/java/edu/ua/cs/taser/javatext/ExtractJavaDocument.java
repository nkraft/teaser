package edu.ua.cs.taser.javatext;

import edu.ua.cs.taser.common.FileContent;
import edu.ua.cs.taser.document.EntityType;
import edu.ua.cs.taser.document.JavaEntity;
import edu.ua.cs.taser.token.JavaToken;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;

import rx.util.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExtractJavaDocument implements Func1<FileContent, JavaDocument> {

    private final JavaLexerType lexerType;

    public ExtractJavaDocument(final JavaLexerType lexerType) {
        this.lexerType = lexerType;
    }

    public JavaDocument call(FileContent fileContent) {
        char[] content = fileContent.getCharacters();
        JavaLexer lexer = JavaLexerFactory.createJavaLexer(lexerType);
        lexer.setCharStream(new ANTLRStringStream(content, content.length));
        JavaTokenStream cts = new JavaTokenStream(lexer);
        JavaParser parser = new JavaParser(cts);
        cts.registerParser(parser);
        final String filename = fileContent.getPath().toString();
        List<JavaToken> tokens = null;
        List<JavaEntity> entities = null;
        String cUnit = null;
        try {
            tokens = parser.compilationUnit(filename, EntityType.FILE);
            entities = parser.getEntities();
            cUnit = parser.getCompilationUnit();
        } catch (RecognitionException e) {
            tokens = new ArrayList<JavaToken>();
            entities = new ArrayList<JavaEntity>();
            cUnit = filename;
        }
        return new JavaDocument(cUnit, lexerType.toString().toLowerCase(Locale.US), entities, tokens);
    }
}

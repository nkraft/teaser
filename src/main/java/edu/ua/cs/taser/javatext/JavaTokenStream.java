package edu.ua.cs.teaser.javatext;

import edu.ua.cs.teaser.token.TokenType;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;

public class JavaTokenStream extends CommonTokenStream {

    protected JavaParser parser = null;

    public JavaTokenStream(JavaLexer lexer) {
        super(lexer);
    }

    public void registerParser(JavaParser parser) {
        this.parser = parser;
    }

    @Override
    protected void fetch(int n) {
        for (int i=1; i<=n; i++) {
            Token t = tokenSource.nextToken();
            t.setTokenIndex(tokens.size());
            tokens.add(t);
            if (t.getType()==Token.EOF) break;
            if (t.getChannel() == JavaLexer.COMMENT_CHANNEL) {
                parser.addComment(
                    parser.makeComment(t,
                        (t.getType() == JavaLexer.BLOCK_COMMENT) ?
                            TokenType.BLOCK_COMMENT :
                            TokenType.LINE_COMMENT
                    )
                );
            }
        }
    }
}

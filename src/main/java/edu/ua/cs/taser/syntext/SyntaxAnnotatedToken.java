package edu.ua.cs.taser.syntext;

import edu.ua.cs.taser.token.Token;
import edu.ua.cs.taser.token.TokenType;

public class SyntaxAnnotatedToken implements Token {

    private String text;
    private TokenType type;
    private Number weight;

    public SyntaxAnnotatedToken(final Token token) {
        this(token.getType(), token.getText());
    }

    public SyntaxAnnotatedToken(final TokenType type, final String text) {
        this.type = type;
        this.text = text;
        this.weight = 1;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public TokenType getType() {
        return type;
    }

    public Number getWeight() { return weight; }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public void setWeight(Number weight) { this.weight = weight; }

    @Override
    public String toString() {
        return String.format("%-24s %-16s", type.toString(), text);
    }
}

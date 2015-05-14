package edu.ua.cs.teaser.token;

import edu.ua.cs.teaser.document.Entities;
import edu.ua.cs.teaser.document.EntityType;
import edu.ua.cs.teaser.document.JavaEntity;
import edu.ua.cs.teaser.util.position.Position;
import edu.ua.cs.teaser.util.position.Positions;

public final class Tokens {

    public static JavaToken newJavaToken(String text, TokenType type, Position pos) {
        return newJavaToken(text, type, pos, Entities.noJavaEntity());
    }

    public static JavaToken newJavaToken(String text, TokenType type, Position pos, JavaEntity entity) {
        return new BasicJavaToken(new BasicToken(text, type), pos, entity);
    }

    private static final class BasicToken implements Token {
        private String text;
        private TokenType type;
        private Number weight;

        BasicToken(String text, TokenType type) {
            this(text, type, 1);
        }

        BasicToken(String text, TokenType type, Number weight) {
            this.text = text;
            this.type = type;
            this.weight = weight;
        }

        @Override public String getText() { return text; }
        @Override public TokenType getType() { return type; }
        @Override public Number getWeight() { return weight; }
        @Override public void setText(String text) { this.text = text; }
        @Override public void setType(TokenType type) { this.type = type; }
        @Override public void setWeight(Number weight) { this.weight = weight; }

        @Override public String toString() {
            return String.format("[%4s] %-24s %-16s", weight.toString(), type.toString(), text);
        }
    }

    private static final class BasicJavaToken implements JavaToken {
        private final Token token;
        private Position pos;
        private JavaEntity entity;

        BasicJavaToken(Token token, Position pos, JavaEntity entity) {
            this.token = token;
            this.pos = pos;
            this.entity = entity;
        }

        @Override public String getText() { return token.getText(); }
        @Override public TokenType getType() { return token.getType(); }
        @Override public Number getWeight() { return token.getWeight(); }
        @Override public void setText(String text) { token.setText(text); }
        @Override public void setType(TokenType type) { token.setType(type); }
        @Override public void setWeight(Number weight) { token.setWeight(weight); }

        @Override public Position getPosition() { return pos; }
        @Override public JavaEntity getEntity() { return entity; }
        @Override public void setPosition(Position pos) { this.pos = pos; }
        @Override public void setEntity(JavaEntity entity) { this.entity = entity; }

        @Override public String toString() {
            return String.format("(%16s) %s <-- %s", pos.toString(), token.toString(), entity.getName());
        }
    }

    private Tokens() {}
}

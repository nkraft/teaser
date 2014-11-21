package edu.ua.cs.taser.token;

public interface Token {
    public String getText();
    public TokenType getType();
    public Number getWeight();
    public void setText(String text);
    public void setType(TokenType type);
    public void setWeight(Number weight);
}

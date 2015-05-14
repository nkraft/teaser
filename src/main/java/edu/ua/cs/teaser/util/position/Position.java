package edu.ua.cs.teaser.util.position;

public interface Position extends Comparable<Position> {
    public int getLine();
    public int getColumn();

    public Position getStart();
    public Position getEnd();

    public boolean isDefined();
    public boolean isRange();

    public boolean precedes(Position pos);
    public boolean includes(Position pos);
    public boolean follows(Position pos);

    public Position union(Position pos);
    public Position withEnd(Position pos);
    public Position withStart(Position pos);
}

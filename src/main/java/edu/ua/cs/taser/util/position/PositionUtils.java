package edu.ua.cs.taser.util.position;

public final class PositionUtils {

    public static boolean consecutiveStartLines(Position pos1, Position pos2) {
        // pos2.start.line - pos1.start.line = 1
        return true;
    }

    public static boolean sameStartColumn(Position pos1, Position pos2) {
        // pos1.start.col = pos2.start.col
        return true;
    }

    private PositionUtils() {}
}

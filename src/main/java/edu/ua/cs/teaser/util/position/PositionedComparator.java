package edu.ua.cs.teaser.util.position;

import java.util.Comparator;

public final class PositionedComparator implements Comparator<Positioned> {

    public static PositionedComparator positionedComparator() {
        return INSTANCE;
    }

    private static final PositionedComparator INSTANCE = new PositionedComparator();

    @Override public int compare(Positioned p1, Positioned p2) {
        return p1.getPosition().compareTo(p2.getPosition());
    }

    private PositionedComparator() {}
}

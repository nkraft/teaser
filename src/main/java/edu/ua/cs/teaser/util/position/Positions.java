package edu.ua.cs.teaser.util.position;

public final class Positions {

    public static Position position(int line, int column) {
        return new BasicPosition(line, column);
    }

    public static Position rangePosition(int startLine, int startColumn, int endLine, int endColumn) {
        return rangePosition(position(startLine, startColumn), position(endLine, endColumn));
    }

    public static Position rangePosition(Position start, Position end) {
        if (start.isRange())
            throw new IllegalArgumentException(message("rangePosition", "start", start.toString()));
        if (end.isRange())
            throw new IllegalArgumentException(message("rangePosition", "end", end.toString()));
        if (start.compareTo(end) >= 0)
            throw new IllegalArgumentException(message("rangePosition", "start", start.toString(), "end", end.toString()));
        return new RangePosition(start, end);
    }

    public static Position headPosition() {
        return BoundaryPosition.HEAD;
    }

    public static Position tailPosition() {
        return BoundaryPosition.TAIL;
    }

    public static Position noPosition() {
        return NoPosition.INSTANCE;
    }

    private static abstract class BasePosition implements Position {
        @Override
        public boolean includes(Position pos) {
            return isRange() &&
                pos.isDefined() &&
                getStart().precedes(pos.getStart()) &&
                getEnd().follows(pos.getEnd());
        }

        @Override
        public Position union(Position pos) {
            if (!pos.isRange()) return this;
            if (!isRange()) return pos;
            Position start = getStart().precedes(pos.getStart()) ? getStart() : pos.getStart();
            Position end = getEnd().follows(pos.getEnd()) ? getEnd() : pos.getEnd();
            return rangePosition(start, end);
        }
    }

    private static abstract class DefinedPosition extends BasePosition {
        @Override
        public int compareTo(Position pos) {
            if (this == pos) return 0;
            if (!pos.isDefined()) return pos.compareTo(this) > 0 ? -1 : 1;

            Position self = getStart();
            Position other = pos.getStart();

            int cmp = self.getLine() - other.getLine();
            if (cmp != 0) return cmp;

            cmp = self.getColumn() - other.getColumn();
            if (cmp != 0) return cmp;

            if (!isRange()) return pos.isRange() ? -1 : 0;
            if (!pos.isRange()) return 1;

            self = getEnd();
            other = pos.getEnd();

            cmp = self.getLine() - other.getLine();
            if (cmp != 0) return cmp;

            return self.getColumn() - other.getColumn();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (this == obj) return true;
            if (!(obj instanceof DefinedPosition)) return false;
            DefinedPosition pos = (DefinedPosition)obj;
            if (!isRange()) {
                return pos.isRange() ? false : getLine() == pos.getLine() && getColumn() == pos.getColumn();
            } else {
                return !pos.isRange() ? false : getStart().equals(pos.getStart()) && getEnd().equals(pos.getEnd());
            }
        }

        @Override
        public int hashCode() {
            if (!isRange()) {
                return (getLine() * 17) ^ getColumn();
            } else {
                return getStart().hashCode() ^ getEnd().hashCode();
            }
        }

        @Override public boolean isDefined() { return true; }

        @Override
        public boolean precedes(Position pos) {
            if (this == pos) return true;
            if (!pos.isDefined()) return pos.follows(this);
            Position end = getEnd();
            Position start = pos.getStart();
            if (end.getLine() < start.getLine()) return true;
            if (end.getLine() > start.getLine()) return false;
            return end.getColumn() <= start.getColumn();
        }

        @Override
        public boolean follows(Position pos) {
            if (this == pos) return true;
            if (!pos.isDefined()) return pos.precedes(this);
            Position start = getStart();
            Position end = pos.getEnd();
            if (start.getLine() > end.getLine()) return true;
            if (start.getLine() < end.getLine()) return false;
            return start.getColumn() >= end.getColumn();
        }

        @Override
        public Position withEnd(Position pos) {
            return rangePosition(getStart(), pos.getEnd());
        }

        @Override
        public Position withStart(Position pos) {
            return rangePosition(pos.getStart(), getEnd());
        }
    }

    private static final class BasicPosition extends DefinedPosition {
        private final int line;
        private final int column;

        public BasicPosition(int line, int column) {
            this.line = line;
            this.column = column;
        }

        @Override public int getLine() { return line; }
        @Override public int getColumn() { return column; }
        @Override public Position getStart() { return this; }
        @Override public Position getEnd() { return this; }
        @Override public boolean isRange() { return false; }

        @Override
        public String toString() {
            return "[" + line + "," + column + "]";
        }
    }

    private static final class RangePosition extends DefinedPosition {
        private final Position start;
        private final Position end;

        public RangePosition(Position start, Position end) {
            this.start = start;
            this.end = end;
        }

        @Override public int getLine() { return start.getLine(); }
        @Override public int getColumn() { return start.getColumn(); }
        @Override public Position getStart() { return start; }
        @Override public Position getEnd() { return end; }
        @Override public boolean isRange() { return true; }

        @Override
        public String toString() {
            return "[" + start.toString() + ":" + end.toString() + "]";
        }
    }

    private static abstract class UndefinedPosition extends BasePosition {
        @Override public int getLine() { throw new UnsupportedOperationException(message(this, "getLine")); }
        @Override public int getColumn() { throw new UnsupportedOperationException(message(this, "getColumn")); }

        @Override public Position getStart() { throw new UnsupportedOperationException(message(this, "getStart")); }
        @Override public Position getEnd() { throw new UnsupportedOperationException(message(this, "getEnd")); }

        @Override public boolean isDefined() { return false; }
        @Override public boolean isRange() { return false; }
    }

    private static final class BoundaryPosition extends UndefinedPosition {
        public static final BoundaryPosition HEAD = new BoundaryPosition();
        public static final BoundaryPosition TAIL = new BoundaryPosition();

        @Override
        public int compareTo(Position pos) {
            if (this == pos) return 0;
            if (pos.isDefined()) return this == HEAD ? -1 : 1;
            if (pos == TAIL) return -1;
            if (pos == HEAD) return 1;
            throw new UnsupportedOperationException(message(this, "compareTo", "pos", pos.toString()));
        }

        @Override
        public boolean precedes(Position pos) {
            if (this == pos) return true;
            return pos.isDefined() ? this == HEAD : pos == TAIL;
        }

        @Override
        public boolean follows(Position pos) {
            if (this == pos) return true;
            return pos.isDefined() ? this == TAIL : pos == HEAD;
        }

        @Override public Position withEnd(Position pos) {
            if (this == HEAD && (pos.isDefined() || pos == TAIL)) {
                return rangePosition(this, pos);
            }
            throw new IllegalArgumentException(message(this, "withEnd", "pos", pos.toString()));
        }

        @Override public Position withStart(Position pos) {
            if (this == TAIL && (pos.isDefined() || pos == HEAD)) {
                return rangePosition(pos, this);
            }
            throw new IllegalArgumentException(message(this, "withStart", "pos", pos.toString()));
        }

        @Override public String toString() {
            return this == HEAD ? "[HEAD]" : "[TAIL]";
        }

        private BoundaryPosition() {}
    }

    private static final class NoPosition extends UndefinedPosition {
        public static final NoPosition INSTANCE = new NoPosition();

        @Override
        public int compareTo(Position pos) {
            if (this == pos) return 0;
            throw new UnsupportedOperationException(message(this, "compareTo"));
        }

        @Override public boolean precedes(Position pos) { return this == pos; }
        @Override public boolean follows(Position pos) { return this == pos; }

        @Override public Position withEnd(Position pos) { return pos; }
        @Override public Position withStart(Position pos) { return pos; }

        @Override public String toString() { return "[NoPosition]"; }

        private NoPosition() {}
    }

    private static String message(String method, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("Positions.").append(method).append("(");
        int i = 0;
        while (i < args.length) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(args[i]).append('=').append(args[i+1]);
            i += 2;
        }
        sb.append(")");
        return sb.toString();
    }

    private static String message(Position obj, String method, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append("Position.").append(method).append("(this=").append(obj.toString());
        int i = 0;
        while (i < args.length) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(args[i]).append('=').append(args[i+1]);
            i += 2;
        }
        sb.append(")");
        return sb.toString();
    }

    private Positions() {}
}

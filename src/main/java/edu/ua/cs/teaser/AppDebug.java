package edu.ua.cs.teaser;

import edu.ua.cs.teaser.cli.args.RawArgs;
import edu.ua.cs.teaser.util.position.Position;
import static edu.ua.cs.teaser.util.position.Positions.position;
import static edu.ua.cs.teaser.util.position.Positions.rangePosition;
import static edu.ua.cs.teaser.util.position.Positions.headPosition;
import static edu.ua.cs.teaser.util.position.Positions.tailPosition;
import static edu.ua.cs.teaser.util.position.Positions.noPosition;
import static edu.ua.cs.teaser.io.Files.createDirectories;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.IOException;

public class AppDebug {

    public static void launch(String[] args) {
        // testCli(args);
        testPositions();
        testPositionsCompareTo();
    }

    public static void testCli(String[] args) {
        final RawArgs rawArgs = new RawArgs();
        final JCommander cli = new JCommander(rawArgs);
        try {
            cli.parse(args);
            final String outputDirPath = rawArgs.getOutputDir();
            if (!createDirectories(outputDirPath)) {
                System.err.println("Failed to create output directory '" + outputDirPath + "'");
            } else {
                System.err.println("Created output directory '" + outputDirPath + "'");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            cli.usage();
        }
    }

    public static void testPositionsCompareTo() {
        Position p0 = position(0, 0);
        Position p1 = position(99, 1);
        Position p2 = position(7, 1);
        Position p3 = position(9, 5);
        Position p4 = position(39, 9);
        Position p5 = position(40, 9);
        Position p6 = position(44, 9);
        Position p7 = position(42, 13);
        Position p8 = position(42, 32);
        Position p9 = position(99, 1);

        assert p0.compareTo(p0) == 0;
        assert p0.compareTo(p1) < 0;
        assert p1.compareTo(p0) > 0;
        assert p1.compareTo(p9) == 0;
        assert p9.compareTo(p1) == 0;

        assert p7.compareTo(p8) < 0;
        assert p8.compareTo(p7) > 0;

        Position r0 = rangePosition(p0, p1);
        Position r1 = rangePosition(p2, p1);
        Position r2 = rangePosition(p3, p4);
        Position r3 = rangePosition(p5, p6);
        Position r4 = rangePosition(p7, p8);
        Position r5 = rangePosition(p0, p9);

        assert r0.compareTo(r0) == 0;
        assert r0.compareTo(r1) < 0;
        assert r1.compareTo(r0) > 0;
        assert r0.compareTo(r5) == 0;

        assert p3.compareTo(r2) < 0;
        assert p4.compareTo(r2) > 0;
        assert p7.compareTo(r4) < 0;
        assert p8.compareTo(r4) > 0;

        assert r2.compareTo(p3) > 0;
        assert r2.compareTo(p4) < 0;
        assert r4.compareTo(p7) > 0;
        assert r4.compareTo(p8) < 0;

        Position h0 = headPosition();
        Position h1 = headPosition();

        assert h0.compareTo(h0) == 0;
        assert h0.compareTo(h1) == 0;

        assert h0.compareTo(p0) < 0;
        assert h0.compareTo(p4) < 0;
        assert h0.compareTo(p8) < 0;
        assert h0.compareTo(r0) < 0;
        assert h0.compareTo(r4) < 0;

        assert p0.compareTo(h0) > 0;
        assert p4.compareTo(h0) > 0;
        assert p8.compareTo(h0) > 0;
        assert r0.compareTo(h0) > 0;
        assert r4.compareTo(h0) > 0;

        Position t0 = tailPosition();
        Position t1 = tailPosition();

        assert t0.compareTo(t0) == 0;
        assert t0.compareTo(t1) == 0;

        assert t0.compareTo(p0) > 0;
        assert t0.compareTo(p4) > 0;
        assert t0.compareTo(p8) > 0;
        assert t0.compareTo(r0) > 0;
        assert t0.compareTo(r4) > 0;

        assert p0.compareTo(t0) < 0;
        assert p4.compareTo(t0) < 0;
        assert p8.compareTo(t0) < 0;
        assert r0.compareTo(t0) < 0;
        assert r4.compareTo(t0) < 0;

        assert h0.compareTo(t0) < 0;
        assert t0.compareTo(h0) > 0;

        Position n0 = noPosition();
        Position n1 = noPosition();

        assert n0.compareTo(n1) == 0;

        try { n0.compareTo(p0); assert false; } catch (UnsupportedOperationException e) {}
        try { n0.compareTo(r0); assert false; } catch (UnsupportedOperationException e) {}
        try { n0.compareTo(h0); assert false; } catch (UnsupportedOperationException e) {}
        try { n0.compareTo(t0); assert false; } catch (UnsupportedOperationException e) {}
        try { p0.compareTo(n0); assert false; } catch (UnsupportedOperationException e) {}
        try { r0.compareTo(n0); assert false; } catch (UnsupportedOperationException e) {}
        try { h0.compareTo(n0); assert false; } catch (UnsupportedOperationException e) {}
        try { t0.compareTo(n0); assert false; } catch (UnsupportedOperationException e) {}
    }

    public static void testPositions() {
        Position p0 = position(0, 0);
        Position p1 = position(99, 1);
        Position p2 = position(7, 1);
        Position p3 = position(9, 5);
        Position p4 = position(15, 5);
        Position p5 = position(40, 9);
        Position p6 = position(44, 9);
        Position p7 = position(42, 13);
        Position p8 = position(42, 32);
        Position p9 = position(99, 1);

        assert p0.getLine() == 0 && p0.getColumn() == 0;
        assert p1.getLine() == 99 && p1.getColumn() == 1;
        assert p2.getLine() == 7 && p2.getColumn() == 1;

        assert p3.getStart() == p3 && p3.getEnd() == p3;
        assert p4.getStart() == p4 && p4.getEnd() == p4;
        assert p5.getStart() == p5 && p5.getEnd() == p5;

        assert !p6.isRange();
        assert !p7.isRange();
        assert !p8.isRange();

        assert p0.precedes(p0);
        assert p0.precedes(p1);
        assert p7.precedes(p8);
        assert !p1.precedes(p0);
        assert !p8.precedes(p7);

        assert !p0.includes(p0);
        assert !p0.includes(p1);

        assert p0.follows(p0);
        assert p1.follows(p0);
        assert p8.follows(p7);
        assert !p0.follows(p1);
        assert !p7.follows(p8);

        assert p3.union(p2) == p3;
        assert p4.union(p5) == p4;

        assert p1.equals(p1);
        assert p1.equals(p9);
        assert !p1.equals(p2);
        assert !p4.equals(p3);

        Position r0 = rangePosition(0, 0, 99, 1);
        Position r1 = rangePosition(7, 1, 99, 1);
        Position r2 = rangePosition(9, 5, 15, 5);
        Position r3 = rangePosition(40, 9, 44, 9);
        Position r4 = rangePosition(42, 13, 42, 32);

        Position r5 = rangePosition(p0, p1);
        Position r6 = rangePosition(p2, p1);
        Position r7 = rangePosition(p3, p4);
        Position r8 = rangePosition(p5, p6);
        Position r9 = rangePosition(p7, p8);

        assert r0.getLine() == 0 && r0.getColumn() == 0;
        assert r1.getLine() == 7 && r1.getColumn() == 1;

        assert !(r1.getLine() == 99 || r1.getColumn() == 5);
        assert !(r0.getStart() == p0 || r0.getEnd() == p1);
        assert r5.getStart() == p0 && r5.getEnd() == p1;
        assert r6.getStart() == p2 && r6.getEnd() == p1;

        assert r3.isRange();
        assert r5.isRange();

        assert r2.precedes(r3);
        assert p5.precedes(r8);
        assert p5.precedes(r9);
        assert r2.precedes(p6);
        assert p5.precedes(r4);
        assert !r0.precedes(r1);
        assert !r3.precedes(r4);

        assert r0.includes(r0);
        assert r0.includes(r6);
        assert r3.includes(r9);
        assert r3.includes(p5);
        assert r3.includes(p6);
        assert r3.includes(p7);
        assert !r2.includes(r3);
        assert !r2.includes(p5);

        assert r3.follows(r2);
        assert r3.follows(p4);
        assert r3.follows(p5);
        assert p5.follows(r2);
        assert p6.follows(r4);
        assert r0.follows(r0);
        assert !r0.follows(r1);
        assert r1.follows(r1);
        assert !r3.follows(p6);
        assert !p2.follows(r0);
        assert !p5.follows(r4);

        Position U = r5.union(r5);
        assert U.getStart() == p0 && U.getEnd() == p1;
        U = r5.union(r6);
        assert U.getStart() == p0 && U.getEnd() == p1;
        U = r6.union(r5);
        assert U.getStart() == p0 && U.getEnd() == p1;
        Position p10 = position(10, 9);
        Position p11 = position(39, 9);
        Position r10 = rangePosition(p10, p11);
        U = r10.union(r8);
        assert U.getStart() == p10 && U.getEnd() == p6;
        U = r8.union(r10);
        assert U.getStart() == p10 && U.getEnd() == p6;
        assert r8.union(p11) == r8;
        assert p11.union(r8) == r8;

        assert r0.equals(r0);
        assert r1.equals(r1);
        assert r1.equals(r6);
        assert r6.equals(r1);
        assert !r0.equals(r1);
        assert !r1.equals(r0);
        assert !r5.equals(p1);
        assert !r6.equals(p2);
        assert !p1.equals(r5);
        assert !p2.equals(r6);

        assert p0.withEnd(p1).equals(r0);
        assert p0.withEnd(p1).equals(r5);
        assert p1.withStart(p2).equals(r6);

        assert r1.withStart(p0).equals(r5);
        assert r1.withEnd(p4).equals(rangePosition(p2, p4));

        assert p11.withEnd(r3).equals(rangePosition(p11, p6));

        Position head = headPosition();
        Position tail = tailPosition();
        Position none = noPosition();

        assert head.precedes(head);
        assert tail.precedes(tail);
        assert none.precedes(none);

        assert head.precedes(tail);
        assert !tail.precedes(head);
        assert !head.precedes(none);
        assert !tail.precedes(none);

        assert head.follows(head);
        assert tail.follows(tail);
        assert none.follows(none);

        assert !head.follows(tail);
        assert tail.follows(head);
        assert !head.follows(none);
        assert !tail.follows(none);

        assert head.precedes(p0);
        assert head.precedes(p1);
        assert head.precedes(r0);
        assert head.precedes(r1);

        assert !head.follows(p0);
        assert !head.follows(p1);
        assert !head.follows(r0);
        assert !head.follows(r1);

        assert p0.follows(head);
        assert p1.follows(head);
        assert r0.follows(head);
        assert r1.follows(head);

        assert !p0.precedes(head);
        assert !p1.precedes(head);
        assert !r0.precedes(head);
        assert !r1.precedes(head);

        assert !tail.precedes(p0);
        assert !tail.precedes(p1);
        assert !tail.precedes(r0);
        assert !tail.precedes(r1);

        assert tail.follows(p0);
        assert tail.follows(p1);
        assert tail.follows(r0);
        assert tail.follows(r1);

        assert !p0.follows(tail);
        assert !p1.follows(tail);
        assert !r0.follows(tail);
        assert !r1.follows(tail);

        assert p0.precedes(tail);
        assert p1.precedes(tail);
        assert r0.precedes(tail);
        assert r1.precedes(tail);

        assert !none.precedes(p0);
        assert !none.precedes(p1);
        assert !none.precedes(r0);
        assert !none.precedes(r1);

        assert !none.follows(p0);
        assert !none.follows(p1);
        assert !none.follows(r0);
        assert !none.follows(r1);

        assert !p0.follows(none);
        assert !p1.follows(none);
        assert !r0.follows(none);
        assert !r1.follows(none);

        assert !p0.precedes(none);
        assert !p1.precedes(none);
        assert !r0.precedes(none);
        assert !r1.precedes(none);
    }

}

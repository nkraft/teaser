package edu.ua.cs.teaser.cli;

import edu.ua.cs.teaser.cli.args.RealizedArgs;
import edu.ua.cs.teaser.javatext.ReadJavaDocument;
import edu.ua.cs.teaser.javatext.WriteJavaDocument;
import edu.ua.cs.teaser.javatext.rewrite.FoldComments;
import edu.ua.cs.teaser.javatext.rewrite.MarkLeadingComments;
import edu.ua.cs.teaser.javatext.rewrite.MergeComments;
import edu.ua.cs.teaser.javatext.rewrite.SplitNonidentifiers;
import edu.ua.cs.teaser.util.function.Functors;
import edu.ua.cs.teaser.util.function.Predicates;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import static rx.Observable.from;

public final class Rewrite {

    public static void execute(final RealizedArgs args) {
        final Scheduler cpu = Schedulers.computation();
        final Scheduler io = Schedulers.io();

        from(args.getInputFiles())
            .parallel(Functors.map(new ReadJavaDocument()), io)
            .filter(Predicates.javaDocumentHasTokens())
            .parallel(Functors.map(new MergeComments()), cpu)
            .parallel(Functors.map(new MarkLeadingComments()), cpu)
            .parallel(Functors.map(new FoldComments()), cpu)
            .parallel(Functors.map(new SplitNonidentifiers()), cpu)
            .observeOn(io)
            .toBlockingObservable()
            .forEach(new WriteJavaDocument(args.getOutputDir()));
    }
}

package edu.ua.cs.teaser.cli;

import edu.ua.cs.teaser.cli.args.RealizedArgs;
import edu.ua.cs.teaser.syntext.FormatSyntaxAnnotatedCorpus;
import edu.ua.cs.teaser.syntext.ReadSyntaxAnnotatedCorpus;
import edu.ua.cs.teaser.util.function.Functors;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import static rx.Observable.from;

public final class FormatCorpus {

    public static void execute(final RealizedArgs args) {
        final Scheduler io = Schedulers.io();

        from(args.getInputFiles())
            .parallel(Functors.map(new ReadSyntaxAnnotatedCorpus()), io)
            .observeOn(io)
            .toBlockingObservable()
            // TODO(nkraft): Pass the format or file ext as arg (to a factory?)
            // TODO(nkraft): Separate the format and write steps?
            .forEach(new FormatSyntaxAnnotatedCorpus(args.getOutputDir()));
    }
}

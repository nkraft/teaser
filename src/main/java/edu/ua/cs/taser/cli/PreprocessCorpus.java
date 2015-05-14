package edu.ua.cs.teaser.cli;

import edu.ua.cs.teaser.cli.args.RealizedArgs;
import edu.ua.cs.teaser.syntext.ReadSyntaxAnnotatedCorpus;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedCorpus;
import edu.ua.cs.teaser.syntext.WriteSyntaxAnnotatedCorpus;
import edu.ua.cs.teaser.syntext.preprocess.LowercaseTokens;
import edu.ua.cs.teaser.syntext.preprocess.RemoveStopTokens;
import edu.ua.cs.teaser.syntext.preprocess.SplitTokens;
import edu.ua.cs.teaser.syntext.preprocess.StemTokens;
import edu.ua.cs.teaser.syntext.preprocess.WeightTokens;
import edu.ua.cs.teaser.util.function.Functors;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import rx.util.functions.Func1;
import static rx.Observable.from;

public final class PreprocessCorpus {

    public static void execute(final RealizedArgs args) {
        final Scheduler cpu = Schedulers.computation();
        final Scheduler io = Schedulers.io();

        from(args.getInputFiles())
            .parallel(Functors.map(new ReadSyntaxAnnotatedCorpus()), io)
            .observeOn(Schedulers.newThread())
            .parallel(Functors.map(new Func1<SyntaxAnnotatedCorpus, SyntaxAnnotatedCorpus>() {
                @Override
                public SyntaxAnnotatedCorpus call(final SyntaxAnnotatedCorpus syncorp) {
                    from(syncorp.getDocuments())
                        .map(new RemoveStopTokens(syncorp, "java.lang"))
                        .map(new SplitTokens(syncorp, true))
                        .map(new LowercaseTokens(syncorp))
                        .map(new RemoveStopTokens(syncorp, "Fox"))
                        .map(new StemTokens(syncorp))
                        .map(new WeightTokens(syncorp))
                        .toBlockingObservable()
                        .last();
                    return syncorp;
                }
            }), cpu)
            .observeOn(io)
            .toBlockingObservable()
            .forEach(new WriteSyntaxAnnotatedCorpus(args.getOutputDir()));
    }
}

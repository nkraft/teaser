package edu.ua.cs.taser.cli;

import edu.ua.cs.taser.cli.args.RealizedArgs;
import edu.ua.cs.taser.javatext.ReadJavaDocument;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedCorpus;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedDocument;
import edu.ua.cs.taser.syntext.WriteSyntaxAnnotatedCorpus;
import edu.ua.cs.taser.util.function.Functors;
import edu.ua.cs.taser.util.function.Operators;
import edu.ua.cs.taser.util.function.Predicates;

import rx.schedulers.Schedulers;
import static rx.Observable.from;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

public final class BuildCorpus {

    public static void execute(final RealizedArgs args) {
        final List<SyntaxAnnotatedDocument> syndocs =
            Operators.flatten(
                from(args.getInputFiles())
                    .parallel(Functors.map(new ReadJavaDocument()), Schedulers.io())
                    .filter(Predicates.javaDocumentHasTokens())
                    .parallel(Functors.map(args.getCorpusType()), Schedulers.computation())
                    .observeOn(Schedulers.newThread())
            )
            .toList()
            .toBlockingObservable()
            .single();

        new WriteSyntaxAnnotatedCorpus(args.getOutputDir())
            .call(new SyntaxAnnotatedCorpus(args.getProject(), syndocs));
    }
}

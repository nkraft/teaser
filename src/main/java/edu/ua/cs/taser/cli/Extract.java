package edu.ua.cs.teaser.cli;

import edu.ua.cs.teaser.cli.args.RealizedArgs;
import edu.ua.cs.teaser.common.LoadFileContent;
import edu.ua.cs.teaser.javatext.ExtractJavaDocument;
import edu.ua.cs.teaser.javatext.WriteJavaDocument;
import edu.ua.cs.teaser.util.function.Functors;
import edu.ua.cs.teaser.util.function.Predicates;

import rx.schedulers.Schedulers;
import static rx.Observable.from;

public final class Extract {

    public static void execute(final RealizedArgs args) {
        from(args.getInputFiles())
            .parallel(Functors.map(new LoadFileContent()), Schedulers.io())
            .filter(Predicates.fileContentNotEmpty())
            .parallel(Functors.map(new ExtractJavaDocument(args.getJavaLexerType())), Schedulers.computation())
            .observeOn(Schedulers.io())
            .toBlockingObservable()
            .forEach(new WriteJavaDocument(args.getOutputDir()));
    }
}

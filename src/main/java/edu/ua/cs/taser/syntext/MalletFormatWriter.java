package edu.ua.cs.teaser.syntext;

import java.io.File;
import java.io.PrintWriter;

public class MalletFormatWriter {

    private final SyntaxAnnotatedCorpus syncorp;

    public MalletFormatWriter(final SyntaxAnnotatedCorpus syncorp) {
        this.syncorp = syncorp;
    }

    public void write(final String outputFilePath) {
        PrintWriter w = null;
        try {
            w = new PrintWriter(new File(outputFilePath));
            for (final SyntaxAnnotatedDocument syndoc : syncorp.getDocuments()) {
                w.print(syndoc.getName());
                for (final SyntaxAnnotatedToken token : syndoc.getTokens()) {
                    int weight = token.getWeight().intValue();
                    for (int i = 0; i < weight; ++i) {
                        w.print(' ');
                        w.print(token.getText());
                    }
                }
                w.println();
            }
        } catch (Exception e) {
            System.err.println("Failed to write file '" + outputFilePath + "'");
            System.err.println(e);
        } finally {
            if (w != null) {
                try {
                    w.flush();
                    w.close();
                } catch (Exception e) {
                    ;
                }
            }
        }
    }

}

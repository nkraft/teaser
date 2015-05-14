package edu.ua.cs.teaser.common;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class PorterStemmer implements Stemmer {

    private final SnowballStemmer stemmer = new englishStemmer();

    public String stem(final String s) {
        stemmer.setCurrent(s);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}

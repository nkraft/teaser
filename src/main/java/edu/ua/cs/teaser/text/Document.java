package edu.ua.cs.teaser.text;

import java.util.List;

public class Document {

    private String name;
    private List<Word> words;

    public Document(String name, List<Word> words) {
        this.name = name;
        this.words = words;
    }

    public String getName() {
        return name;
    }

    public List<Word> getWords() {
        return words;
    }
}

package edu.ua.cs.taser.text;

import java.util.ArrayList;
import java.util.List;

public class Corpus {

    private List<Document> documents;
    private String name;
    private List<String> pp;

    public Corpus(List<Document> documents, String name) {
        this(documents, name, new ArrayList<String>());
    }

    public Corpus(List<Document> documents, String name, List<String> pp) {
        this.name = name;
        this.documents = documents;
        this.pp = pp;
    }

    public void addPreprocessor(String p) {
        pp.add(p);
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public String getName() {
        return name;
    }

    public List<String> getPreprocessors() {
        return pp;
    }
}


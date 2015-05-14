package edu.ua.cs.teaser.syntext;

// import edu.ua.cs.teaser.document.EntityType;

// import java.util.ArrayList;
// import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
// import java.util.ListIterator;
// import java.util.Map;
// import java.util.Set;

public class SyntaxAnnotatedCorpus {

    private final String name;
    private final List<SyntaxAnnotatedDocument> syndocs;
    // private final Map<EntityType, List<Integer>> typeIndex;
    private final List<String> transformations;

    public SyntaxAnnotatedCorpus(final String name, final List<SyntaxAnnotatedDocument> syndocs) {
        this(name, syndocs, new LinkedList<String>());
    }

    public SyntaxAnnotatedCorpus(final String name, final List<SyntaxAnnotatedDocument> syndocs, final List<String> transformations) {
        this.name = name;
        this.syndocs = syndocs;
        // this.typeIndex = new HashMap<EntityType, List<Integer>>();
        this.transformations = transformations;
        // if (!syndocs.isEmpty()) {
        //     makeTypeIndex();
        // }
    }

    public void addTransformation(String name) {
        transformations.add(name);
    }

    public String getName() {
        return name;
    }

    // public Set<EntityType> getDocumentTypes() {
    //     return typeIndex.keySet();
    // }

    public List<SyntaxAnnotatedDocument> getDocuments() {
        return syndocs;
    }

    // public List<SyntaxAnnotatedDocument> getDocumentsOfType(final EntityType type) {
    //     List<SyntaxAnnotatedDocument> docs = new LinkedList<SyntaxAnnotatedDocument>();
    //     if (typeIndex.containsKey(type)) {
    //         for (Integer index : typeIndex.get(type)) {
    //             docs.add(syndocs.get(index));
    //         }
    //     }
    //     return docs;
    // }

    public List<String> getTransformations() {
        return transformations;
    }

    // private void makeTypeIndex() {
    //     final ListIterator<SyntaxAnnotatedDocument> it = syndocs.listIterator();
    //     while (it.hasNext()) {
    //         final int index = it.nextIndex();
    //         final SyntaxAnnotatedDocument syndoc = it.next();
    //         final EntityType type = syndoc.getType();
    //         List<Integer> indices = typeIndex.get(type);
    //         if (indices == null) {
    //             indices = new ArrayList<Integer>();
    //             typeIndex.put(type, indices);
    //         }
    //         indices.add(index);
    //     }
    // }
}

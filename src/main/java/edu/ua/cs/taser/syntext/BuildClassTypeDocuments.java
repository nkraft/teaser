package edu.ua.cs.taser.syntext;

import edu.ua.cs.taser.document.JavaEntity;
import edu.ua.cs.taser.javatext.JavaDocument;
import edu.ua.cs.taser.token.JavaToken;

import rx.util.functions.Func1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BuildClassTypeDocuments implements Func1<JavaDocument, List<SyntaxAnnotatedDocument>> {

    public List<SyntaxAnnotatedDocument> call(JavaDocument jdoc) {
        Map<String, SyntaxAnnotatedDocument> syndocMap = new HashMap<String, SyntaxAnnotatedDocument>();
        List<SyntaxAnnotatedDocument> syndocs = new LinkedList<SyntaxAnnotatedDocument>();
        for (JavaToken t : jdoc.getTokens()) {
            JavaEntity e = t.getEntity();
            if (e.getType().isClassType()) {
                addToken(e, t, syndocMap, syndocs);
            } else {
                e = e.getParent();
                while ((e != null) && (!e.getType().isClassType())) {
                    e = e.getParent();
                }
                if (e != null) {
                    addToken(e, t, syndocMap, syndocs);
                }
            }
        }
        return syndocs;
    }

    private static void addToken(JavaEntity e, JavaToken t, Map<String, SyntaxAnnotatedDocument> syndocMap, List<SyntaxAnnotatedDocument> syndocs) {
        String name = e.getName();
        if (!syndocMap.containsKey(name)) {
            SyntaxAnnotatedDocument d = new SyntaxAnnotatedDocument(e.getType(), name);
            syndocMap.put(name, d);
            syndocs.add(d);
        }
        syndocMap.get(name).getTokens().add(new SyntaxAnnotatedToken(t));
    }
}

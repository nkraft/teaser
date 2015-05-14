package edu.ua.cs.teaser.javatext;

import edu.ua.cs.teaser.document.JavaEntity;
import edu.ua.cs.teaser.javatext.JavaDocument;
import edu.ua.cs.teaser.token.JavaToken;
import edu.ua.cs.teaser.util.position.Position;
import edu.ua.cs.teaser.util.position.Positions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class JavaDocumentWriter {

    private final XMLOutputFactory factory = XMLOutputFactory.newInstance();
    private final Map<JavaEntity, String> entities = new IdentityHashMap<JavaEntity, String>();
    private final JavaDocument jdoc;

    public JavaDocumentWriter(final JavaDocument jdoc) {
        this.jdoc = jdoc;
    }

    public void write(final String outputFilePath) {
        XMLStreamWriter w = null;
        try {
            final FileWriter fw = new FileWriter(new File(outputFilePath));
            w = factory.createXMLStreamWriter(fw);
            w.writeStartDocument("utf-8", "1.0");
            w.writeCharacters("\n");
            w.writeStartElement("jdoc");
            w.writeAttribute("cUnit", jdoc.getCompilationUnit());
            w.writeAttribute("jls", jdoc.getJavaLanguageSpec());
            w.writeCharacters("\n");
            final List<String> rewrites = jdoc.getRewrites();
            for (String rw : rewrites) {
                w.writeEmptyElement("rw");
                w.writeAttribute("name", rw);
                w.writeCharacters("\n");
            }
            final ListIterator<JavaEntity> eit = jdoc.getEntities().listIterator();
            while (eit.hasNext()) {
                String id = String.valueOf(eit.nextIndex());
                JavaEntity entity = eit.next();
                entities.put(entity, id);
                writeJavaEntity(w, entity, id);
            }
            for (final JavaToken token : jdoc.getTokens()) {
                writeJavaToken(w, token);
            }
            w.writeEndElement();
            w.writeEndDocument();
        } catch (Exception e) {
            System.err.println("Failed to write file '" + outputFilePath + "'");
            System.err.println(e);
        } finally {
            if (w != null) {
                try {
                    w.flush();
                    w.close();
                } catch (XMLStreamException e) {
                    ;
                }
            }
        }
    }

    private void writeJavaEntity(XMLStreamWriter w, JavaEntity e, String id) throws XMLStreamException {
        w.writeStartElement("jent");
        w.writeAttribute("id", id);
        JavaEntity parent = e.getParent();
        if (parent != null) {
            w.writeAttribute("pid", entities.get(parent));
        }
        w.writeAttribute("type", e.getType().toString());
        writePosition(w, e.getPosition());
        w.writeCharacters(e.getName());
        w.writeEndElement();
        w.writeCharacters("\n");
    }

    private static void writePosition(XMLStreamWriter w, Position r) throws XMLStreamException {
        Position sp = r.getStart();
        Position ep = r.getEnd();
        w.writeAttribute("span", String.format("%d:%d:%d:%d", sp.getLine(), sp.getColumn(), ep.getLine(), ep.getColumn()));
    }

    private void writeJavaToken(XMLStreamWriter w, JavaToken t) throws XMLStreamException {
        w.writeStartElement("jtok");
        w.writeAttribute("eid", entities.get(t.getEntity()));
        w.writeAttribute("type", t.getType().toString());
        writePosition(w, t.getPosition());
        w.writeCharacters(t.getText());
        w.writeEndElement();
        w.writeCharacters("\n");
    }
}

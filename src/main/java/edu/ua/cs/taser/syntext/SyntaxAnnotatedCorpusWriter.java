package edu.ua.cs.taser.syntext;

import java.io.File;
import java.io.FileWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class SyntaxAnnotatedCorpusWriter {

    private final XMLOutputFactory factory = XMLOutputFactory.newInstance();
    private final SyntaxAnnotatedCorpus syncorp;

    public SyntaxAnnotatedCorpusWriter(final SyntaxAnnotatedCorpus syncorp) {
        this.syncorp = syncorp;
    }

    public void write(final String outputFilePath) {
        XMLStreamWriter w = null;
        try {
            final FileWriter fw = new FileWriter(new File(outputFilePath));
            w = factory.createXMLStreamWriter(fw);
            w.writeStartDocument("utf-8", "1.0");
            w.writeCharacters("\n");
            w.writeStartElement("syncorp");
            w.writeAttribute("name", syncorp.getName());
            w.writeCharacters("\n");
            for (final String tr : syncorp.getTransformations()) {
                w.writeEmptyElement("tr");
                w.writeAttribute("name", tr);
                w.writeCharacters("\n");
            }
            for (final SyntaxAnnotatedDocument syndoc : syncorp.getDocuments()) {
                w.writeStartElement("syndoc");
                w.writeAttribute("type", syndoc.getType().toString());
                w.writeAttribute("name", syndoc.getName());
                w.writeCharacters("\n");
                for (final SyntaxAnnotatedToken token : syndoc.getTokens()) {
                    writeToken(w, token);
                }
                w.writeEndElement();
                w.writeCharacters("\n");
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

    private void writeToken(XMLStreamWriter w, SyntaxAnnotatedToken t) throws XMLStreamException {
        w.writeStartElement("syntok");
        w.writeAttribute("type", t.getType().toString());
        w.writeAttribute("weight", t.getWeight().toString());
        w.writeCharacters(t.getText());
        w.writeEndElement();
        w.writeCharacters("\n");
    }
}

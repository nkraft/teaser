package edu.ua.cs.taser.syntext;

import edu.ua.cs.taser.document.EntityType;
import edu.ua.cs.taser.lang.Numbers;
import edu.ua.cs.taser.token.TokenType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class SyntaxAnnotatedCorpusReader {

    private enum CharactersAction { STORE, IGNORE };

    private final XMLInputFactory factory = XMLInputFactory.newInstance();
    private final Path path;

    public SyntaxAnnotatedCorpusReader(Path path) {
        this.path = path;
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    }

    public SyntaxAnnotatedCorpus read() {
        String cName = "-";
        List<SyntaxAnnotatedToken> syntoks = null;
        final List<SyntaxAnnotatedDocument> syndocs = new LinkedList<SyntaxAnnotatedDocument>();
        final List<String> transformations = new LinkedList<String>();
        InputStream is = null;
        try {
            is = Files.newInputStream(path, StandardOpenOption.READ);
            XMLEventReader r = factory.createXMLEventReader(is);
            CharactersAction action = CharactersAction.IGNORE;
            String[] rawTokenData = null;
            String[] rawDocumentData = null;
            while (r.hasNext()) {
                XMLEvent e = r.nextEvent();
                int etype = e.getEventType();
                switch (etype) {
                case XMLEvent.START_ELEMENT:
                    StartElement se = e.asStartElement();
                    String sen = se.getName().toString();
                    if ("syntok".equals(sen)) {
                        action = CharactersAction.STORE;
                        String type = se.getAttributeByName(new QName("type")).getValue();
                        String weight = se.getAttributeByName(new QName("weight")).getValue();
                        rawTokenData = new String[] {type, weight, null};
                    } else if ("syndoc".equals(sen)) {
                        syntoks = new LinkedList<SyntaxAnnotatedToken>();
                        String type = se.getAttributeByName(new QName("type")).getValue();
                        String name = se.getAttributeByName(new QName("name")).getValue();
                        rawDocumentData = new String[] {type, name};
                    } else if ("tr".equals(sen)) {
                        transformations.add(se.getAttributeByName(new QName("name")).getValue());
                    } else if ("syncorp".equals(sen)) {
                        cName = se.getAttributeByName(new QName("name")).getValue();
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    Characters c = e.asCharacters();
                    if (action == CharactersAction.STORE) {
                        rawTokenData[2] = c.getData();
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    action = CharactersAction.IGNORE;
                    final String een = e.asEndElement().getName().toString();
                    if ("syntok".equals(een)) {
                        syntoks.add(makeSyntaxAnnotatedToken(rawTokenData[0], rawTokenData[1], rawTokenData[2]));
                        rawTokenData = null;
                    } else if ("syndoc".equals(een)) {
                        syndocs.add(makeSyntaxAnnotatedDocument(rawDocumentData[0], rawDocumentData[1], syntoks));
                        rawDocumentData = null;
                    }
                    break;
                default:
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            ;
        } catch (IOException e) {
            ;
        } catch (XMLStreamException e) {
            ;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
        return new SyntaxAnnotatedCorpus(cName, syndocs, transformations);
    }

    private static SyntaxAnnotatedDocument makeSyntaxAnnotatedDocument(String type, String name, final List<SyntaxAnnotatedToken> tokens) {
        return new SyntaxAnnotatedDocument(EntityType.valueOf(type.toUpperCase(Locale.US)), name, tokens);
    }

    private static SyntaxAnnotatedToken makeSyntaxAnnotatedToken(String type, String weight, String text) {
        final SyntaxAnnotatedToken t = new SyntaxAnnotatedToken(TokenType.valueOf(type.toUpperCase(Locale.US)), text);
        t.setWeight(Numbers.parseNumber(weight, t.getWeight()));
        return t;

    }

    private static Number toNumber(String s) {
        Number n;
        try {
            n = Integer.decode(s);
        } catch (NumberFormatException e) {
            n = Double.valueOf(s);
        }
        return n;
    }
}

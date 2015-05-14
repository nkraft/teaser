package edu.ua.cs.teaser.javatext;

import edu.ua.cs.teaser.document.EntityType;
import edu.ua.cs.teaser.document.Entities;
import edu.ua.cs.teaser.document.JavaEntity;
import edu.ua.cs.teaser.token.JavaToken;
import edu.ua.cs.teaser.token.TokenType;
import edu.ua.cs.teaser.token.Tokens;
import edu.ua.cs.teaser.util.Lists;
import edu.ua.cs.teaser.util.position.Position;
import edu.ua.cs.teaser.util.position.PositionedComparator;
import edu.ua.cs.teaser.util.position.Positions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class JavaDocumentReader {

    private enum CharactersAction { STORE, IGNORE };

    private final XMLInputFactory factory = XMLInputFactory.newInstance();
    private final Path path;

    public JavaDocumentReader(Path path) {
        this.path = path;
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    }

    public JavaDocument read() {
        String cUnit = "-";
        String jls = "?";
        Map<Integer, String[]> rawEntities = new TreeMap<Integer, String[]>();
        List<String[]> rawTokens = new LinkedList<String[]>();
        List<String> rewrites = new LinkedList<String>();
        InputStream is = null;
        try {
            is = Files.newInputStream(path, StandardOpenOption.READ);
            XMLEventReader r = factory.createXMLEventReader(is);
            CharactersAction action = CharactersAction.IGNORE;
            Integer entityId = null;
            String[] rawData = null;
            while (r.hasNext()) {
                XMLEvent e = r.nextEvent();
                int etype = e.getEventType();
                switch (etype) {
                case XMLEvent.START_ELEMENT:
                    StartElement se = e.asStartElement();
                    String sen = se.getName().toString();
                    if ("jtok".equals(sen)) {
                        action = CharactersAction.STORE;
                        String entity = se.getAttributeByName(new QName("eid")).getValue();
                        String type = se.getAttributeByName(new QName("type")).getValue();
                        String span = se.getAttributeByName(new QName("span")).getValue();
                        rawData = new String[] {entity, null, type, span};
                    } else if ("jent".equals(sen)) {
                        action = CharactersAction.STORE;
                        entityId = Integer.parseInt(se.getAttributeByName(new QName("id")).getValue());
                        String type = se.getAttributeByName(new QName("type")).getValue();
                        String span = se.getAttributeByName(new QName("span")).getValue();
                        Attribute parent = se.getAttributeByName(new QName("pid"));
                        if (parent != null) {
                            rawData = new String[] {parent.getValue(), null, type, span};
                        } else {
                            rawData = new String[] {null, null, type, span};
                        }
                    } else if ("rw".equals(sen)) {
                        rewrites.add(se.getAttributeByName(new QName("name")).getValue());
                    } else if ("jdoc".equals(sen)) {
                        cUnit = se.getAttributeByName(new QName("cUnit")).getValue();
                        jls = se.getAttributeByName(new QName("jls")).getValue();
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    Characters c = e.asCharacters();
                    if (action == CharactersAction.STORE) {
                        rawData[1] = c.getData();
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    action = CharactersAction.IGNORE;
                    final String een = e.asEndElement().getName().toString();
                    if ("jtok".equals(een)) {
                        rawTokens.add(rawData);
                    } else if ("jent".equals(een)) {
                        rawEntities.put(entityId, rawData);
                        entityId = null;
                    }
                    rawData = null;
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
        Map<Integer, JavaEntity> entityMap = new TreeMap<Integer, JavaEntity>();
        for (Map.Entry<Integer, String[]> rawJavaEntity : rawEntities.entrySet()) {
            String[] v = rawJavaEntity.getValue();
            Integer pid = (v[0] != null) ? Integer.parseInt(v[0]) : null;
            JavaEntity e = (pid != null) ? entityMap.get(pid) : null;
            entityMap.put(rawJavaEntity.getKey(), makeJavaEntity(v[2], e, v[1], v[3]));
        }
        List<JavaToken> tokens = new LinkedList<JavaToken>();
        for (String[] rt : rawTokens) {
            Integer eid = Integer.parseInt(rt[0]);
            tokens.add(makeJavaToken(rt[2], rt[1], entityMap.get(eid), rt[3]));
        }
        List<JavaEntity> entities = new LinkedList<JavaEntity>();
        for (JavaEntity entity : entityMap.values()) {
            Lists.insort(entities, entity, PositionedComparator.positionedComparator());
        }
        return new JavaDocument(cUnit, jls.toLowerCase(Locale.US), entities, tokens, rewrites);
    }

    private static JavaEntity makeJavaEntity(String type, JavaEntity parent, String name, String span) {
        return Entities.newJavaEntity(EntityType.valueOf(type.toUpperCase(Locale.US)), parent, name, rangePosition(span.split(":")));
    }

    private static Position rangePosition(String[] r) {
        int[] p = new int[4];
        for (int i = 0; i < 4; ++i) {
            p[i] = Integer.parseInt(r[i]);
        }
        return Positions.rangePosition(p[0], p[1], p[2], p[3]);
    }

    private static JavaToken makeJavaToken(String type, String text, JavaEntity entity, String span) {
        return Tokens.newJavaToken(text, TokenType.valueOf(type.toUpperCase(Locale.US)), rangePosition(span.split(":")), entity);
    }
}

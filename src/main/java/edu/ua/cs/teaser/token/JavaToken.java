package edu.ua.cs.teaser.token;

import edu.ua.cs.teaser.document.JavaEntity;
import edu.ua.cs.teaser.util.position.Positionable;

public interface JavaToken extends Token, Positionable {
    public JavaEntity getEntity();
    public void setEntity(JavaEntity entity);
}

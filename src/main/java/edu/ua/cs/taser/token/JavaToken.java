package edu.ua.cs.taser.token;

import edu.ua.cs.taser.document.JavaEntity;
import edu.ua.cs.taser.util.position.Positionable;

public interface JavaToken extends Token, Positionable {
    public JavaEntity getEntity();
    public void setEntity(JavaEntity entity);
}

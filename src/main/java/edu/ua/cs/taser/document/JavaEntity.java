package edu.ua.cs.teaser.document;

import edu.ua.cs.teaser.util.position.Positionable;

public interface JavaEntity extends Entity, Positionable {
    public JavaEntity getParent();
}

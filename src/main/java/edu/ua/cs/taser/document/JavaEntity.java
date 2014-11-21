package edu.ua.cs.taser.document;

import edu.ua.cs.taser.util.position.Positionable;

public interface JavaEntity extends Entity, Positionable {
    public JavaEntity getParent();
}

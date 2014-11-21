package edu.ua.cs.taser.document;

public interface Entity {
    public String getName();
    public EntityType getType();
    public void setName(String name);
    public void setType(EntityType type);
}

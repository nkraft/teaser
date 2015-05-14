package edu.ua.cs.teaser.document;

import edu.ua.cs.teaser.util.position.Position;
import edu.ua.cs.teaser.util.position.Positions;

public final class Entities {

    public static Entity newEntity(String name, EntityType type) {
        return new BasicEntity(name, type);
    }

    public static JavaEntity newJavaEntity(EntityType type) {
        return newJavaEntity(type, null);
    }

    public static JavaEntity newJavaEntity(EntityType type, JavaEntity parent) {
        return newJavaEntity(type, parent, "(" + type.toString() + ")");
    }

    public static JavaEntity newJavaEntity(EntityType type, JavaEntity parent, String name) {
        return newJavaEntity(type, parent, name, Positions.tailPosition());
    }

    public static JavaEntity newJavaEntity(EntityType type, JavaEntity parent, String name, Position pos) {
        return new BasicJavaEntity(new BasicEntity(name, type), pos, parent);
    }

    public static JavaEntity noJavaEntity() {
        return NoJavaEntity.INSTANCE;
    }

    private static final class BasicEntity implements Entity {
        private String name;
        private EntityType type;

        BasicEntity(String name, EntityType type) {
            this.name = name;
            this.type = type;
        }

        @Override public String getName() { return name; }
        @Override public EntityType getType() { return type; }
        @Override public void setName(String name) { this.name = name; }
        @Override public void setType(EntityType type) { this.type = type; }

        @Override public String toString() {
            return String.format("%-12s %-64s", type.toString(), name);
        }
    }

    private static final class BasicJavaEntity implements JavaEntity {
        private final Entity entity;
        private Position pos;
        private JavaEntity parent;

        BasicJavaEntity(Entity entity, Position pos, JavaEntity parent) {
            this.entity = entity;
            this.pos = pos;
            this.parent = parent;
        }

        @Override public String getName() { return entity.getName(); }
        @Override public EntityType getType() { return entity.getType(); }
        @Override public void setName(String name) { entity.setName(name); }
        @Override public void setType(EntityType type) { entity.setType(type); }
        @Override public Position getPosition() { return pos; }
        @Override public void setPosition(Position pos) { this.pos = pos; }
        @Override public JavaEntity getParent() { return parent; }

        @Override public String toString() {
            String p = (parent != null) ? " ^(" + parent.getName() + ")" : "";
            return String.format("(%16s) %s %s", pos.toString(), entity.toString(), p);
        }
    }

    private static final class NoJavaEntity implements JavaEntity {
        public static final NoJavaEntity INSTANCE = new NoJavaEntity();

        @Override public String getName() { throw new UnsupportedOperationException(message("getName")); }
        @Override public EntityType getType() { throw new UnsupportedOperationException(message("getType")); }
        @Override public void setName(String name) { throw new UnsupportedOperationException(message("setName")); }
        @Override public void setType(EntityType type) { throw new UnsupportedOperationException(message("setType")); }
        @Override public Position getPosition() { return Positions.noPosition(); }
        @Override public void setPosition(Position pos) { throw new UnsupportedOperationException(message("setPosition")); }
        @Override public JavaEntity getParent() { return this; }

        @Override public String toString() { return "[NoJavaEntity]"; }

        private String message(String name) { return "NoJavaEntity." + name; }

        private NoJavaEntity() {}
    }

    private Entities() {}
}

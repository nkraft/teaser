package edu.ua.cs.taser.document;

import edu.ua.cs.taser.util.HierarchicalType;
import edu.ua.cs.taser.util.HierarchicalTypes;

import java.util.HashSet;
import java.util.Set;

public enum EntityType implements HierarchicalType<EntityType> {
    // Only leaf constants are concrete (interior nodes are abstract)
    ENTITY(null),
        CLASS_TYPE(ENTITY),
            CLASS(CLASS_TYPE),
            ENUM(CLASS_TYPE),
            INTERFACE(CLASS_TYPE),
            ANNOTATION(CLASS_TYPE),
        COMPILATION_UNIT(ENTITY),
            FILE(COMPILATION_UNIT),
        METHOD(ENTITY),
        ;

    private Set<EntityType> children = new HashSet<EntityType>();
    private EntityType parent;

    @Override
    public Set<EntityType> getAllChildren() {
        return HierarchicalTypes.getAllChildren(this);
    }

    @Override
    public Set<EntityType> getChildren() {
        return children;
    }

    @Override
    public EntityType getParent() {
        return parent;
    }

    public boolean isA(EntityType other) {
        return HierarchicalTypes.isChildOf(this, other);
    }

    @Override
    public boolean isChildOf(EntityType other) {
        return HierarchicalTypes.isChildOf(this, other);
    }

    public boolean isMethod() {
        return isChildOf(METHOD);
    }

    public boolean isClassType() {
        return HierarchicalTypes.isChildOf(this, CLASS_TYPE);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    private EntityType(EntityType parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.children.add(this);
        }
    }
}

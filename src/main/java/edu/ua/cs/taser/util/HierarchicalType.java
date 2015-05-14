package edu.ua.cs.teaser.util;

import java.util.Set;

public interface HierarchicalType<T extends Enum<T> & HierarchicalType<T>> {
    public Set<T> getAllChildren();
    public Set<T> getChildren();
    public T getParent();
    public boolean isChildOf(T other);
}

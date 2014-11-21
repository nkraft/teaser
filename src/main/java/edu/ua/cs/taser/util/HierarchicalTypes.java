package edu.ua.cs.taser.util;

import java.util.HashSet;
import java.util.Set;

public final class HierarchicalTypes {

    public static <T extends Enum<T> & HierarchicalType<T>>
            void addAllChildren(T root, Set<T> allChildren) {
        Set<T> children = root.getChildren();
        allChildren.addAll(children);
        for (T child : children) {
            addAllChildren(child, allChildren);
        }
    }

    public static <T extends Enum<T> & HierarchicalType<T>>
            Set<T> getAllChildren(T root) {
        Set<T> allChildren = new HashSet<T>();
        addAllChildren(root, allChildren);
        return allChildren;
    }

    public static <T extends Enum<T> & HierarchicalType<T>>
            boolean isChildOf(T type, T candidate) {
        if (type == null || candidate == null) {
            return false;
        }
        for (T t = type; t != null; t = t.getParent()) {
            if (candidate == t) {
                return true;
            }
        }
        return false;
    }

    private HierarchicalTypes() {}
}

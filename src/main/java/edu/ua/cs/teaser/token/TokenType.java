package edu.ua.cs.teaser.token;

import edu.ua.cs.teaser.util.HierarchicalType;
import edu.ua.cs.teaser.util.HierarchicalTypes;

import java.util.HashSet;
import java.util.Set;

public enum TokenType implements HierarchicalType<TokenType> {
    // Only leaf constants are concrete (interior nodes are abstract)
    TOKEN(null),
        COMMENT(TOKEN),
            BLOCK_COMMENT(COMMENT),
            LINE_COMMENT(COMMENT),
                MERGED_COMMENT(LINE_COMMENT),
            LEADING_COMMENT(COMMENT),
                CLASS_COMMENT(LEADING_COMMENT),
                METHOD_COMMENT(LEADING_COMMENT),
        IDENTIFIER(TOKEN),
            NAME_DECL(IDENTIFIER),
                TYPE_NAME(NAME_DECL),
                    CLASS_TYPE_NAME(TYPE_NAME),
                        CLASS_NAME(CLASS_TYPE_NAME),
                        ENUM_NAME(CLASS_TYPE_NAME),
                        INTERFACE_NAME(CLASS_TYPE_NAME),
                        ANNOTATION_NAME(CLASS_TYPE_NAME),
                    TYPE_PARAMETER_NAME(TYPE_NAME),
                MEMBER_NAME(NAME_DECL),
                    FIELD_NAME(MEMBER_NAME),
                    METHOD_NAME(MEMBER_NAME),
                    ENUM_CONSTANT_NAME(MEMBER_NAME),
                    ANNOTATION_ELEMENT_NAME(MEMBER_NAME),
                VARIABLE_NAME(NAME_DECL),
                    PARAMETER_NAME(VARIABLE_NAME),
                    LOCAL_VARIABLE_NAME(VARIABLE_NAME),
                LABEL_NAME(NAME_DECL),
            NAME_USE(IDENTIFIER),
                TYPE_REF(NAME_USE),
                    ANNOTATION_REF(TYPE_REF),
                    CLASS_OR_INTERFACE_REF(TYPE_REF),
                    PARAMETER_TYPE(TYPE_REF),
                    THROWS(TYPE_REF),
                PRIMARY_NAME_REF(NAME_USE),
                QUALIFIED_NAME_REF(NAME_USE),
                MEMBER_REF(NAME_USE),
                    ENUM_CONSTANT_REF(MEMBER_REF),
                    METHOD_REF(MEMBER_REF),
                        CONSTRUCTOR_CALL(METHOD_REF),
                        METHOD_CALL(METHOD_REF),
                    ANNOTATION_ELEMENT_REF(MEMBER_REF),
                LABEL_REF(NAME_USE),
        LITERAL(TOKEN),
            STRING_LITERAL(LITERAL),
        ;

    private Set<TokenType> children = new HashSet<TokenType>();
    private TokenType parent;

    @Override
    public Set<TokenType> getAllChildren() {
        return HierarchicalTypes.getAllChildren(this);
    }

    @Override
    public Set<TokenType> getChildren() {
        return children;
    }

    @Override
    public TokenType getParent() {
        return parent;
    }

    public boolean isA(TokenType other) {
        return HierarchicalTypes.isChildOf(this, other);
    }

    @Override
    public boolean isChildOf(TokenType other) {
        return HierarchicalTypes.isChildOf(this, other);
    }

    public boolean isComment() {
        return isChildOf(COMMENT);
    }

    public boolean isIdentifier() {
        return isChildOf(IDENTIFIER);
    }

    public boolean isLiteral() {
        return isChildOf(LITERAL);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    private TokenType(TokenType parent) {
        this.parent = parent;
        if (this.parent != null) {
            this.parent.children.add(this);
        }
    }
}

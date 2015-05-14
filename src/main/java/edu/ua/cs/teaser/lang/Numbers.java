package edu.ua.cs.teaser.lang;

public final class Numbers {

    public static boolean hasDigit(final String str) {
        final int length = str.length();
        for (int i = 0; i < length; ++i) {
            if (Character.isDigit(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDouble(final String str) {
        try {
            Double.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(final String str) {
        try {
            Long.decode(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumber(final String str) {
        return (isLong(str) || isDouble(str));
    }

    public static boolean isNumeric(final String str) {
        // TODO(nkraft) Speed this up.

        // Disallow "NaN" and "Infinity" (allowed by Double.valueOf)
        final char first = str.charAt(0);
        if (first == 'N' || first == 'I') return false;

        // Disallow hex strings without digits (e.g., "fade")
        if (!hasDigit(str)) return false;

        return isNumber(str);
    }

    public static Number parseNumber(String string) {
        return parseNumber(string, null);
    }

    public static Number parseNumber(String string, Number defaultValue) {
        if (string == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e1) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e2) {
                return defaultValue;
            }
        }
    }

    private Numbers() {}
}

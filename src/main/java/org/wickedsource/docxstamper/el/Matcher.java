package org.wickedsource.docxstamper.el;

/**
 * Represents a Matcher that checks if an expression starts with a specified prefix and ends with a specified suffix.
 *
 * @author Joseph Verron
 * @version ${version}
 * @since 1.6.5
 */
public record Matcher(String prefix, String suffix) {
    boolean match(String expression) {
        assertNotNull(expression);
        return expression.startsWith(prefix())
               && expression.endsWith(suffix());
    }

    private static void assertNotNull(String expression) {
        if (expression == null)
            throw new IllegalArgumentException("Cannot strip NULL expression!");
    }

    /**
     * Strips the prefix and suffix from the given expression.
     * @param expression the expression to strip.
     * @return the stripped expression.
     */
    public String strip(String expression) {
        assertNotNull(expression);
        int start = prefix.length();
        int end = expression.length() - suffix.length();
        return expression.substring(start, end);
    }
}

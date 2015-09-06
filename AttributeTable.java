import java.util.ArrayList;
import java.util.Collections;

class AttributeTable {							
    public static enum Type {WHITESPACE, DIGIT, LETTER, BRACKET, DELIMITER, DOLLAR, FORBIDDEN};
    private static ArrayList<Type> attributes = new ArrayList<Type>(Collections.nCopies(256, Type.FORBIDDEN));

    static {
        for (int i = 9; i <= 13; i++)
            attributes.set(i, Type.WHITESPACE);
        attributes.set(' ', Type.WHITESPACE);
        attributes.set(',', Type.DELIMITER);
        attributes.set(';', Type.DELIMITER);
        attributes.set(')', Type.DELIMITER);
        attributes.set(':', Type.DELIMITER);
        attributes.set('(', Type.BRACKET);
        attributes.set('$', Type.DOLLAR);
        for (int i = 48; i < 58; i++)
            attributes.set(i, Type.DIGIT);
        for (int i = 65; i < 91; i++)
            attributes.set(i, Type.LETTER);
    }

    public static Type getAttribute(char c) {
        return attributes.get(c);
    }

    public static Type getAttribute(int c) {
        if (c<=0)
            return Type.FORBIDDEN;
        return getAttribute((char) c);
    }
}

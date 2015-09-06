import java.util.ArrayList;

class DelimiterTable {
    public static final int DELIMITER_MIN_CODE = 301;
    private static ArrayList<Character> singleDelimiters = new ArrayList<Character>();
    private static ArrayList<String> doubleDelimiters = new ArrayList<String>();

    static {
        singleDelimiters.add('(');
        singleDelimiters.add(')');
    	singleDelimiters.add(',');
    	singleDelimiters.add(':');
        singleDelimiters.add(';');

        doubleDelimiters.add("($");
        doubleDelimiters.add("$)");
    }

    public static int getSingleDelemiterCode(char delimiter) {
        if (singleDelimiters.indexOf(delimiter)!=-1)
            return (int)delimiter;
        return -1;
    }

    public static int getDoubleDelemiterCode(String delimiter) {
        return DELIMITER_MIN_CODE + doubleDelimiters.indexOf(delimiter);
    }

    public static ArrayList<String> getDoubleDelimiters(){
        return (ArrayList<String>)doubleDelimiters.clone();
    }

    public static ArrayList<Character> getSingleDelimiters(){
        return (ArrayList<Character>)singleDelimiters.clone();
    }
}


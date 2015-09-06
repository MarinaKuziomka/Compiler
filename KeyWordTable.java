import java.util.ArrayList;

class KeyWordTable {
    public static final int KEY_WORD_MIN_CODE = 401;
    private static ArrayList<String> keyWords = new ArrayList<String>();

    static {
        keyWords.add("PROCEDURE");
        keyWords.add("BEGIN");
        keyWords.add("END");
        keyWords.add("LABEL");
        keyWords.add("GOTO");
        keyWords.add("RETURN");
    }

    public static int getCode(String keyword) {
        int id = keyWords.indexOf(keyword);
        if (id != -1)
            return KEY_WORD_MIN_CODE + id;
        return -1;
    }

    public static ArrayList<String> getKeyWords(){
        return (ArrayList<String>)keyWords.clone();
    }
}


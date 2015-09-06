import java.util.ArrayList;

class MyReader{
    private int position=0;
    private int lenght=0;
    private int row=1;
    private String str;

    MyReader(String str){
        this.str=new String(str);             
        lenght=str.length();
    }
    
    public int read(){
        if (position<lenght){
        	if (str.charAt(position) == '\n')
        		row++;
            return str.charAt(position++);
        }
        return -1;
    }
    
    public int getRow(){
    	return row;
    }
}

class LexAnalize {
    private IdentifiersTable idnTable = new IdentifiersTable();
    private ConstantsTable constTable = new ConstantsTable();
    private MyReader reader;
    private ArrayList<Lexem> lexems = new ArrayList<Lexem>();
    private int c;
    
    private void addLexem(int id){
    	lexems.add(new Lexem(id, reader.getRow()));
    }

    private void processWhitespace(){
        while ((c = reader.read()) != -1) {
            if (AttributeTable.getAttribute(c) != AttributeTable.Type.WHITESPACE)
                break;
        }
    }

    private void processDigit(){
    	StringBuffer sb = new StringBuffer();
        sb.append((char) c);
        while ((c = reader.read()) != -1 && AttributeTable.getAttribute(c) == AttributeTable.Type.DIGIT) {
            sb.append((char) c);
        }

        Long elem=Long.parseLong(sb.toString());
        int constId=constTable.getCode(elem);

        if (constId != -1)
            addLexem(constId);
        else
            addLexem(constTable.append(elem));
    }

    private void processLetter(){
        StringBuffer sb = new StringBuffer();
        sb.append((char) c);
        AttributeTable.Type attr;
        while ((c = reader.read()) != -1 &&
                ((attr = AttributeTable.getAttribute(c)) == AttributeTable.Type.LETTER || attr == AttributeTable.Type.DIGIT)) {
            sb.append((char) c);
        }
        int keyWord = KeyWordTable.getCode(sb.toString());
        if (keyWord != -1)
            addLexem(keyWord);
        else {
            int idn = idnTable.getCode(sb.toString());
            if (idn != -1)
                addLexem(idn);
            else
                addLexem(idnTable.append(sb.toString()));
        }
    }

    private void processDelimiter(){
        addLexem(DelimiterTable.getSingleDelemiterCode((char) c));
        c = reader.read();
    }

    private void processDollar(){
        if ((c = reader.read()) == -1){
        	addLexem(c);
        	c = reader.read();
            return;
        }
    	if (c != ')'){
    		addLexem(-4);
        	c = reader.read();
    	}
        else {
            addLexem(DelimiterTable.getDoubleDelemiterCode("$)"));
            c = reader.read();
        }
    }

    private void processBracket(){
        c=reader.read();
        if (c!=-1) {
            if (c == '*') {
                c = reader.read();
                if (c == -1){
                	addLexem(c);
                	c = reader.read();
                    return;
                }
                while ((c != -1) && (c != '*'))
                    c = reader.read();
                if (c == -1) {
                	addLexem(c);
                	c = reader.read();
                    return;
                }
                if ((c = reader.read()) != -1) {
                    if (c != ')') {
                        c=1;
                        checkAttribute();
                    }
                    c = reader.read();
                }
            } else 
            if (c == '$'){
            	addLexem(DelimiterTable.getDoubleDelemiterCode("($"));
                c = reader.read();
            } else {
            	addLexem(DelimiterTable.getSingleDelemiterCode('('));
                c = reader.read();
            }
        }else{
        	addLexem(c);
        	c = reader.read();
        }
    }

    private void checkAttribute(){
        switch (AttributeTable.getAttribute(c)) {
            case WHITESPACE:
                processWhitespace();
                break;
            case DIGIT:
                processDigit();
                break;
            case LETTER:
                processLetter();
                break;
            case DELIMITER:
                processDelimiter();
                break;
            case DOLLAR:
                processDollar();
                break;
            case BRACKET:
                processBracket();
                break;
            case FORBIDDEN:
                c=reader.read();
                addLexem(-2);
                break;
        }
    }

    private ArrayList<Lexem> parse(String str){
        reader= new MyReader(str);
        c = reader.read();
        while (true) {
            checkAttribute();
            if (c == -1){
            	addLexem(0);
                break;
            }
        }
        return lexems;
    }

    public ResultLexAnalize lexicalAnalize(String str) {
        ArrayList<Lexem> errors = new ArrayList<Lexem>();
        lexems = parse(str);
        for (Lexem lex:lexems){
            if (lex.id < 0){
                errors.add(lex);
            }
        }
        boolean good = true;
        if (errors.size()>0)
        	good = false;
        return new ResultLexAnalize(good, errors,idnTable, constTable, lexems,str);
        
    }

}

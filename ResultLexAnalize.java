import java.util.ArrayList;

class ResultLexAnalize {
    public IdentifiersTable idnTable;
    public ConstantsTable constTable;
    public ArrayList<Lexem> lexems;
    public ArrayList<Lexem> errors;
    public boolean status;
    public String source;
    
    ResultLexAnalize(boolean status, ArrayList<Lexem> errors, IdentifiersTable idnTable, ConstantsTable constTable, ArrayList<Lexem> lexems,String source) {
        this.status = status;
        this.errors = errors;
        this.constTable = constTable;
        this.idnTable = idnTable;
        this.lexems = lexems;
        this.source = source;
    }
}

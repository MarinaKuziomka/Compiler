import java.util.ArrayList;

public class ResultSyntaxAnalize {
    public ArrayList<Lexem> errors;
    public MyMutableTreeNode tree;
    public boolean status;
    public IdentifiersTableFull identifiersTable;
    public String source;

    public ResultSyntaxAnalize(boolean status, ArrayList<Lexem> errors,MyMutableTreeNode tree, IdentifiersTableFull identifiersTable,String source){
        this.status = status;
        this.errors = errors;
        this.tree = tree;
        this.identifiersTable = identifiersTable;
        this.source = source;
    }
}

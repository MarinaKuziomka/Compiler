import java.util.ArrayList;

public class SyntaxAnalize {
	private ArrayList<Lexem> lexems;
	private int position;
	private IdentifiersTable identifiersTable;
	private ConstantsTable constantsTable;
    private ArrayList<Lexem> errors = new ArrayList<Lexem>();
    private boolean status = false;
    private int row;
    private int lexem;
    private IdentifiersTableFull identifierTableFull = new IdentifiersTableFull();
    private ArrayList<Lexem> activeIdentifiers = new ArrayList<Lexem>();    
    
    public ResultSyntaxAnalize synataxAnalise(ResultLexAnalize resultLexAnalize){
        position = 0;
        lexems = resultLexAnalize.lexems;
        identifiersTable = resultLexAnalize.idnTable;
        constantsTable = resultLexAnalize.constTable;
        
        for(int i=0;i<lexems.size();i++){
        	if (lexems.get(i).id <0)
        		lexems.remove(i--);
        }
        
        MyMutableTreeNode tree = signal_program();

        for(Lexem error:errors){
            System.out.format("       :(%d) %s\n", error.row, ErrorTable.getSyntaxError(error.id));
        }
        return new ResultSyntaxAnalize(status,errors,tree, identifierTableFull, resultLexAnalize.source);
    }

    private Lexem get(){
        try 
        {
            while(lexems.get(position).id<0)
                position++;
            row = lexems.get(position).row;
            return lexems.get(position++);
        }
        catch (Exception e){
            return null;
        }
    }
    
    private Lexem current(){
        return lexems.get(position);
    }

    private void addError(int id){
        errors.add(new Lexem(id, row));
    }

    private void updateParameters(){
        for(Lexem lex:activeIdentifiers){
            identifierTableFull.parameters.add(new Identifier(lex, identifiersTable.get(lex.id)));
        }
        activeIdentifiers.clear();
    }
    
    private void updateLabels(){
        for(Lexem lex:activeIdentifiers){
            identifierTableFull.labels.put(lex.id,new Identifier(lex, constantsTable.get(lex.id).toString()));
        }
        activeIdentifiers.clear();
    }
    
    private MyMutableTreeNode signal_program(){
    	MyMutableTreeNode root= new MyMutableTreeNode("<signal-program>");
        if (program(root)){
            status = true;
            return root;
        }
        return null;
    }
    
    private boolean program(MyMutableTreeNode parent){
    	MyMutableTreeNode internal = new MyMutableTreeNode("<program>");
        String code1 = "PROCEDURE";
        char code2 = ';';

        if (get().id == KeyWordTable.getCode(code1)) {
            internal.add(new MyMutableTreeNode(code1, lexem));
            if (procedure_identifier(internal)) {
            	if (parameters_list(internal)){
            		if (get().id == DelimiterTable.getSingleDelemiterCode(code2)) {
            			internal.add(new MyMutableTreeNode(code2, lexem));
            			if (block(internal)) {
            				if (get().id == DelimiterTable.getSingleDelemiterCode(code2)) {
            					internal.add(new MyMutableTreeNode(code2, lexem));
            					parent.add(internal);
            					return true;
            				} 
            				else
            					addError(-11);
            			}
            		}
            		else
            			addError(-11);
            	}
            }
        }
        else
            addError(-10);
        return false;
    }

    private boolean block(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<block>");
        String code1 = "BEGIN";
        String code2 = "END";
        if (declarations(internal)){
            if (get().id == KeyWordTable.getCode(code1)) {
                internal.add(new MyMutableTreeNode(code1, lexem));
                if (statements_list(internal)){
                    if (get().id == KeyWordTable.getCode(code2)){
                        internal.add(new MyMutableTreeNode(code2, lexem));
                        parent.add(internal);
                        return true;
                    }
                    else
                    	addError(-13);
                }
            }
            else
            	addError(-12);
        }
        return false;
    }

    private boolean procedure_identifier(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<procedure-identifier>");
         if (identifier(internal)){
             parent.add(internal);
             identifierTableFull.procedureName = new Identifier(activeIdentifiers.get(0),identifiersTable.get(activeIdentifiers.get(0).id));
             activeIdentifiers.clear();
             return true;
         }
     return false;
    }

    private boolean identifier(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<identifier>");
        Lexem idn = get();
        if (identifiersTable.get(idn.id)!=null){
            internal.add(new MyMutableTreeNode(identifiersTable.get(idn.id),idn.id));
            activeIdentifiers.add(idn);
            parent.add(internal);
            return true;
    }
        else
        	addError(-30);
        return false;
    }
    
    private boolean parameters_list(MyMutableTreeNode parent){
    	MyMutableTreeNode internal = new MyMutableTreeNode("<parameters-list>");
        char code1 = '(';
        char code2 = ')';
        char nextCode = ';';
        
        if (current().id == DelimiterTable.getSingleDelemiterCode(nextCode)){
            if (empty(internal)){
                parent.add(internal);
                return true;
            }
        }
        activeIdentifiers.clear();
        if (get().id == DelimiterTable.getSingleDelemiterCode(code1)){
            internal.add(new MyMutableTreeNode((char)code1, lexem));
            if (variable_identifier(internal)){	
                if (identifiers_list(internal)){
                    if (get().id == DelimiterTable.getSingleDelemiterCode(code2)){
                        internal.add(new MyMutableTreeNode(code2, lexem));
                        updateParameters();
                        parent.add(internal);
                    	return true;
                    }
                    else 
                    	addError(-16);
                }
            }
        }
        else
            addError(-17);
        return false;
    }
    
    private boolean empty(MyMutableTreeNode parent){
        parent.add(new MyMutableTreeNode("<empty>"));
        return true;
    }
    
    private boolean variable_identifier(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<variable-identifier>");
         if (identifier(internal)){
             parent.add(internal);
             return true;
         }
         return false;
    }
    
    private boolean identifiers_list(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<identifiers-list>");
        char code1 = ',';
        char nextCode = ')';

        if (current().id == DelimiterTable.getSingleDelemiterCode(nextCode)){
            if (empty(internal)){
                parent.add(internal);
                return true;
            }
        }
        if (get().id == DelimiterTable.getSingleDelemiterCode(code1)){
            internal.add(new MyMutableTreeNode((char)code1, lexem));
            if (variable_identifier(internal)){
                if (identifiers_list(internal)){
                    parent.add(internal);
                    return true;
                }
            }
        }
        else
            addError(-18);
        return false;
    }
    
    private boolean declarations(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<declaration>");
        if (label_declarations(internal)) {
            parent.add(internal);
            return true;
        }
        return false;
    }

    private boolean label_declarations(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<lable-declarations>");
        String code1 = "LABEL";
        char code2 = ';';        
        String nextCode = "BEGIN";
        
        if (current().id == KeyWordTable.getCode(nextCode)){
            if (empty(internal)) {
                parent.add(internal);
                return true;
            }
        }
        activeIdentifiers.clear();
        if (get().id == KeyWordTable.getCode(code1)){
            internal.add(new MyMutableTreeNode(code1, lexem));
            if (unsigned_integer(internal)){
            	if (labels_list(internal)){
                    if (get().id == DelimiterTable.getSingleDelemiterCode(code2)){
                        internal.add(new MyMutableTreeNode(code2, lexem));
                        updateLabels();
                    	parent.add(internal);
                    	return true;
                    }
                    else
                    	addError(-11);
            	}
            }
        }
        else
            addError(-14);
        return false;
    }

    private boolean unsigned_integer(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<unsigned-integer>");
        Lexem idn = get();
    	Long constant = constantsTable.get(idn.id);
        if (constant!=null){
        	internal.add(new MyMutableTreeNode(constant.toString(),idn.id));
        	activeIdentifiers.add(idn);
        	parent.add(internal);
        	return true;
        }
        else
        	addError(-31);
        return false;
    }
    
    private boolean unsigned_integer_1(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<unsigned-integer>");
    	Lexem idn = get();
    	Long constant = constantsTable.get(idn.id);
        if (constant!=null){
        	internal.add(new MyMutableTreeNode(constant.toString(),idn.id));
        	parent.add(internal);
        	return true;
        }
        return false;
    }


    private boolean labels_list(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<labels-list>");
        char code1 = ',';
        char nextCode = ';';

        if (current().id == DelimiterTable.getSingleDelemiterCode(nextCode)){
            if (empty(internal)){
                parent.add(internal);
                return true;
            }
        }
        if (get().id == DelimiterTable.getSingleDelemiterCode(code1)){
            internal.add(new MyMutableTreeNode((char)code1, lexem));
            if (unsigned_integer(internal)){
                if (labels_list(internal)){
                    parent.add(internal);
                    return true;
                }
            }
        }
        else
            addError(-19);
        return false;
    }
    
    private boolean statements_list(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<statements_list>");       
        String nextCode = "END";

        if (current().id == KeyWordTable.getCode(nextCode)){
            if (empty(internal)){
                parent.add(internal);
                return true;
            }
        }
        if (statement(internal)){
        	if (statements_list(internal)){
        		parent.add(internal);
                return true;
            }
        }
        return false;
    }
    
    private boolean statement(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<statement>");       
        int id = current().id;
        char code0 = ':';
        String code1 = "GOTO";
        String code2 = "RETURN";
        char code3 = ';';
        String code4 = "($";
        String code5 = "$)";
        
        if (unsigned_integer_1(internal)){
        	if (get().id == DelimiterTable.getSingleDelemiterCode(code0)){
        		internal.add(new MyMutableTreeNode(code0, lexem));
        		if (statement(internal)){
        			parent.add(internal);
                    return true;
        		}
        		else
        			return false;
        	}
        	else {
    			addError(-15);
    			return false;
    		}
        }
        
        if (id == KeyWordTable.getCode(code1)){
    		internal.add(new MyMutableTreeNode(code1));
        	if (unsigned_integer(internal)){
        		if (get().id == DelimiterTable.getSingleDelemiterCode(code3)){
            		internal.add(new MyMutableTreeNode(code3, lexem));
            		parent.add(internal);
            		return true;
        		}
        		else {
        			addError(-11);
        			return false;
        		}
        	}
        }
        
        if (id == KeyWordTable.getCode(code2)){
    		internal.add(new MyMutableTreeNode(code2));
    		if (get().id == DelimiterTable.getSingleDelemiterCode(code3)){
        		internal.add(new MyMutableTreeNode(code3, lexem));
        		parent.add(internal);
        		return true;
    		}
    		else {
    			addError(-11);
    			return false;
    		}
        }
        
        if (id == DelimiterTable.getSingleDelemiterCode(code3)){
        	internal.add(new MyMutableTreeNode(code3, lexem));
        	parent.add(internal);
        	return true;
        }
        
        if (id == DelimiterTable.getDoubleDelemiterCode(code4)){
    		internal.add(new MyMutableTreeNode(code4, lexem));
        	if(assembly_insert_file_identifier(internal)){
                if (get().id == DelimiterTable.getDoubleDelemiterCode(code5)){
            		internal.add(new MyMutableTreeNode(code5, lexem));
            		parent.add(internal);
            		return true;
                }
                else
                	addError(-21);
        	}
        }
        else
        	addError(-20);
 
        return false;
    }
 
    private boolean assembly_insert_file_identifier(MyMutableTreeNode parent) {
    	MyMutableTreeNode internal = new MyMutableTreeNode("<assembly-insert-file-identifier>");
         if (identifier(internal)){
             parent.add(internal);
             return true;
         }
         return false;
    }
    
}

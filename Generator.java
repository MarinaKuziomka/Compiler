import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Generator {
	private ArrayList<String> asmCode = new ArrayList<String>();
    private ArrayList<Lexem> errors = new ArrayList<Lexem>();
    private int row;
    private ArrayList<Integer> allowedLabels;
    private MyMutableTreeNode statementsList;
    private HashMap<Integer,Identifier> labels;
    private ArrayList<Identifier> parameters;
    private ArrayList<Integer> declaratedLabels = new ArrayList<Integer>();
	
    private void addError(int id){
        asmCode.add(String.format(";Помилка: %s",ErrorTable.getError(id)));
        errors.add(new Lexem(id, row));
    }
    
    private void addSourceRow(String str){
        asmCode.add(String.format(";(%d) %s",row,str));
    }
    
    public ResultGenerator generate (ResultSyntaxAnalize resultSyntaxAnalize){
    	IdentifiersTableFull identifierTableFull = resultSyntaxAnalize.identifiersTable;
    	MyMutableTreeNode treeNode = resultSyntaxAnalize.tree;
        ArrayList<String> sourceArray = new ArrayList<String>(Arrays.asList(resultSyntaxAnalize.source.split(String.format("%c%c", (char) 13, (char) 10))));
        parameters = identifierTableFull.parameters;
        labels = identifierTableFull.labels;

        Identifier parameter;
        boolean error;
    	
    	String value;
        asmCode.add(".386");
        asmCode.add(".DATA");
        
        asmCode.add(String.format("%-8s%s", identifierTableFull.procedureName.name, "PROC"));
        
        asmCode.add(String.format("\t%-8s%s", "PUSH", "EBP"));
        asmCode.add(String.format("\t%-8s%s", "MOV", "EBP, ESP"));
        asmCode.add(String.format("\t%-8s%s\n", "PUSH", "ESI"));
        
        if (parameters.size()!=0){
        	
        	int count = 0;
        	for (int i=0;i<parameters.size();i++){
        		parameter =parameters.get(i);
        		row = parameter.lexem.row;
        		
                error = false;
            	if (parameter.lexem.id == identifierTableFull.procedureName.lexem.id){
            		addError(-42);
            		error = true;
            		continue;
            	}
                for(int j=0;j<i;j++)
                    if(parameter.lexem.id == parameters.get(j).lexem.id){
                        addError(-40);
                        error = true;
                        //якщо тільки 1 раз
                        break;
                    }
                
                if (!error){
                    asmCode.add(String.format("\t%-8s%-8s%s%d%s", parameter.name, "EQU", "[EBP + ", count+=8, "]"));
                }
        	}	
        }
        
        asmCode.add("");
        statementsList = treeNode.getChildren(0).getChildren(4).getChildren(2);
        allowedLabels = new ArrayList<Integer>();
        
        do{
        	MyMutableTreeNode current = statementsList.getChildren(0).getChildren(0);
        	if (current.getUserObject().toString().equals("<unsigned-integer>")){
        		allowedLabels.add(current.getChildren(0).getLexem());
        	}
        	if (statementsList.getChildren(1).getChildren(0).getUserObject().toString().equals("<empty>"))
        		statementsList = null;
        	else
        		statementsList = statementsList.getChildren(1);
        	
        } while(statementsList !=null);
    
        statementsList = treeNode.getChildren(0).getChildren(4).getChildren(2);
        statementListFunc(statementsList);
        
        asmCode.add("LABEL_END:");
        asmCode.add(String.format("\t%-8s%s", "POP", "ESI"));
        asmCode.add(String.format("\t%-8s%s", "POP", "EBP"));
        asmCode.add(String.format("\t%-8s", "RET"));
        asmCode.add(String.format("%-8s%s", identifierTableFull.procedureName.name, "ENDP"));
        
        return new ResultGenerator(errors,asmCode);
    }

    private void statementListFunc(MyMutableTreeNode statement){
    	if (statement.getChildren(0).getUserObject().toString().equals("<empty>"))
    		return;
    	statementFunc(statement.getChildren(0));
    	statementListFunc(statement.getChildren(1));
    }
    
    private void statementFunc(MyMutableTreeNode statement){
    	
    	String type = statement.getChildren(0).getUserObject().toString();
    	boolean error = false;
    	if (type.equals("<unsigned-integer>")){
    		
    		int value = statement.getChildren(0).getChildren(0).getLexem();
    		for (int i:declaratedLabels){
    			if (value == i){
    				error = true;
    				break;
    			}
    		}
    		if (!error){
    			asmCode.add(String.format("LABEL_%s:", labels.get(value).name));
    			declaratedLabels.add(value);
    			statementFunc(statement.getChildren(2));
    		}
    		return;
    	}
    	if (type.equals("GOTO")){
    		int label = statement.getChildren(1).getChildren(0).getLexem();
    		if (allowedLabels.indexOf(label)!=-1)
    			asmCode.add(String.format("\tJMP LABEL_%s", labels.get(label).name));
    		else
    			addError(-41);
    		return;
    	}
    	if (type.equals("RETURN")){
    		asmCode.add("\tJMP LABEL_END");
    		return;
    	}
    	if (type.equals(";")){
    		asmCode.add("\tNOP");
    		return;
    	}
    	if (type.equals("($")){
    		String filename = statement.getChildren(1).getChildren(0).getChildren(0).getUserObject().toString();

    		File file = new File(filename);
            if (file.exists()) {
                try {
                    BufferedReader bf = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                    String s;
                    while ((s = bf.readLine()) != null)
                    	asmCode.add(s);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }else
            	addError(-43);
    		return;
    	}
    }        
}
        

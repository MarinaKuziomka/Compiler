import java.util.ArrayList;

class IdentifiersTable {
    public final int IDENTIFIER_MIN_CODE = 1001;
    private int count = 0;
    private ArrayList<String> identifiers = new ArrayList<String>();

    public int getCount(){
        return count;
    }

    public String getByIndex(int id){
        if (id>=0 && id<count)
            return identifiers.get(id);
        return null;
    }

    public int append(String identifier) {
        identifiers.add(identifier);
        return IDENTIFIER_MIN_CODE + count++;
    }

    public int getCode(String idn) {
        int id = identifiers.indexOf(idn);
        if (id != -1)
            return IDENTIFIER_MIN_CODE + id;
        return -1;
    }

    public String get(int code) {
    	try{
    		return identifiers.get(code - IDENTIFIER_MIN_CODE);
    	}
    	catch (IndexOutOfBoundsException e){
    		return null;
        }
    }

    public ArrayList<String> getIdentifiers(){
        return (ArrayList<String>)identifiers.clone();
    }
}

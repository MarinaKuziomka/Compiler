import java.util.ArrayList;

class ConstantsTable {
    public final int CONSTANT_MIN_CODE = 501;
    private int count = 0;
    private ArrayList<Long> constants = new ArrayList<Long>();

    public void delete (int code){
    	try{
    		constants.remove(code - CONSTANT_MIN_CODE);
    	}
    	catch (IndexOutOfBoundsException e){
    	}
    }
    
    public int append(Long constant) {
        constants.add(constant);
        return CONSTANT_MIN_CODE + count++;
    }
    public int getCount(){
        return count;
    }
    public Long getByIndex(int id){
        return constants.get(id);
    }

    public int getCode(Long elem) {

        int id = constants.indexOf(elem);
        if (id != -1)
            return CONSTANT_MIN_CODE + id;
        return -1;
    }
    
    public Long get(int code) {
    	try{
    		return constants.get(code - CONSTANT_MIN_CODE);
    	}
    	catch (IndexOutOfBoundsException e){
    		return null;
        }
    }
    public ArrayList<Long> getConstants(){
        return (ArrayList<Long>)constants.clone();
    }

}


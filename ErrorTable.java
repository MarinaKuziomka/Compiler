import java.util.ArrayList;
import java.util.HashMap;

public class ErrorTable {
    private static HashMap<Integer,String> errors = new HashMap<Integer, String>();

    static{
        errors.put(-1,"Неочікуваний кінець файлу");
        errors.put(-2,"Невідомий символ");
        errors.put(-3,"Незакритий коментар");
        errors.put(-4, "Незакритий подвійний розділювач");
        		
        errors.put(-10,"Очікувався 'PROCEDURE'");
        errors.put(-11,"Очікувався ';'");
        errors.put(-12, "Очікувався 'BEGIN'");
        errors.put(-13, "Очікувався 'END'");
        errors.put(-14, "Очікувався 'LABEL'");
        errors.put(-15,"Очікувався ':'");
        errors.put(-16,"Очікувався ')'");
        errors.put(-17,"Очікувався '(' або ';'");
        errors.put(-18,"Очікувався ',' або ')'");
        errors.put(-19,"Очікувався ',' або ';'");        
        errors.put(-20,"Очікувався statement або 'END'");
        errors.put(-21,"Очікувався '$)'");     
    
        errors.put(-30,"Очікувався ідентифікатор");
        errors.put(-31,"Очікувалась константа");
        
        errors.put(-40,"Повторення параметра");
        errors.put(-41,"Мітка невизначена");
        errors.put(-42,"Ім'я параметру збігається з назвою процедури");
        errors.put(-43,"Файл не існує");

        errors.put(-99,"");
    }

    public static String getError(int id){
        return errors.get(id);
    }

    public static String getSyntaxError(int id){
            return errors.get(id);
    }
}

import java.util.ArrayList;

public class ResultGenerator {
    public ArrayList<Lexem> errors;
    public ArrayList<String> asmListing;

    ResultGenerator(ArrayList<Lexem> errors, ArrayList<String> asmListing){
        this.errors = errors;
        this.asmListing = asmListing;
    }

}

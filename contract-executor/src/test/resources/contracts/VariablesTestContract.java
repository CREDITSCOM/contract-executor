import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VariablesTestContract extends com.credits.scapi.v0.SmartContract {
    public String nullField;
    public int intField;
    public Integer integerField;
    public Double doubleField;
    public String stringField;
    public List<Integer> listIntegerField;
    public Set<Integer> setIntegerField;
    public Map<String, Integer> mapStringIntegerField;

    public VariablesTestContract() {
        this.nullField = null;
        this.intField = 5;
        this.integerField = 55;
        this.doubleField = 5.55;
        this.stringField = "some string value" ;
        this.listIntegerField = new ArrayList<>();
        this.listIntegerField.add(5);
        this.setIntegerField = new HashSet<>();
        this.setIntegerField.add(5);
        this.mapStringIntegerField = new HashMap<>();
        this.mapStringIntegerField.put("string key" , 5);
    }
}
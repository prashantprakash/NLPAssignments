import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ColumnData {
	
	public Map<String,Double> columnValues;
    public ColumnData(Set<String> words)
    {
       columnValues=new HashMap<String,Double>();
       for(String w: words)
       {
           columnValues.put(w,Constant.ZERO);
       }
    }

}

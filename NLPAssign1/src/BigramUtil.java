import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BigramUtil {
	public void printTable(Map<String, ColumnData> input) {
		System.out.format("%-30s", "");
		List<String> keys = new LinkedList<>();
		keys.addAll(input.keySet());
		for (String key : input.keySet()) {
			System.out.format("%-30s", key);
		}
		System.out.println("");
		for (String key : input.keySet()) {
			System.out.format("%-30s", key);
			for (int i = 0; i < input.keySet().size(); i++) {
				String w = keys.get(i);
				String val = Double.toString(input.get(w).columnValues.get(key));
				System.out.format("%-30s", val);
			}
			System.out.println("");
		}

	}
}

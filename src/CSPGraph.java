import java.util.ArrayList;
import java.util.Collection;

public class CSPGraph {
    private ArrayList<DSLSet> sets;

    public CSPGraph(Collection<DSLSet> sets) {
        this.sets = new ArrayList<>(sets);
    }
}

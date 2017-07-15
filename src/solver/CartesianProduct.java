package solver;

import java.util.HashSet;

public class CartesianProduct extends Relation {
    public CartesianProduct(DSLSet domain, DSLSet range) { super(domain, range); }
    public HashSet<Value> getAfterSet(Value value) { return range.getValues(); }

    @Override
    public HashSet<Value> getAdjacencySet(Value value) {
        return range.getValues();
    }

    @Override
    public String explain(Value lValue, Value rValue) { return ""; }
}

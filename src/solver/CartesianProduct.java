package solver;

import java.util.HashSet;

public class CartesianProduct extends Relation {
    @Override
    public HashSet<Value> getRelatedValues(Value value) { return range.getValues(); }
    public HashSet<Value> getAfterSet(Value value) { return range.getValues(); }
    public CartesianProduct(DSLSet domain, DSLSet range) { super(domain, range); }
}

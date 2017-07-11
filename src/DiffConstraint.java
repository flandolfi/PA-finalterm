import java.util.HashSet;

public class DiffConstraint extends Relation {
    public DiffConstraint(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public HashSet<Value> getRelatedValues(Value value) {
        HashSet<Value> values = range.getValues();
        values.removeAll(getAfterSet(value));

        return values;
    }
}

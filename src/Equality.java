import java.util.HashSet;

public class Equality extends Relation {
    public Equality(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public HashSet<Value> getUnconstrainedValues(Value value) {
        return getAfterSet(value);
    }
}

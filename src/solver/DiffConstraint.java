package solver;

import java.util.HashSet;

public class DiffConstraint extends Relation {
    public DiffConstraint(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public HashSet<Value> getRelatedValues(Value value) {
        HashSet<Value> values = range.getValues();
        values.removeAll(getAfterSet(value));

        return values;
    }

    @Override
    public String explain(Value lValue, Value rValue) {
        StringBuilder builder = new StringBuilder();
        getAfterSet(lValue).forEach(v -> builder.append(" \u2227 ")
                .append(v.getDomainName()).append(" \u2260 ").append(v));
        builder.delete(0, 3);

        return builder.toString();
    }
}

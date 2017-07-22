package dsl;

import java.util.HashSet;

public class DiffConstraint extends Relation {
    public DiffConstraint(Domain domain, Domain range) { super(domain, range); }

    @Override
    public HashSet<Value> getAdjacencySet(Value value) {
        HashSet<Value> values = range.getValues();
        values.removeAll(getAfterSet(value));

        return values;
    }

    @Override
    protected String explain(Value lValue, Value rValue) {
        StringBuilder builder = new StringBuilder();
        getAfterSet(lValue).forEach(v -> builder.append(" \u2227 ") // AND
                .append(v.getDomainName()).append(" \u2260 ").append(v)); // NEQ
        builder.delete(0, 3);

        return builder.toString();
    }
}

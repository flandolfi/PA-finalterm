package dsl;

import java.util.HashSet;

public class EqConstraint extends Relation {
    public EqConstraint(Domain domain, Domain range) { super(domain, range); }

    @Override
    public HashSet<Value> getAdjacencySet(Value value) {
        HashSet<Value> afterSet = getAfterSet(value);

        return afterSet.size() > 1? new HashSet<>() : afterSet;
    }

    @Override
    protected String explain(Value lValue, Value rValue) {
        return rValue.getDomainName() + " = " + rValue;
    }
}

package solver;

import java.util.HashSet;

public class EqConstraint extends Relation {
    public EqConstraint(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public HashSet<Value> getRelatedValues(Value value) {
        HashSet<Value> afterSet = getAfterSet(value);

        return afterSet.size() > 1? new HashSet<>() : afterSet;
    }

    @Override
    public String explain(Value lValue, Value rValue) {
        return rValue.getDomainName() + " = " + rValue;
    }
}

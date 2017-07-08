import java.util.HashSet;

public class Equality extends Relation {
    public Equality(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public boolean addPair(Value lValue, Value rValue) {
        if (!domain.contains(lValue) || !range.contains(rValue))
            return false;

        relatesTo.computeIfAbsent(lValue, k -> new HashSet<>()).remove(rValue);
        return true;
    }

    @Override
    public HashSet<Value> getSuccessorNeighborhoodOf(Value value) {
        if (!domain.contains(value))
            return null;

        return relatesTo.getOrDefault(value, new HashSet<>());
    }
}

import java.util.HashSet;

public class Inequality extends Relation {
    public Inequality(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public boolean addPair(Value lValue, Value rValue) {
        if (!domain.contains(lValue) || !range.contains(rValue))
            return false;

        relatesTo.computeIfAbsent(lValue, k -> range.getValues()).add(rValue);
        return true;
    }

    @Override
    public HashSet<Value> getSuccessorNeighborhoodOf(Value value) {
        if (!domain.contains(value))
            return null;

        return relatesTo.getOrDefault(value, range.getValues());
    }
}

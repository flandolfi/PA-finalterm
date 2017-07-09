import java.util.HashSet;

public class Equality extends Relation {
    public Equality(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public boolean addPair(Value lValue, Value rValue) {
        if (!domain.contains(lValue) || !range.contains(rValue))
            return false;

        afterSets.computeIfAbsent(lValue, k -> new HashSet<>()).add(rValue);
        return true;
    }

    @Override
    public HashSet<Value> getAfterSet(Value value) {
        if (!domain.contains(value))
            return null;

        return afterSets.getOrDefault(value, new HashSet<>());
    }
}

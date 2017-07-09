import java.util.HashSet;

public class Inequality extends Relation {
    public Inequality(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public boolean addPair(Value lValue, Value rValue) {
        if (!domain.contains(lValue) || !range.contains(rValue))
            return false;

        afterSets.computeIfAbsent(lValue, k -> range.getValues()).remove(rValue);
        return true;
    }

    @Override
    public HashSet<Value> getAfterSet(Value value) {
        if (!domain.contains(value))
            return null;

        return afterSets.getOrDefault(value, range.getValues());
    }
}

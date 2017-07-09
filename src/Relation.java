import java.util.HashMap;
import java.util.HashSet;

public abstract class Relation {
    protected final HashMap<Value, HashSet<Value>> afterSets = new HashMap<>();
    protected final DSLSet domain;
    protected final DSLSet range;

    public Relation(DSLSet domain, DSLSet range) {
        this.domain = domain;
        this.range = range;
    }

    public boolean addPair(Value lValue, Value rValue) {
        if (!domain.contains(lValue) || !range.contains(rValue))
            return false;

        afterSets.computeIfAbsent(lValue, k -> new HashSet<>()).add(rValue);
        return true;
    }

    public HashSet<Value> getAfterSet(Value value) {
        return domain.contains(value) ?
                afterSets.getOrDefault(value, new HashSet<>()) : null;
    }

    public DSLSet getDomain() { return domain; }
    public DSLSet getRange() { return range; }
    public abstract HashSet<Value> getUnconstrainedValues(Value value);
}

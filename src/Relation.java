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
        return afterSets.computeIfAbsent(lValue, k -> new HashSet<>()).add(rValue);
    }

    public HashSet<Value> getAfterSet(Value value) {
        return afterSets.getOrDefault(value, new HashSet<>());
    }

    public DSLSet getDomain() { return domain; }
    public DSLSet getRange() { return range; }
    public abstract HashSet<Value> getRelatedValues(Value value);
}

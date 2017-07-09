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

    public DSLSet getDomain() { return domain; }
    public DSLSet getRange() { return range; }
    public abstract boolean addPair(Value lValue, Value rValue);
    public abstract HashSet<Value> getAfterSet(Value value);
}

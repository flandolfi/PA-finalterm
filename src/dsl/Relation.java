package dsl;

import java.util.*;

public abstract class Relation {
    protected final HashMap<Value, HashSet<Value>> afterSets = new HashMap<>();
    protected final Domain domain;
    protected final Domain range;

    public Relation(Domain domain, Domain range) {
        this.domain = domain;
        this.range = range;
    }

    public boolean addPair(Value lValue, Value rValue) {
        return afterSets.computeIfAbsent(lValue, k -> new HashSet<>()).add(rValue);
    }

    public HashSet<Value> getAfterSet(Value value) {
        return afterSets.getOrDefault(value, new HashSet<>());
    }

    public Domain getDomain() { return domain; }
    public Domain getRange() { return range; }
    public abstract HashSet<Value> getAdjacencySet(Value value);
    public abstract String explain(Value lValue, Value rValue);
}

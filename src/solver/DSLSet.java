package solver;

import java.util.HashMap;
import java.util.HashSet;

public class DSLSet {
    private final HashSet<Value> values = new HashSet<>();
    private final HashMap<DSLSet, Relation> relations = new HashMap<>();
    private final String label;

    public DSLSet(String label) { this.label = label; }
    public boolean addValue(Value value) { return values.add(value); }
    public boolean contains(Value value) { return values.contains(value); }
    public HashSet<Value> getValues() { return new HashSet<>(values); }
    public String toString() { return label; }

    public boolean addRelation(DSLSet range, Relation relation) {
        return relations.putIfAbsent(range, relation) != null;
    }

    public Relation getRelationWith(DSLSet range) {
        return relations.getOrDefault(range, new CartesianProduct(this, range));
    }
}

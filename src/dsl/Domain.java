package dsl;

import java.util.*;

public class Domain {
    private final HashSet<Value> values = new HashSet<>();
    private final HashMap<Domain, Relation> relations = new HashMap<>();
    private final String label;

    public Domain(String label) { this.label = label; }
    public boolean addValue(Value value) { return values.add(value); }
    public boolean contains(Value value) { return values.contains(value); }
    public HashSet<Value> getValues() { return new HashSet<>(values); }
    public String toString() { return label; }

    public boolean addRelation(Domain range, Relation relation) {
        return relations.putIfAbsent(range, relation) == null;
    }

    public Relation getRelationWith(Domain range) {
        return relations.getOrDefault(range, new CartesianProduct(this, range));
    }
}

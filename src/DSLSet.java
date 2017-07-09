import java.util.HashMap;
import java.util.HashSet;

public class DSLSet {
    private final HashSet<Value> values = new HashSet<>();
    private final HashMap<DSLSet, Relation> relations = new HashMap<>();

    public void addValue(Value value) { values.add(value); }
    public boolean contains(Value value) { return values.contains(value); }
    public HashSet<Value> getValues() { return new HashSet<>(values); }

    public boolean addRelation(DSLSet range, Relation relation) {
        return relations.putIfAbsent(range, relation) != null;
    }

    public Relation getRelationWith(DSLSet range) {
        return relations.getOrDefault(range, new Relation(this, range) {
            @Override
            public boolean addPair(Value lValue, Value rValue) { return true; }

            @Override
            public HashSet<Value> getAfterSet(Value value) {
                return range.getValues(); // Cartesian product
            }
        });
    }
}

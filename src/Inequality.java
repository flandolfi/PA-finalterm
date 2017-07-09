import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Inequality extends Relation {
    public Inequality(DSLSet domain, DSLSet range) { super(domain, range); }

    @Override
    public HashSet<Value> getRelatableValues(Value value) {
        return domain.contains(value) ? range.getValues().stream()
                .filter(v -> !getAfterSet(value).contains(v))
                .collect(Collectors.toCollection(HashSet::new)) : null;
    }
}

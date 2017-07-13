package solver;

public class Value {
    private final String label;
    private final String domainName;

    public Value(String domainName, String label) {
        this.label = label;
        this.domainName = domainName;
    }

    public String toString() { return domainName + label; }
    public String getDomainName() { return domainName; }
    public String getLabel() { return label; }
    public int hashCode() { return toString().hashCode(); }
    public boolean equals(Object value) {
        return toString().equals(value.toString());
    }
}

public class Value {
    private final String label;
    private final String domainName;

    public Value(String domainName, String label) {
        this.label = label;
        this.domainName = domainName;
    }

    public String toString() { return label; }
    public String getDomainName() { return domainName; }
    public String getLabel() { return label; }
}

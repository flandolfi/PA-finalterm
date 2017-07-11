import javafx.util.Pair;
import java.io.Reader;
import java.util.HashMap;

public class Parser {
    private Scanner scanner;
    private HashMap<String, DSLSet> sets;
    private Type token;

    public CSPGraph parse(Reader reader) throws LexicalException, SyntaxException {
        scanner = new Scanner(reader);
        sets = new HashMap<>();

        return parseCSP();
    }

    private void expect(Type type) throws SyntaxException, LexicalException {
        token = scanner.getToken();

        if (token != type)
            throw new SyntaxException("Line " + scanner.getLineNo() + ": Expected: "
                    + type + "; Found: " + token + ".");
    }

    private CSPGraph parseCSP() throws SyntaxException, LexicalException {
        parseDomain();
        parseDList();
        parseRList();
        expect(Type.EOF);

        return new CSPGraph(sets.values());
    }

    private void parseDomain() throws SyntaxException, LexicalException {
        expect(Type.WORD);
        DSLSet set = new DSLSet(scanner.getTokenValue());
        expect(Type.EQ);
        expect(Type.O_BR);
        parseVList(set);
        expect(Type.C_BR);

        if (sets.put(set.toString(), set) != null)
            throw new SyntaxException("Line " + scanner.getLineNo()
                    + ": Set names must be unique.");
    }

    private Value parseValue() throws SyntaxException, LexicalException {
        expect(Type.WORD);
        String dName = scanner.getTokenValue();
        expect(Type.NUM);

        return new Value(dName, scanner.getTokenValue());
    }

    private void parseVList(DSLSet set) throws LexicalException, SyntaxException {
        do {
            Value value = parseValue();

            if (!value.getDomainName().equals(set.toString()))
                throw new SyntaxException("Line " + scanner.getLineNo() + ": All value " +
                        "labels must start with the name of the set they belong to.");

            if (!set.addValue(value))
                throw new SyntaxException("Line " + scanner.getLineNo()
                        + ": All values must be unique.");
        } while (scanner.peek() == Type.COMMA);
    }

    private void parseDList() throws LexicalException, SyntaxException {
        while (scanner.peek() == Type.WORD)
            parseDomain();
    }

    private void parseRelation() throws LexicalException, SyntaxException {
        Relation relation;

        switch (token = scanner.getToken()) {
            case O_BR:
                Pair<Value, Value> pair = parsePair();
                DSLSet domain = sets.get(pair.getKey().getDomainName());
                DSLSet range = sets.get(pair.getValue().getDomainName());
                relation = new Equality(domain, range);
                parsePList(relation);
                break;

            case BANG:
                break;

            default:
                throw new SyntaxException("Line " + scanner.getLineNo() + ": Expected: "
                        + Type.BANG + ", " + Type.O_BR + "; Found: " + token + ".");
        }
    }

    private Pair<Value, Value> parsePair() throws SyntaxException, LexicalException {
        expect(Type.O_PAR);
        Value lValue = parseValue();
        expect(Type.COMMA);
        Value rValue = parseValue();
        expect(Type.C_PAR);

        return new Pair<>(lValue, rValue);
    }

    private void parsePList(Relation relation) throws SyntaxException, LexicalException {
        do {
            expect(Type.O_PAR);
            Value lValue = parseValue();
            expect(Type.COMMA);
            Value rValue = parseValue();
            expect(Type.C_PAR);
            relation.addPair(lValue, rValue);
        } while (scanner.peek() == Type.COMMA);
    }

    private void parseRList() {
        // TODO
    }
}

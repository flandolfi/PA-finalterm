package compiler;

import javafx.util.Pair;
import java.io.Reader;
import java.util.HashMap;
import solver.*;

public class Parser {
    private Scanner scanner;
    private HashMap<String, DSLSet> sets;
    private Type token;

    public CSPGraph parse(Reader reader) throws LexicalException,
            SyntaxException, WarningException {
        scanner = new Scanner(reader);
        sets = new HashMap<>();

        return parseCSPGraph();
    }

    private void expect(Type type) throws SyntaxException, LexicalException {
        token = scanner.getToken();

        if (token != type)
            throw new SyntaxException(scanner.printLineNo() + "Expected: "
                    + type + "; Found: " + token + ".");
    }

    private CSPGraph parseCSPGraph() throws SyntaxException,
            LexicalException, WarningException {
        parseDList();
        parseRList();
        expect(Type.EOF);

        return new CSPGraph(sets.values());
    }

    private Value parseValue() throws SyntaxException, LexicalException {
        expect(Type.WORD);
        String dName = scanner.getTokenValue();
        expect(Type.NUM);

        return new Value(dName, scanner.getTokenValue());
    }

    private void parseVList(DSLSet set) throws LexicalException,
            SyntaxException, WarningException {
        do {
            Value value = parseValue();

            if (!value.getDomainName().equals(set.toString()))
                throw new SyntaxException(scanner.printLineNo() + "All value labels " +
                        "must start with the name of the set they belong to.");

            if (!set.addValue(value))
                throw new WarningException(scanner.printLineNo()
                        + "Set values should be unique.");
        } while (scanner.peek() == Type.COMMA);
    }

    private void parseDList() throws LexicalException, SyntaxException, WarningException {
        do {
            expect(Type.WORD);
            DSLSet set = new DSLSet(scanner.getTokenValue());
            expect(Type.EQ);
            expect(Type.O_BR);
            parseVList(set);
            expect(Type.C_BR);

            if (sets.put(set.toString(), set) != null)
                throw new SyntaxException(scanner.printLineNo()
                        + "Set names must be unique.");
        } while (scanner.peek() == Type.WORD);
    }

    private void parseRList() throws LexicalException, SyntaxException, WarningException {
        token = scanner.peek();

        while (token == Type.O_BR || token == Type.BANG) {
            if (token == Type.BANG)
                scanner.getToken();

            expect(Type.O_BR);
            Pair<Value, Value> pair = parsePair();
            DSLSet domain = sets.get(pair.getKey().getDomainName());
            DSLSet range = sets.get(pair.getValue().getDomainName());
            Relation relation = token == Type.O_BR? new EqConstraint(domain, range)
                    : new DiffConstraint(domain, range);
            parsePList(relation);
            expect(Type.C_BR);

            if (!domain.addRelation(range, relation))
                throw new SyntaxException(scanner.printLineNo()
                        + "Can not define multiple relations between two sets.");
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

    private void parsePList(Relation relation) throws SyntaxException,
            LexicalException, WarningException {
        do {
            Pair<Value, Value> pair = parsePair();

            if (!relation.getDomain().contains(pair.getKey()) ||
                    !relation.getRange().contains(pair.getValue()))
                throw new SyntaxException(scanner.printLineNo()
                        + "Mixed domains/ranges are not allowed.");

            if (!relation.addPair(pair.getKey(), pair.getValue()))
                throw new WarningException(scanner.printLineNo()
                        + "Relation pairs should be unique.");
        } while (scanner.peek() == Type.COMMA);
    }
}

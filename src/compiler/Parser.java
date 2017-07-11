package compiler;

import javafx.util.Pair;
import java.io.Reader;
import java.util.HashMap;
import solver.*;

public class Parser {
    private Scanner scanner;
    private HashMap<String, DSLSet> sets;
    private Type token;

    public CSPGraph parse(Reader reader) throws CompilerException {
        scanner = new Scanner(reader);
        sets = new HashMap<>();

        return parseCSPGraph();
    }

    private void expect(Type type) throws CompilerException {
        token = scanner.getToken();

        if (token != type)
            throw new CompilerException(scanner.printLineNo() + "Expected: "
                    + type + "; Found: " + token + ".");
    }

    private CSPGraph parseCSPGraph() throws CompilerException {
        parseDList();
        parseRList();
        expect(Type.EOF);

        return new CSPGraph(sets.values());
    }

    private Value parseValue() throws CompilerException {
        expect(Type.WORD);
        String dName = scanner.getTokenValue();
        expect(Type.NUM);

        return new Value(dName, scanner.getTokenValue());
    }

    private void parseVList(DSLSet set) throws CompilerException {
        do {
            Value value = parseValue();

            if (!value.getDomainName().equals(set.toString()))
                throw new CompilerException(scanner.printLineNo() + "All value " +
                        "labels must start with the name of the set they belong to.");

            if (!set.addValue(value))
                throw new CompilerException(scanner.printLineNo()
                        + "Set values must be unique.");
        } while (scanner.peek() == Type.COMMA);
    }

    private void parseDList() throws CompilerException {
        do {
            expect(Type.WORD);
            DSLSet set = new DSLSet(scanner.getTokenValue());
            expect(Type.EQ);
            expect(Type.O_BR);
            parseVList(set);
            expect(Type.C_BR);

            if (sets.put(set.toString(), set) != null)
                throw new CompilerException(scanner.printLineNo()
                        + "Set names must be unique.");
        } while (scanner.peek() == Type.WORD);
    }

    private void parseRList() throws CompilerException {
        Type token = scanner.peek();

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
                throw new CompilerException(scanner.printLineNo()
                        + "Can not define multiple relations between two sets.");
        }
    }

    private Pair<Value, Value> parsePair() throws CompilerException {
        expect(Type.O_PAR);
        Value lValue = parseValue();
        expect(Type.COMMA);
        Value rValue = parseValue();
        expect(Type.C_PAR);

        return new Pair<>(lValue, rValue);
    }

    private void parsePList(Relation relation) throws CompilerException {
        do {
            Pair<Value, Value> pair = parsePair();

            if (!relation.getDomain().contains(pair.getKey()) ||
                    !relation.getRange().contains(pair.getValue()))
                throw new CompilerException(scanner.printLineNo()
                        + "Mixed domains/ranges are not allowed.");

            if (!relation.addPair(pair.getKey(), pair.getValue()))
                throw new CompilerException(scanner.printLineNo()
                        + "Relation pairs must be unique.");
        } while (scanner.peek() == Type.COMMA);
    }
}

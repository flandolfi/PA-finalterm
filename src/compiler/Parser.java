package compiler;

import dsl.*;
import javafx.util.Pair;
import java.io.Reader;
import java.util.*;

public class Parser {
    private Scanner scanner;
    private HashMap<String, Domain> sets;
    private Type token;

    public Collection<Domain> parse(Reader reader) throws CompilerException {
        scanner = new Scanner(reader);
        sets = new HashMap<>();

        return parseSolver();
    }

    private void expect(Type type) throws CompilerException {
        token = scanner.getToken();

        if (token != type)
            throw new CompilerException(scanner.printLineNo() + "Expected: "
                    + type + "; Found: " + token + ".");
    }

    private Collection<Domain> parseSolver() throws CompilerException {
        parseDList();
        parseRList();
        expect(Type.EOF);

        return sets.values();
    }

    private Value parseValue() throws CompilerException {
        expect(Type.WORD);
        String value = scanner.getTokenValue();

        if (!value.matches("[A-Za-z]+[0-9]+"))
            throw new CompilerException(scanner.printLineNo() + "All value " +
                    "labels must start with the name of the set they belong " +
                    "to, followed by a number.");

        String[] tokens = value.split("(?<=[A-Za-z])(?=[0-9])");

        return new Value(tokens[0], tokens[1]);
    }

    private void parseVList(Domain set) throws CompilerException {
        Value value = parseValue();

        if (!value.getDomainName().equals(set.toString()))
            throw new CompilerException(scanner.printLineNo() + "All value " +
                    "labels must start with the name of the set they belong to.");

        if (!set.addValue(value))
            throw new CompilerException(scanner.printLineNo()
                    + "Set values must be unique.");

        if (scanner.peek() == Type.COMMA) {
            scanner.getToken();
            parseVList(set);
        }
    }

    private void parseDList() throws CompilerException {
        expect(Type.WORD);
        Domain set = new Domain(scanner.getTokenValue());

        if (!set.toString().matches("[A-Za-z]+"))
            throw new CompilerException(scanner.printLineNo()
                    + "Set names must be literal.");

        expect(Type.EQ);
        expect(Type.O_BR);
        parseVList(set);
        expect(Type.C_BR);

        if (sets.put(set.toString(), set) != null)
            throw new CompilerException(scanner.printLineNo()
                    + "Set names must be unique.");

        if (scanner.peek() == Type.WORD)
            parseDList();
    }

    private void parseRList() throws CompilerException {
        Type token = scanner.peek();

        switch (token) {
            case BANG: scanner.getToken();
            case O_BR:
                expect(Type.O_BR);
                Pair<Value, Value> pair = parsePair();
                Domain domain = sets.get(pair.getKey().getDomainName());
                Domain range = sets.get(pair.getValue().getDomainName());
                Relation relation = token == Type.O_BR? new EqConstraint(domain, range)
                        : new DiffConstraint(domain, range);
                relation.addPair(pair.getKey(), pair.getValue());
                parsePList(relation);
                expect(Type.C_BR);

                if (!domain.addRelation(range, relation))
                    throw new CompilerException(scanner.printLineNo()
                            + "Can not define multiple relations between two sets.");

                parseRList();
                break;
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
        while (scanner.peek() == Type.COMMA) {
            scanner.getToken();
            Pair<Value, Value> pair = parsePair();

            if (!relation.getDomain().contains(pair.getKey()) ||
                    !relation.getRange().contains(pair.getValue()))
                throw new CompilerException(scanner.printLineNo()
                        + "Mixed domains/ranges are not allowed.");

            if (!relation.addPair(pair.getKey(), pair.getValue()))
                throw new CompilerException(scanner.printLineNo()
                        + "Relation pairs must be unique.");
        }
    }
}

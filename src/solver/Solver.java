package solver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import compiler.*;

public class Solver {
    private final ArrayList<DSLSet> sets;

    public Solver(Collection<DSLSet> sets) { this.sets = new ArrayList<>(sets); }

    public List<Value> backtrackingSearch() {
        Enumeration<List<Value>> enumeration = enumeration();

        return enumeration.hasMoreElements()? enumeration.nextElement() : null;
    }

    private Stream<List<Value>> backtrack(
            List<Set<Value>> solutionSets, int step) {
        if (step == sets.size()) {
            System.out.println("RETRIEVED NEW SOLUTION");
            return Stream.of((List<Value>) solutionSets.stream()
                    .map(s -> s.iterator().next())
                    .collect(Collectors.toCollection(ArrayList::new)));
        }

        Stream<List<Value>> solutions = Stream.empty();

        loop: for (Value value: solutionSets.get(step)) {
            List<Set<Value>> newSolSets = new ArrayList<>(solutionSets.subList(0, step));
            newSolSets.add(new HashSet<>(Collections.singleton(value)));

            for (int i = 0; i < step; i++)
                if (!sets.get(step).getRelationWith(sets.get(i))
                        .getRelatedValues(value).containsAll(newSolSets.get(i)))
                    continue loop;

            for (int i = step + 1; i < sets.size() ; i++) {
                newSolSets.add(new HashSet<>(solutionSets.get(i)));
                newSolSets.get(i).retainAll(sets.get(step)
                        .getRelationWith(sets.get(i)).getRelatedValues(value));

                if (newSolSets.get(i).isEmpty())
                    continue loop;
            }

            final Stream<List<Value>> oldSolutions = solutions;
            solutions = Stream.<Supplier<Stream<List<Value>>>>of(
                    () -> oldSolutions,
                    () -> backtrack(newSolSets, step + 1)).flatMap(Supplier::get);
        }

        return solutions;
    }

    public String explain(List<Value> solution) {
        if (solution == null || solution.size() != sets.size())
            return "Not a solution.";

        StringBuilder message = new StringBuilder();

        for (int i = 0; i < sets.size(); i++) {
            Value lValue = solution.get(i);

            if (!sets.get(i).contains(lValue))
                return "Not a solution.";

            message.append(lValue.getDomainName()).append(" = ")
                    .append(lValue).append(" explanation:\n");

            for (int j = 0; j < sets.size(); j++) {
                if (i == j)
                    continue;

                Relation relation = sets.get(i).getRelationWith(sets.get(j));
                Value rValue = solution.get(j);

                if (!relation.getRelatedValues(lValue).contains(rValue))
                    return "Not a solution.";

                if (relation instanceof EqConstraint)
                    message.append(" - { (").append(lValue).append(", ")
                            .append(rValue).append(") }\n");
                else if (relation instanceof DiffConstraint) {
                    Iterator<Value> iterator = relation.getAfterSet(lValue).iterator();
                    message.append(" - !{ ");

                    while (iterator.hasNext()) {
                        Value v = iterator.next();
                        message.append("(").append(lValue).append(", ")
                                .append(v).append(")");

                        if (iterator.hasNext())
                            message.append(", ");
                    }

                    message.append(" }\n");
                } else
                    message.append(" - No constraints with ")
                            .append(sets.get(j)).append("\n");
            }
        }

        return message.toString();
    }

    public Enumeration<List<Value>> enumeration() {
        return new Enumeration<List<Value>>() {
            private Iterator<List<Value>> iterator = backtrack(sets.stream()
                    .map(DSLSet::getValues)
                    .collect(Collectors.toCollection(ArrayList::new)), 0).iterator();

            @Override
            public boolean hasMoreElements() { return iterator.hasNext(); }

            @Override
            public List<Value> nextElement() { return iterator.next(); }
        };
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Path not found");
            System.exit(1);
        }

        try {
            Parser parser = new Parser();
            Solver solver = parser.parse(new BufferedReader(new FileReader(args[0])));
            List<Value> solution = solver.backtrackingSearch();

            if (solution == null)
                System.out.println("No solution found.");
            else
                System.out.println(solver.explain(solution));
        } catch (CompilerException e) {
            System.err.println("ERROR: File " + args[0] + " @ " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File " + args[0] + " not found.");
        }
    }
}

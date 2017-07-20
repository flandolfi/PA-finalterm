package solver;

import dsl.Domain;
import dsl.Value;
import compiler.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Solver {
    protected LinkedList<Value> solution = new LinkedList<>();
    protected LinkedList<Set<Value>> universe;
    protected ArrayList<Domain> sets;

    public Solver(Collection<Domain> sets) {
        this.sets = new ArrayList<>(sets);
        universe = sets.stream().map(Domain::getValues)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<Value> backtrackingSearch() {
        solution = backtrack(new LinkedList<>(), universe);

        return solution;
    }

    protected LinkedList<Value> backtrack(LinkedList<Value> solution,
                                        LinkedList<Set<Value>> prevInferences) {
        return solution.size() == sets.size()? solution : prevInferences.getFirst()
                .stream().map(v -> infer(v, solution, prevInferences))
                .filter(Objects::nonNull).map(i -> backtrack(solution, i))
                .filter(s -> s != null || solution.removeLast() == null)
                .findFirst().orElse(null);
    }

    protected LinkedList<Set<Value>> infer(Value value,
                LinkedList<Value> solution, LinkedList<Set<Value>> inferences) {
        int step = solution.size();

        for (int i = 0; i < step; i++)
            if (!sets.get(step).getRelationWith(sets.get(i))
                    .getAdjacencySet(value).contains(solution.get(i)))
                return null;

        LinkedList<Set<Value>> result = new LinkedList<>();

        for (int i = step + 1; i < sets.size(); i++) {
            result.add(new HashSet<>(inferences.get(i - step)));
            result.getLast().retainAll(sets.get(step)
                    .getRelationWith(sets.get(i)).getAdjacencySet(value));

            if (result.getLast().isEmpty())
                return null;
        }

        solution.add(value);

        return result;
    }

    public String explain() {
        if (solution.isEmpty())
            return "Solution not found.";

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < sets.size(); i++) {
            builder.append(sets.get(i)).append(" = ")
                    .append(solution.get(i)).append("  \u21D2  ");

            for (int j = 0; j < sets.size(); j++) {
                if (i == j)
                    continue;

                String pairs = sets.get(i).getRelationWith(sets.get(j))
                        .explain(solution.get(i), solution.get(j));

                if (!pairs.equals(""))
                    builder.append(pairs).append(" \u2227 ");
            }

            builder.delete(builder.length() - 3, builder.length()).append("\n");
        }

        return builder.toString();
    }

    public SolutionsEnumerator enumerator() {
        return new SolutionsEnumerator(sets);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("ERROR: Missing argument: path to (.csp) file.");
            System.exit(1);
        }

        try {
            Parser parser = new Parser();
            Solver solver = new Solver(parser.parse(
                    new BufferedReader(new FileReader(args[0]))));
            solver.backtrackingSearch();
            System.out.println(solver.explain());
        } catch (CompilerException e) {
            System.err.println("ERROR: File " + args[0] + " @ " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File " + args[0] + " not found.");
        }
    }
}

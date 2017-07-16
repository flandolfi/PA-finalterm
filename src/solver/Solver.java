package solver;

import compiler.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Solver implements Enumeration<List<Value>> {
    private LinkedList<LinkedList<Set<Value>>> stack = new LinkedList<>();
    private LinkedList<Iterator<Value>> iterators = new LinkedList<>();
    private LinkedList<Value> solution = new LinkedList<>(),
            next = new LinkedList<>();
    private ArrayList<DSLSet> sets;

    public Solver(Collection<DSLSet> sets) {
        this.sets = new ArrayList<>(sets);
        stack.add(sets.stream().map(DSLSet::getValues)
                .collect(Collectors.toCollection(LinkedList::new)));
        iterators.add(stack.getLast().getFirst().iterator());
        searchNext();
    }

    public List<Value> backtrackingSearch() {
        solution = backtrack(new LinkedList<>(),
                sets.stream().map(DSLSet::getValues)
                .collect(Collectors.toCollection(LinkedList::new)));

        return solution;
    }

    private LinkedList<Value> backtrack(LinkedList<Value> solution,
                                  LinkedList<Set<Value>> solutionSets) {
        for (Value value: solutionSets.getFirst()) {
            LinkedList<Set<Value>> nextSolutionSets =
                    infer(value, solution, solutionSets);

            if (nextSolutionSets == null)
                continue;

            if (nextSolutionSets.isEmpty() ||
                    backtrack(solution, nextSolutionSets) != null)
                return solution;

            solution.removeLast();
        }

        return null;
    }

    private LinkedList<Set<Value>> infer(Value value, LinkedList<Value> solution,
                                 LinkedList<Set<Value>> solutionSets) {
        int step = solution.size();

        for (int i = 0; i < step; i++)
            if (!sets.get(step).getRelationWith(sets.get(i))
                    .getAdjacencySet(value).contains(solution.get(i)))
                return null;

        LinkedList<Set<Value>> nextSolutionSets = new LinkedList<>();

        for (int i = step + 1; i < sets.size(); i++) {
            nextSolutionSets.add(new HashSet<>(solutionSets.get(i - step)));
            nextSolutionSets.getLast().retainAll(sets.get(step)
                    .getRelationWith(sets.get(i)).getAdjacencySet(value));

            if (nextSolutionSets.getLast().isEmpty())
                return null;
        }

        solution.add(value);

        return nextSolutionSets;
    }

    private void searchNext() {
        while (!stack.isEmpty()) {
            while (iterators.getLast().hasNext()) {
                Value value = iterators.getLast().next();
                LinkedList<Set<Value>> nextSolutionSets =
                        infer(value, next, stack.getLast());

                if (nextSolutionSets == null)
                    continue;

                if (nextSolutionSets.isEmpty())
                    return;

                stack.add(nextSolutionSets);
                iterators.add(nextSolutionSets.getFirst().iterator());
            }

            while (!stack.isEmpty() && !iterators.getLast().hasNext()) {
                iterators.removeLast();
                stack.removeLast();
                next.pollLast();
            }
        }
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

    @Override
    public boolean hasMoreElements() { return !next.isEmpty(); }

    @Override
    public List<Value> nextElement() {
        solution = new LinkedList<>(next);
        next.pollLast();
        searchNext();

        return solution;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Path not found");
            System.exit(1);
        }

        try {
            Parser parser = new Parser();
            Solver solver = parser.parse(new BufferedReader(new FileReader(args[0])));
            solver.backtrackingSearch();
            System.out.println(solver.explain());
        } catch (CompilerException e) {
            System.err.println("ERROR: File " + args[0] + " @ " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File " + args[0] + " not found.");
        } catch (NoSuchElementException e) {
            System.err.println("ERROR: No solution found.");
        }
    }
}

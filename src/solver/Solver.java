package solver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import compiler.*;

public class Solver {
    private final ArrayList<DSLSet> sets;

    public Solver(Collection<DSLSet> sets) { this.sets = new ArrayList<>(sets); }

    public List<Value> backtrackingSearch() {
        return backtrack(new LinkedList<>(), sets.stream()
                .map(DSLSet::getValues)
                .collect(Collectors.toCollection(LinkedList::new)));
    }

    private List<Value> backtrack(LinkedList<Value> solution,
            LinkedList<Set<Value>> solutionSets) {
        int step = solution.size();

        if (step == sets.size())
            return solution;

        loop: for (Value value: solutionSets.getFirst()) {
            for (int i = 0; i < step; i++)
                if (!sets.get(step).getRelationWith(sets.get(i))
                        .getRelatedValues(value).contains(solution.get(i)))
                    continue loop;

            LinkedList<Set<Value>> nextSolutionSets = new LinkedList<>();

            for (int i = step + 1; i < sets.size() ; i++) {
                nextSolutionSets.add(new HashSet<>(solutionSets.get(i - step)));
                nextSolutionSets.getLast().retainAll(sets.get(step)
                        .getRelationWith(sets.get(i)).getRelatedValues(value));

                if (nextSolutionSets.getLast().isEmpty())
                    continue loop;
            }

            solution.add(value);

            if (backtrack(solution, nextSolutionSets) != null)
                return solution;

            solution.removeLast();
        }

        return null;
    }

    public String explain(List<Value> solution) {
        if (solution == null || solution.size() != sets.size())
            return "Not a next.";

        StringBuilder message = new StringBuilder();

        for (int i = 0; i < sets.size(); i++) {
            Value lValue = solution.get(i);

            if (!sets.get(i).contains(lValue))
                return "Not a next.";

            message.append(lValue.getDomainName()).append(" = ")
                    .append(lValue).append(" explanation:\n");

            for (int j = 0; j < sets.size(); j++) {
                if (i == j)
                    continue;

                Relation relation = sets.get(i).getRelationWith(sets.get(j));
                Value rValue = solution.get(j);

                if (!relation.getRelatedValues(lValue).contains(rValue))
                    return "Not a next.";

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
            private LinkedList<LinkedList<Set<Value>>> stack = new LinkedList<>();
            private LinkedList<Iterator<Value>> iterators = new LinkedList<>();
            private LinkedList<Value> next = new LinkedList<>();

            {
                stack.add(sets.stream().map(DSLSet::getValues)
                        .collect(Collectors.toCollection(LinkedList::new)));
                iterators.add(stack.getLast().getFirst().iterator());
                backtrack();
            }

            private void pop() {
                iterators.removeLast();
                stack.removeLast();
                next.pollLast();
            }

            private void backtrack() {
                while (!stack.isEmpty() && !iterators.getLast().hasNext())
                    pop();

                if (stack.isEmpty())
                    return;

                int step = next.size();

                loop: while (iterators.getLast().hasNext()) {
                    Value value = iterators.getLast().next();
                    for (int i = 0; i < step; i++)
                        if (!sets.get(step).getRelationWith(sets.get(i))
                                .getRelatedValues(value).contains(next.get(i)))
                            continue loop;

                    LinkedList<Set<Value>> nextSolutionSets = new LinkedList<>();

                    for (int i = step + 1; i < sets.size() ; i++) {
                        nextSolutionSets.add(new HashSet<>(stack.getLast().get(i - step)));
                        nextSolutionSets.getLast().retainAll(sets.get(step)
                                .getRelationWith(sets.get(i)).getRelatedValues(value));

                        if (nextSolutionSets.getLast().isEmpty())
                            continue loop;
                    }

                    next.add(value);

                    if (nextSolutionSets.isEmpty())
                        return;

                    stack.add(nextSolutionSets);
                    iterators.add(nextSolutionSets.getFirst().iterator());
                    backtrack();

                    if (!next.isEmpty())
                        return;

                    pop();
                }

                backtrack();
            }

            @Override
            public boolean hasMoreElements() { return next != null; }

            @Override
            public List<Value> nextElement() {
                List<Value> solution = new LinkedList<>(next);
                next.removeLast();
                backtrack();

                return solution;
            }
        };
    }

    private Stream<List<Value>> streamBacktrack(LinkedList<Value> solution,
                                          LinkedList<Set<Value>> solutionSets) {
        int step = solution.size();
        LinkedList<Set<Value>> nextSolutionSets = new LinkedList<>();
        LinkedList<Value> nextSolution = new LinkedList<>(solution);
        nextSolution.add(null);

        return step == sets.size()? Stream.of(solution) : solutionSets.getFirst()
                .stream().filter(v -> {
                    for (int i = 0; i < step; i++)
                        if (!sets.get(step).getRelationWith(sets.get(i))
                                .getRelatedValues(v).contains(solution.get(i)))
                            return false;

                    nextSolutionSets.clear();

                    for (int i = step + 1; i < sets.size() ; i++) {
                        nextSolutionSets.add(new HashSet<>(solutionSets.get(i - step)));
                        nextSolutionSets.getLast().retainAll(sets.get(step)
                                .getRelationWith(sets.get(i)).getRelatedValues(v));

                        if (nextSolutionSets.getLast().isEmpty())
                            return false;
                    }

                    return true;
                }).flatMap(v -> {
                    nextSolution.set(step, v);

                    return streamBacktrack(nextSolution, nextSolutionSets);
                });
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
                System.out.println("No next found.");
            else
                System.out.println(solver.explain(solution));
        } catch (CompilerException e) {
            System.err.println("ERROR: File " + args[0] + " @ " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File " + args[0] + " not found.");
        }
    }
}

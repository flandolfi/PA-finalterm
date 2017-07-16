package solver;

import dsl.Domain;
import dsl.Value;

import java.util.*;

public class SolutionsEnumerator extends Solver
        implements Enumeration<List<Value>> {
    private LinkedList<LinkedList<Set<Value>>> stack = new LinkedList<>();
    private LinkedList<Iterator<Value>> iterators = new LinkedList<>();
    private LinkedList<Value> next = new LinkedList<>();

    public SolutionsEnumerator(Collection<Domain> sets) {
        super(sets);
        stack.add(universe);
        iterators.add(stack.getLast().getFirst().iterator());
        searchNext();
    }

    public SolutionsEnumerator(Solver solver) { this(solver.sets); }

    private void searchNext() {
        while (!stack.isEmpty()) {
            while (iterators.getLast().hasNext()) {
                Value value = iterators.getLast().next();
                LinkedList<Set<Value>> inferences =
                        infer(value, next, stack.getLast());

                if (inferences == null)
                    continue;

                if (inferences.isEmpty())
                    return;

                stack.add(inferences);
                iterators.add(inferences.getFirst().iterator());
            }

            while (!stack.isEmpty() && !iterators.getLast().hasNext()) {
                iterators.removeLast();
                stack.removeLast();
                next.pollLast();
            }
        }
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
}

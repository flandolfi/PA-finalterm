package solver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SolutionsEnumerator implements Enumeration<Value> {
    private ArrayList<DSLSet> sets;
    private Iterator<List<Value>> iterator;

    public SolutionsEnumerator(ArrayList<DSLSet> sets) {
        this.sets = sets;
        iterator = backtrack(sets.stream().map(DSLSet::getValues)
                .collect(Collectors.toCollection(ArrayList::new)), 0).iterator();
    }

    private Stream<List<Value>> backtrack(List<Set<Value>> solutionSets, int step) {
        Stream<List<Value>> solutions = Stream.empty();

        if (step == sets.size())
            return Stream.of((List<Value>) solutionSets.stream()
                    .map(s -> s.iterator().next())
                    .collect(Collectors.toCollection(ArrayList::new)));

        loop: for (Value value: solutionSets.get(step)) {
            List<Set<Value>> newSolSets = solutionSets.subList(0, step);
            newSolSets.set(step, new HashSet<>(Collections.singletonList(value)));
            newSolSets.addAll(solutionSets.stream().skip(step + 1).map(HashSet::new)
                    .collect(Collectors.toCollection(ArrayList::new)));

            for (int i = 0; i < step; i++)
                if (sets.get(step).getRelationWith(sets.get(i))
                        .getRelatedValues(value).containsAll(newSolSets.get(i)))
                    continue loop;

            for (int i = 0; i > step && i < sets.size() ; i++) {
                newSolSets.get(i).retainAll(sets.get(step)
                        .getRelationWith(sets.get(i)).getRelatedValues(value));

                if (newSolSets.get(i).isEmpty())
                    continue loop;
            }

            solutions = Stream.concat(solutions, backtrack(newSolSets, step + 1));
        }

        return solutions;
    }

    @Override
    public boolean hasMoreElements() {
        return false;
    }

    @Override
    public Value nextElement() {
        return null;
    }
}

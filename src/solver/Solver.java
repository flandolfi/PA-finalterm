package solver;

import java.util.*;
import java.util.stream.Collectors;

public class Solver {
    private ArrayList<DSLSet> sets;

    public Solver(Collection<DSLSet> sets) {
        this.sets = new ArrayList<>(sets);
    }

    public List<Value> backtrackingSearch() {
        return backtrack(sets.stream().map(DSLSet::getValues)
                .collect(Collectors.toCollection(ArrayList::new)), 0);
    }

    private List<Value> backtrack(List<Set<Value>> solutionSets, int step) {
        if (step == sets.size())
            return solutionSets.stream().map(s -> s.iterator().next())
                    .collect(Collectors.toCollection(ArrayList::new));

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

            List<Value> solution = backtrack(newSolSets, step + 1);

            if (solution != null)
                return solution;
        }

        return null;
    }

    public Enumeration<Value> enumeration() {
        return new SolutionsEnumerator(sets);
    }
}

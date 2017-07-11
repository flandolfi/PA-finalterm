package solver;

import java.util.*;
import java.util.stream.Collectors;

public class CSPGraph {
    private ArrayList<DSLSet> sets;

    public CSPGraph(Collection<DSLSet> sets) {
        this.sets = new ArrayList<>(sets);
    }

    public ArrayList<Value> backtrackingSearch() {
        return backtrack(sets.stream().map(DSLSet::getValues)
                .collect(Collectors.toCollection(ArrayList::new)), 0);
    }

    private ArrayList<Value> backtrack(
            ArrayList<HashSet<Value>> solutionSets, int step) {
        if (step == sets.size())
            return solutionSets.stream().map(s -> s.iterator().next())
                    .collect(Collectors.toCollection(ArrayList::new));

        loop: for (Value value: solutionSets.get(step)) {
            ArrayList<HashSet<Value>> newSolSets = solutionSets.stream()
                    .map(HashSet<Value>::new)
                    .collect(Collectors.toCollection(ArrayList::new));
            newSolSets.set(step, new HashSet<>(Collections.singletonList(value)));

            for (int i = 0; i < sets.size(); i++) {
                if (i == step)
                    continue;

                newSolSets.get(i).retainAll(sets.get(step)
                        .getRelationWith(sets.get(i)).getRelatedValues(value));

                if (newSolSets.get(i).isEmpty())
                    continue loop;
            }

            ArrayList<Value> solution = backtrack(newSolSets, step + 1);

            if (solution != null)
                return solution;
        }

        return null;
    }

    public Enumeration<Value> solutionsEnumerator() {
        return new Enumeration<Value>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public Value nextElement() {
                return null;
            }
        };
    }
}

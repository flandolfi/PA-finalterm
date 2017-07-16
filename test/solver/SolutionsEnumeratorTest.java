package solver;

import dsl.Value;
import compiler.Parser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class SolutionsEnumeratorTest {
    @Test
    void enumeration() {
        try {
            Parser parser = new Parser();
            SolutionsEnumerator solver = new SolutionsEnumerator(
                    parser.parse(new BufferedReader(
                    new FileReader("./test/samples/Sudoku.csp"))));

            while(solver.hasMoreElements())
                System.out.println(solver.nextElement());
        } catch (Exception e) {
            fail("You shouldn't be here! (op.cit.)");
        }
    }

    @Test
    void backtrackingSearch() {
        try {
            Parser parser = new Parser();
            SolutionsEnumerator solver = new SolutionsEnumerator(
                    parser.parse(new BufferedReader(
                            new FileReader("./test/samples/Sudoku.csp"))));
            final List<Value> fst, snd = new LinkedList<>(), next = new LinkedList<>();
            List<List<Value>> solutions = new LinkedList<>();

            fst = solver.backtrackingSearch();
            assertNotNull(fst);

            for (int i = 0; i < 18; i++) {
                assertTrue(solver.hasMoreElements());
                next.clear();
                snd.clear();
                next.addAll(solver.nextElement());
                snd.addAll(solver.backtrackingSearch());
                assertIterableEquals(fst, snd);
                solutions.forEach(s -> assertFalse(s.containsAll(next)));
                solutions.add(new LinkedList<>(next));
            }

            assertFalse(solver.hasMoreElements());
        } catch (Exception e) {
            fail("You shouldn't be here! (op.cit.)");
        }
    }
}
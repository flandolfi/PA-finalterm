package solver;

import dsl.*;
import compiler.*;
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
    void enumerateSingle() {
        try {
            Parser parser = new Parser();
            SolutionsEnumerator solver = new SolutionsEnumerator(
                    parser.parse(new BufferedReader(
                            new FileReader("./test/samples/Single.csp"))));

            while(solver.hasMoreElements())
                System.out.println(solver.nextElement());
        } catch (Exception e) {
            fail("You shouldn't be here! (op.cit.)");
        }
    }

    @Test
    void enumerateAll() {
        try {
            Parser parser = new Parser();
            SolutionsEnumerator solver = new SolutionsEnumerator(
                    parser.parse(new BufferedReader(
                            new FileReader("./test/samples/Empty.csp"))));
            int i;

            for (i = 0; solver.hasMoreElements(); i++)
                solver.nextElement();

            System.out.println("Solutions found: " + i);
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
            String explanation = solver.explain();

            for (int i = 0; i < 18; i++) {
                assertTrue(solver.hasMoreElements());
                next.clear();
                snd.clear();
                snd.addAll(solver.backtrackingSearch());
                next.addAll(solver.nextElement());
                assertIterableEquals(fst, snd);
                solutions.forEach(s -> assertFalse(s.containsAll(next)));
                solutions.add(new LinkedList<>(next));
            }

            assertFalse(solver.hasMoreElements());
            assertFalse(explanation.equals(solver.explain()));
            solver.backtrackingSearch();
            assertTrue(explanation.equals(solver.explain()));
        } catch (Exception e) {
            fail("You shouldn't be here! (op.cit.)");
        }
    }
}
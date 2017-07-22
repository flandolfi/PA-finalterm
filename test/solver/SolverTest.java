package solver;

import dsl.Solver;
import dsl.Value;
import compiler.Parser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {
    @Test
    void backtrackingSearch() {
        try {
            Parser parser = new Parser();
            Solver solver = new Solver(
                    parser.parse(new BufferedReader(
                            new FileReader("./test/samples/Sudoku.csp"))));
            List<Value> fst, snd;

            fst = solver.backtrackingSearch();
            snd = solver.backtrackingSearch();

            assertIterableEquals(fst, snd);
        } catch (Exception e) {
            fail("You shouldn't be here! (op.cit.)");
        }
    }
}
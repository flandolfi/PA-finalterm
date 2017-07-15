package solver;

import compiler.Parser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

class SolverTest {
    @Test
    void enumeration() {
        try {
            Parser parser = new Parser();
            Scanner scanner = new Scanner(System.in);
            Solver solver = parser.parse(new BufferedReader(
                    new FileReader("./test/samples/Sudoku.csp")));

            while(solver.hasMoreElements())
                System.out.println(solver.nextElement());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package compiler;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

public class Scanner {
    private StreamTokenizer input;

    public Scanner(Reader reader) {
        input = new StreamTokenizer(reader);
        input.resetSyntax();
        input.eolIsSignificant(false);
        input.wordChars('A', 'Z');
        input.wordChars('a', 'z');
        input.ordinaryChar('=');
        input.ordinaryChar('{');
        input.ordinaryChar('}');
        input.ordinaryChar(',');
        input.ordinaryChar('(');
        input.ordinaryChar(')');
        input.ordinaryChar('!');
        input.whitespaceChars(0x00, 0x20);
        input.parseNumbers();
    }

    public Type getToken() throws CompilerException {
        try {
            switch (input.nextToken()) {
                case StreamTokenizer.TT_EOF: return Type.EOF;
                case StreamTokenizer.TT_WORD: return Type.WORD;
                case '=': return Type.EQ;
                case '{': return Type.O_BR;
                case '}': return Type.C_BR;
                case '(': return Type.O_PAR;
                case ')': return Type.C_PAR;
                case ',': return Type.COMMA;
                case '!': return Type.BANG;
                default:
                    throw new CompilerException(printLineNo() +
                            "Unrecognized token: '" + input.ttype + "'");
            }
        } catch (IOException e) {
            return Type.EOF;
        }
    }

    public Type peek() throws CompilerException {
        Type next = getToken();
        input.pushBack();

        return next;
    }

    public String getTokenValue() { return input.sval; }
    public String printLineNo() { return "Line " + input.lineno() + ": "; }
}

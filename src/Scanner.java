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
//        input.whitespaceChars(' ', ' ');
        input.ordinaryChar('=');
        input.ordinaryChar('{');
        input.ordinaryChar('}');
        input.ordinaryChar(',');
        input.ordinaryChar('(');
        input.ordinaryChar(')');
        input.ordinaryChar('!');
    }

    public Type getToken() throws LexicalException {
        try {
            switch (input.nextToken()) {
                case StreamTokenizer.TT_EOF: return Type.EOF;
                case StreamTokenizer.TT_WORD: return Type.WORD;
                case StreamTokenizer.TT_NUMBER: return Type.NUM;
                case '=': return Type.EQ;
                case '{': return Type.O_BR;
                case '}': return Type.C_BR;
                case '(': return Type.O_PAR;
                case ')': return Type.C_PAR;
                case ',': return Type.COMMA;
                case '!': return Type.BANG;
                default:
                    throw new LexicalException("Line " + input.lineno() +
                            ": Unrecognized token: '" + input.sval + "'");
            }
        } catch (IOException e) {
            return Type.EOF;
        }
    }

    public Type peek() throws LexicalException {
        Type next = getToken();
        input.pushBack();

        return next;
    }

    public String getTokenValue() { return input.sval; }
    public int getLineNo() { return input.lineno(); }
}

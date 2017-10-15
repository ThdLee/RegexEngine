import dfa.DFAConstructor;
import nfa.Lexer;
import nfa.NFAConstructor;

public class RegexEngine {

    private static final RegexEngine instance = new RegexEngine();

    public static RegexEngine getInstance() {
        return instance;
    }

    private Lexer lexer;
    private NFAConstructor nfa;
    private DFAConstructor dfa;

    private RegexEngine() {
        lexer = new Lexer();
        nfa = new NFAConstructor(lexer);
        dfa = new DFAConstructor(nfa);
    }

    public Pattern createPattern(String regex) {
        Pattern pattern = new Pattern(dfa.constructDFA(regex));

        pattern.setPrefix(lexer.isPrefix());
        pattern.setSuffix(lexer.isSuffix());

        return pattern;
    }
}

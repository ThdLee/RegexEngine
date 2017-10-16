import dfa.DFAConstructor;
import nfa.Lexer;
import nfa.NFAConstructor;

public class RegexEngine {


    private Lexer lexer;
    private NFAConstructor nfa;
    private DFAConstructor dfa;

    public RegexEngine() {
        lexer = new Lexer();
        nfa = new NFAConstructor(lexer);
        dfa = new DFAConstructor(nfa);
    }

    /**
     * @param regex The expression to be compiled
     * @return a Pattern for regular expression
     */
    public Pattern createPattern(String regex) {
        Pattern pattern = new Pattern(dfa.constructDFA(regex));

        pattern.setPrefix(lexer.isPrefix());
        pattern.setSuffix(lexer.isSuffix());

        return pattern;
    }
}

package com.regex;

import com.regex.dfa.DFAConstructor;
import com.regex.analysis.Lexer;
import com.regex.nfa.NFAConstructor;

public class RegexEngine {

    private static final Lexer lexer;
    private static final NFAConstructor nfa;
    private static final DFAConstructor dfa;

    static  {
        lexer = new Lexer();
        nfa = new NFAConstructor(lexer);
        dfa = new DFAConstructor(nfa);
    }

    /**
     * @param regex The expression to be compiled
     * @return a com.regex.Pattern for regular expression
     */
    public static Pattern createPattern(String regex) {
        Pattern pattern = new Pattern(dfa.constructDFA(regex));

        pattern.setPrefix(lexer.isPrefix());
        pattern.setSuffix(lexer.isSuffix());

        return pattern;
    }
}
